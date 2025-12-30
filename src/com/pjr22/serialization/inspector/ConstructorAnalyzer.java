package com.pjr22.serialization.inspector;

import java.lang.reflect.Constructor;
import java.lang.reflect.Parameter;
import java.util.Set;

/**
 * Analyzes and selects the best constructor for deserialization.
 * <p>
 * The selection algorithm follows these rules:
 * <ul>
 *   <li>Prefer constructors with more parameters</li>
 *   <li>Match parameter names to field names</li>
 *   <li>Handle type conversions (e.g., int to long)</li>
 *   <li>When multiple constructors with same parameter count exist,
 *       use the constructor with the most matching field names</li>
 *   <li>Choose first available in case of tie</li>
 * </ul>
 */
public class ConstructorAnalyzer {

    /**
     * Selects the best constructor for deserialization based on field names.
     *
     * @param clazz      the class to analyze
     * @param fieldNames the set of field names to match against constructor parameters
     * @return the best constructor, or null if no suitable constructor is found
     */
    public static Constructor<?> selectBestConstructor(Class<?> clazz, Set<String> fieldNames) {
        if (clazz == null) {
            return null;
        }

        Constructor<?>[] constructors = clazz.getDeclaredConstructors();
        if (constructors.length == 0) {
            return null;
        }

        // If no field names provided, prefer default constructor
        if (fieldNames == null || fieldNames.isEmpty()) {
            for (Constructor<?> constructor : constructors) {
                if (constructor.getParameterCount() == 0) {
                    return constructor;
                }
            }
            // Fall back to first constructor if no default constructor exists
            return constructors[0];
        }

        // Find constructor with most matching parameter names
        // If multiple constructors have same match count, prefer more parameters
        Constructor<?> bestConstructor = null;
        int bestMatchCount = -1;
        int bestParamCount = -1;

        for (Constructor<?> constructor : constructors) {
            int paramCount = constructor.getParameterCount();
            int matchCount = countMatchingParameterNames(constructor, fieldNames);

            // Prefer constructors with more matching parameter names
            // If same match count, prefer constructors with more parameters
            if (matchCount > bestMatchCount ||
                (matchCount == bestMatchCount && paramCount > bestParamCount)) {
                bestConstructor = constructor;
                bestMatchCount = matchCount;
                bestParamCount = paramCount;
            }
        }

        return bestConstructor;
    }

    /**
     * Counts how many constructor parameter names match the given field names.
     *
     * @param constructor the constructor to analyze
     * @param fieldNames  the set of field names to match against
     * @return the count of matching parameter names
     */
    public static int countMatchingParameterNames(Constructor<?> constructor, Set<String> fieldNames) {
        if (constructor == null || fieldNames == null || fieldNames.isEmpty()) {
            return 0;
        }

        int matchCount = 0;
        Parameter[] parameters = constructor.getParameters();
        
        for (Parameter parameter : parameters) {
            String paramName = parameter.getName();
            if (paramName != null && fieldNames.contains(paramName)) {
                matchCount++;
            }
        }
        
        return matchCount;
    }

    /**
     * Checks if a constructor parameter type is compatible with a field type.
     * <p>
     * This allows for type conversions such as:
     * <ul>
     *   <li>int to long</li>
     *   <li>float to double</li>
     *   <li>widening primitive conversions</li>
     *   <li>boxing/unboxing conversions</li>
     * </ul>
     *
     * @param paramType the constructor parameter type
     * @param fieldType the field type
     * @return true if the types are compatible, false otherwise
     */
    public static boolean isTypeCompatible(Class<?> paramType, Class<?> fieldType) {
        if (paramType == null || fieldType == null) {
            return false;
        }

        // Same type
        if (paramType.equals(fieldType)) {
            return true;
        }

        // Primitive to primitive widening
        if (paramType.isPrimitive() && fieldType.isPrimitive()) {
            return isPrimitiveWidening(paramType, fieldType);
        }

        // Primitive to wrapper (boxing)
        if (paramType.isPrimitive() && !fieldType.isPrimitive()) {
            Class<?> paramWrapper = getWrapperClass(paramType);
            return paramWrapper != null && paramWrapper.equals(fieldType);
        }

        // Wrapper to primitive (unboxing)
        if (!paramType.isPrimitive() && fieldType.isPrimitive()) {
            Class<?> fieldWrapper = getWrapperClass(fieldType);
            return fieldWrapper != null && paramType.equals(fieldWrapper);
        }

        // Number type compatibility (e.g., Integer to Long)
        if (Number.class.isAssignableFrom(paramType) && Number.class.isAssignableFrom(fieldType)) {
            // Allow conversions between number types
            return true;
        }

        return false;
    }

    /**
     * Checks if a primitive type can be widened to another primitive type.
     *
     * @param from the source primitive type
     * @param to   the target primitive type
     * @return true if widening is possible, false otherwise
     */
    private static boolean isPrimitiveWidening(Class<?> from, Class<?> to) {
        if (from.equals(to)) {
            return true;
        }

        // byte -> short, int, long, float, double
        if (from.equals(byte.class)) {
            return to.equals(short.class) || to.equals(int.class) ||
                   to.equals(long.class) || to.equals(float.class) ||
                   to.equals(double.class);
        }

        // short -> int, long, float, double
        if (from.equals(short.class)) {
            return to.equals(int.class) || to.equals(long.class) ||
                   to.equals(float.class) || to.equals(double.class);
        }

        // char -> int, long, float, double
        if (from.equals(char.class)) {
            return to.equals(int.class) || to.equals(long.class) ||
                   to.equals(float.class) || to.equals(double.class);
        }

        // int -> long, float, double
        if (from.equals(int.class)) {
            return to.equals(long.class) || to.equals(float.class) ||
                   to.equals(double.class);
        }

        // long -> float, double
        if (from.equals(long.class)) {
            return to.equals(float.class) || to.equals(double.class);
        }

        // float -> double
        if (from.equals(float.class)) {
            return to.equals(double.class);
        }

        return false;
    }

    /**
     * Gets the wrapper class for a primitive type.
     *
     * @param primitiveType the primitive type
     * @return the corresponding wrapper class, or null if not a primitive
     */
    private static Class<?> getWrapperClass(Class<?> primitiveType) {
        if (primitiveType.equals(byte.class)) {
            return Byte.class;
        } else if (primitiveType.equals(short.class)) {
            return Short.class;
        } else if (primitiveType.equals(int.class)) {
            return Integer.class;
        } else if (primitiveType.equals(long.class)) {
            return Long.class;
        } else if (primitiveType.equals(float.class)) {
            return Float.class;
        } else if (primitiveType.equals(double.class)) {
            return Double.class;
        } else if (primitiveType.equals(char.class)) {
            return Character.class;
        } else if (primitiveType.equals(boolean.class)) {
            return Boolean.class;
        }
        return null;
    }
}
