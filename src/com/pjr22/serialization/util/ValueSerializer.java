package com.pjr22.serialization.util;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Random;

/**
 * Utility class for serializing and deserializing JDK classes
 * that can be constructed with a single value (String or Number).
 * This provides a generic mechanism to handle classes like UUID, Date, Random, etc.
 * without requiring special cases for each type.
 */
public class ValueSerializer {

    /**
     * Checks if a class can be serialized as a simple value.
     * A class is considered serializable as a simple value if it:
     * 1. Is a JDK class (java.* package)
     * 2. Has a toString() method that produces a reconstructible value
     * 3. Has either a fromString(String) method or a single-parameter constructor
     *    that can reliably construct a new instance from the value
     *
     * @param clazz the class to check
     * @return true if the class can be serialized as a simple value
     */
    public static boolean canSerializeAsValue(Class<?> clazz) {
        // Only handle JDK classes
        if (!isJdkClass(clazz)) {
            return false;
        }

        // Exclude String - it's already handled by JsonSerializer
        if (clazz == String.class) {
            return false;
        }

        // Exclude Collection types - they should be serialized as JSON arrays, not as simple values
        // This prevents ArrayList, HashSet, etc. from being incorrectly serialized as Strings
        if (Collection.class.isAssignableFrom(clazz)) {
            return false;
        }

        // Exclude Map types - they should be serialized as JSON objects, not as simple values
        // This prevents LinkedHashMap, HashMap, etc. from being incorrectly serialized as Strings
        if (Map.class.isAssignableFrom(clazz)) {
            return false;
        }

        // Check for fromString(String) method or single-parameter constructor
        return hasFromStringMethod(clazz) || hasSingleParameterConstructor(clazz);
    }

    /**
     * Checks if a class is a JDK class (java.* or javax.* package).
     */
    private static boolean isJdkClass(Class<?> clazz) {
        String className = clazz.getName();
        return className.startsWith("java.") || className.startsWith("javax.");
    }

    /**
     * Checks if a class has a fromString(String) static method.
     */
    private static boolean hasFromStringMethod(Class<?> clazz) {
        try {
            // Try getMethod first (public methods)
            Method fromString = clazz.getMethod("fromString", String.class);
            return fromString != null;
        } catch (NoSuchMethodException e) {
            // Try getDeclaredMethod (all methods including package-private)
            try {
                Method fromString = clazz.getDeclaredMethod("fromString", String.class);
                return fromString != null;
            } catch (NoSuchMethodException e2) {
                return false;
            }
        }
    }

    /**
     * Checks if a class has a single-parameter constructor accepting String or Number.
     */
    private static boolean hasSingleParameterConstructor(Class<?> clazz) {
        // Check public constructors first
        Constructor<?>[] constructors = clazz.getConstructors();
        for (Constructor<?> constructor : constructors) {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            if (paramTypes.length == 1) {
                Class<?> paramType = paramTypes[0];
                if (paramType == String.class || isNumberType(paramType)) {
                    return true;
                }
            }
        }
        // Also check declared constructors (includes private ones)
        try {
            Constructor<?>[] declaredConstructors = clazz.getDeclaredConstructors();
            for (Constructor<?> constructor : declaredConstructors) {
                Class<?>[] paramTypes = constructor.getParameterTypes();
                if (paramTypes.length == 1) {
                    Class<?> paramType = paramTypes[0];
                    if (paramType == String.class || isNumberType(paramType)) {
                        return true;
                    }
                }
            }
        } catch (Exception e) {
            // Ignore exceptions
        }
        return false;
    }

    /**
     * Checks if a type is a number type (primitive or wrapper).
     */
    private static boolean isNumberType(Class<?> type) {
        return type == byte.class || type == Byte.class ||
               type == short.class || type == Short.class ||
               type == int.class || type == Integer.class ||
               type == long.class || type == Long.class ||
               type == float.class || type == Float.class ||
               type == double.class || type == Double.class ||
               Number.class.isAssignableFrom(type);
    }

    /**
     * Serializes an object to a simple value (String or Number).
     * Returns null if the object cannot be serialized as a simple value.
     *
     * @param obj the object to serialize
     * @return the serialized value (String, Number, or null)
     */
    public static Object serializeAsValue(Object obj) {
        if (obj == null) {
            return null;
        }

        Class<?> clazz = obj.getClass();

        if (!canSerializeAsValue(clazz)) {
            return null;
        }

        // For Date, serialize as ISO 8601 string with millisecond precision
        if (obj instanceof Date) {
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            return sdf.format((Date) obj);
        }

        // For Random, serialize as seed (long)
        if (obj instanceof Random) {
            try {
                // In Java 9+, the seed field is private final in the java.util module,
                // which is not accessible by default. Use nextLong() instead.
                Random random = (Random) obj;
                // Call nextLong() to get a reproducible value
                long value = random.nextLong();
                return value;
            } catch (Exception e) {
                // Fallback: return 0
                return 0L;
            }
        }

        // For UUID and other classes, use toString() directly (no JSON serialization needed)
        return obj.toString();
    }

    /**
     * Deserializes a simple value to an object of the specified class.
     * Returns null if deserialization is not possible.
     *
     * @param value the value to deserialize (String, Number, or null)
     * @param targetClass the target class to deserialize to
     * @return the deserialized object, or null if not possible
     */
    @SuppressWarnings("unchecked")
    public static <T> T deserializeFromValue(Object value, Class<T> targetClass) {
        if (value == null) {
            return null;
        }

        if (!canSerializeAsValue(targetClass)) {
            return null;
        }

        // Special handling for Date - deserialize from ISO 8601 string
        if (targetClass == Date.class) {
            try {
                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                return (T) sdf.parse(value.toString());
            } catch (Exception e) {
                // Return null if parsing fails
                return null;
            }
        }

        // Try fromString(String) method first
        try {
            Method fromString = targetClass.getMethod("fromString", String.class);
            String stringValue = value.toString();
            return (T) fromString.invoke(null, stringValue);
        } catch (Exception e) {
            // Continue to try constructor
        }

        // Try single-parameter constructor
        try {
            Constructor<?>[] constructors = targetClass.getConstructors();
            for (Constructor<?> constructor : constructors) {
                Class<?>[] paramTypes = constructor.getParameterTypes();
                if (paramTypes.length == 1) {
                    Class<?> paramType = paramTypes[0];
                    if (paramType == String.class) {
                        return (T) constructor.newInstance(value.toString());
                    } else if (paramType == long.class || paramType == Long.class) {
                        if (value instanceof Number) {
                            // For primitive long, need to wrap in Long
                            if (paramType == long.class) {
                                return (T) constructor.newInstance(((Number) value).longValue());
                            } else {
                                return (T) constructor.newInstance(((Number) value).longValue());
                            }
                        }
                    } else if (paramType == int.class || paramType == Integer.class) {
                        if (value instanceof Number) {
                            return (T) constructor.newInstance(((Number) value).intValue());
                        }
                    } else if (Number.class.isAssignableFrom(paramType)) {
                        if (value instanceof Number) {
                            return (T) constructor.newInstance(((Number) value).longValue());
                        }
                    }
                }
            }
        } catch (Exception e) {
            // Return null if deserialization fails
        }

        return null;
    }
}
