package com.pjr22.serialization.core;

import com.pjr22.serialization.format.JsonParser;
import com.pjr22.serialization.inspector.ConstructorAnalyzer;
import com.pjr22.serialization.inspector.FieldInspector;
import com.pjr22.serialization.registry.ObjectRegistry;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Deserializes Java objects from JSON format.
 * Handles object references, circular references, and complex object graphs.
 */
public class Deserializer<T> {

    private final Class<T> targetType;
    private final ObjectRegistry objectRegistry;
    private final List<String> warnings;

    /**
     * Creates a new Deserializer for the specified target type.
     *
     * @param targetType the class to deserialize to
     */
    public Deserializer(Class<T> targetType) {
        this.targetType = targetType;
        this.objectRegistry = new ObjectRegistry();
        this.warnings = new ArrayList<>();
    }

    /**
     * Deserializes an object from the input stream.
     *
     * @param inputStream the input stream to read from
     * @return the deserialized object
     * @throws SerializationException if a deserialization error occurs
     */
    public T deserialize(InputStream inputStream) throws SerializationException {
        try (InputStreamReader reader = new InputStreamReader(inputStream, StandardCharsets.UTF_8)) {
            StringBuilder sb = new StringBuilder();
            char[] buffer = new char[8192];
            int charsRead;
            while ((charsRead = reader.read(buffer)) != -1) {
                sb.append(buffer, 0, charsRead);
            }
            String json = sb.toString();
            return deserialize(json);
        } catch (IOException e) {
            throw new SerializationException("Error reading from input stream", e);
        }
    }

    /**
     * Deserializes an object from a JSON string.
     *
     * @param json the JSON string to deserialize from
     * @return the deserialized object
     * @throws SerializationException if a deserialization error occurs
     */
    @SuppressWarnings("unchecked")
    private T deserialize(String json) throws SerializationException {
        try {
            Object parsed = JsonParser.parse(json);

            // Check if this is the "objects" array format (from DeserializerTest)
            if (parsed instanceof Map) {
                Map<String, Object> rootMap = (Map<String, Object>) parsed;
                if (rootMap.containsKey("objects")) {
                    return deserializeObjectsArrayFormat(rootMap);
                }
            }

            // Otherwise, it's the nested format from Serializer
            return (T) deserializeObject(parsed);

        } catch (Exception e) {
            throw new SerializationException("Error deserializing object", e);
        }
    }

    /**
     * Deserializes from the "objects" array format used by DeserializerTest.
     */
    @SuppressWarnings("unchecked")
    private T deserializeObjectsArrayFormat(Map<String, Object> rootMap) throws SerializationException {
        List<Map<String, Object>> objectsList = (List<Map<String, Object>>) rootMap.get("objects");

        // First pass: create all objects and register them
        for (Map<String, Object> objData : objectsList) {
            String id = (String) objData.get("id");
            String className = (String) objData.get("className");
            Map<String, Object> fields = (Map<String, Object>) objData.get("fields");
            Map<String, String> references = (Map<String, String>) objData.getOrDefault("references", new HashMap<>());

            try {
                Class<?> clazz = Class.forName(className);
                Object instance = createInstance(clazz, fields.keySet());
                objectRegistry.register(id, instance);

                // Set field values
                setFields(instance, clazz, fields, references);

            } catch (ClassNotFoundException e) {
                throw new SerializationException("Class not found: " + className, e);
            }
        }

        // Second pass: resolve references
        for (Map<String, Object> objData : objectsList) {
            String id = (String) objData.get("id");
            Map<String, String> references = (Map<String, String>) objData.getOrDefault("references", new HashMap<>());

            Object instance = objectRegistry.get(id);
            if (instance != null && !references.isEmpty()) {
                resolveReferences(instance, references);
            }
        }

        // Return the first object that matches the target type
        for (Map<String, Object> objData : objectsList) {
            String className = (String) objData.get("className");
            if (className.equals(targetType.getName())) {
                String id = (String) objData.get("id");
                return (T) objectRegistry.get(id);
            }
        }

        // If no exact match, try to find the first object
        if (!objectsList.isEmpty()) {
            String id = (String) objectsList.get(0).get("id");
            return (T) objectRegistry.get(id);
        }

        throw new SerializationException("No objects found in JSON");
    }

    /**
     * Deserializes an object from parsed JSON data (nested format from Serializer).
     */
    @SuppressWarnings("unchecked")
    private Object deserializeObject(Object parsed) throws SerializationException {
        if (parsed == null) {
            return null;
        }

        if (parsed instanceof Map) {
            Map<String, Object> map = (Map<String, Object>) parsed;

            // Check if this is a reference
            if (map.containsKey("$ref")) {
                String refId = (String) map.get("$ref");
                Object referenced = objectRegistry.get(refId);
                if (referenced == null) {
                    throw new SerializationException("Referenced object not found: " + refId);
                }
                return referenced;
            }

            // Check if this is an object definition
            if (map.containsKey("$id") && map.containsKey("$class")) {
                String objectId = (String) map.get("$id");
                String className = (String) map.get("$class");
                Map<String, Object> fields = (Map<String, Object>) map.get("fields");

                // Check if already deserialized
                Object existing = objectRegistry.get(objectId);
                if (existing != null) {
                    return existing;
                }

                try {
                    Class<?> clazz = Class.forName(className);

                    // Check serialVersionUID
                    if (map.containsKey("serialVersionUID")) {
                        checkSerialVersionUID(clazz, map.get("serialVersionUID"));
                    }

                    // Create instance
                    Object instance = createInstance(clazz, fields.keySet());
                    objectRegistry.register(objectId, instance);

                    // Set field values
                    setFields(instance, clazz, fields, new HashMap<>());

                    return instance;

                } catch (ClassNotFoundException e) {
                    throw new SerializationException("Class not found: " + className, e);
                }
            }

            // Regular map - return as is
            return map;
        }

        if (parsed instanceof List) {
            return parsed;
        }

        // Primitive types
        return parsed;
    }

    /**
     * Creates an instance of the specified class.
     */
    private Object createInstance(Class<?> clazz, Set<String> fieldNames) throws SerializationException {
        try {
            // Try to find a constructor with matching parameter names
            Constructor<?> constructor = ConstructorAnalyzer.selectBestConstructor(clazz, fieldNames);

            if (constructor != null && constructor.getParameterCount() > 0) {
                // Use parameterized constructor
                return createWithConstructor(constructor, fieldNames);
            }

            // Try default constructor
            try {
                return clazz.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                // No default constructor - try to use any public constructor
                Constructor<?>[] constructors = clazz.getConstructors();
                if (constructors.length > 0) {
                    return constructors[0].newInstance(getDefaultValues(constructors[0].getParameterTypes()));
                }
                throw new SerializationException("No suitable constructor found for class: " + clazz.getName());
            }

        } catch (Exception e) {
            throw new SerializationException("Error creating instance of class: " + clazz.getName(), e);
        }
    }

    /**
     * Creates an instance using a parameterized constructor.
     */
    private Object createWithConstructor(Constructor<?> constructor, Set<String> fieldNames) throws SerializationException {
        try {
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] args = new Object[paramTypes.length];

            for (int i = 0; i < paramTypes.length; i++) {
                // For now, use default values - actual values will be set via fields
                args[i] = getDefaultValue(paramTypes[i]);
            }

            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new SerializationException("Error creating instance with constructor", e);
        }
    }

    /**
     * Gets default values for constructor parameters.
     */
    private Object[] getDefaultValues(Class<?>[] paramTypes) {
        Object[] values = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            values[i] = getDefaultValue(paramTypes[i]);
        }
        return values;
    }

    /**
     * Gets a default value for a given type.
     */
    private Object getDefaultValue(Class<?> type) {
        if (type == boolean.class) return false;
        if (type == byte.class) return (byte) 0;
        if (type == short.class) return (short) 0;
        if (type == int.class) return 0;
        if (type == long.class) return 0L;
        if (type == float.class) return 0.0f;
        if (type == double.class) return 0.0;
        if (type == char.class) return '\0';
        return null;
    }

    /**
     * Sets field values on an instance.
     */
    private void setFields(Object instance, Class<?> clazz, Map<String, Object> fields, Map<String, String> references) throws SerializationException {
        Field[] allFields = FieldInspector.getAllFields(clazz);

        for (Field field : allFields) {
            String fieldName = field.getName();
            field.setAccessible(true);

            try {
                if (fields.containsKey(fieldName)) {
                    Object value = fields.get(fieldName);
                    setFieldValue(instance, field, value);
                }
            } catch (IllegalAccessException e) {
                // Try setter method
                trySetterMethod(instance, field, fields.get(fieldName));
            }
        }
    }

    /**
     * Sets a field value on an instance.
     */
    private void setFieldValue(Object instance, Field field, Object value) throws IllegalAccessException, SerializationException {
        Class<?> fieldType = field.getType();

        if (value == null) {
            field.set(instance, null);
            return;
        }

        // Handle primitive types
        if (fieldType == boolean.class || fieldType == Boolean.class) {
            field.set(instance, convertToBoolean(value));
        } else if (fieldType == byte.class || fieldType == Byte.class) {
            field.set(instance, convertToByte(value));
        } else if (fieldType == short.class || fieldType == Short.class) {
            field.set(instance, convertToShort(value));
        } else if (fieldType == int.class || fieldType == Integer.class) {
            field.set(instance, convertToInt(value));
        } else if (fieldType == long.class || fieldType == Long.class) {
            field.set(instance, convertToLong(value));
        } else if (fieldType == float.class || fieldType == Float.class) {
            field.set(instance, convertToFloat(value));
        } else if (fieldType == double.class || fieldType == Double.class) {
            field.set(instance, convertToDouble(value));
        } else if (fieldType == char.class || fieldType == Character.class) {
            field.set(instance, convertToChar(value));
        } else if (fieldType == String.class) {
            field.set(instance, value.toString());
        } else if (fieldType == BigDecimal.class) {
            if (value instanceof Number) {
                field.set(instance, new BigDecimal(value.toString()));
            } else {
                field.set(instance, new BigDecimal(value.toString()));
            }
        } else if (fieldType.isEnum()) {
            field.set(instance, convertToEnum(fieldType, value));
        } else if (fieldType.isArray()) {
            field.set(instance, convertToArray(fieldType, value));
        } else if (Collection.class.isAssignableFrom(fieldType)) {
            field.set(instance, convertToCollection(fieldType, value));
        } else if (Map.class.isAssignableFrom(fieldType)) {
            field.set(instance, convertToMap(fieldType, value));
        } else if (value instanceof Map) {
            // This might be a nested object
            Object nested = deserializeObject(value);
            field.set(instance, nested);
        } else {
            field.set(instance, value);
        }
    }

    /**
     * Tries to set a field value using a setter method.
     */
    private void trySetterMethod(Object instance, Field field, Object value) {
        try {
            String setterName = "set" + Character.toUpperCase(field.getName().charAt(0)) + field.getName().substring(1);
            Method[] methods = instance.getClass().getMethods();

            for (Method method : methods) {
                if (method.getName().equals(setterName) && method.getParameterCount() == 1) {
                    Class<?> paramType = method.getParameterTypes()[0];
                    Object convertedValue = convertValue(value, paramType);
                    method.invoke(instance, convertedValue);
                    return;
                }
            }
        } catch (Exception e) {
            // Ignore - will be handled by reflection fallback
        }
    }

    /**
     * Converts a value to the specified type.
     */
    private Object convertValue(Object value, Class<?> targetType) {
        if (value == null) return null;

        if (targetType == boolean.class || targetType == Boolean.class) {
            return convertToBoolean(value);
        } else if (targetType == byte.class || targetType == Byte.class) {
            return convertToByte(value);
        } else if (targetType == short.class || targetType == Short.class) {
            return convertToShort(value);
        } else if (targetType == int.class || targetType == Integer.class) {
            return convertToInt(value);
        } else if (targetType == long.class || targetType == Long.class) {
            return convertToLong(value);
        } else if (targetType == float.class || targetType == Float.class) {
            return convertToFloat(value);
        } else if (targetType == double.class || targetType == Double.class) {
            return convertToDouble(value);
        } else if (targetType == char.class || targetType == Character.class) {
            return convertToChar(value);
        } else if (targetType == String.class) {
            return value.toString();
        }

        return value;
    }

    /**
     * Resolves object references.
     */
    private void resolveReferences(Object instance, Map<String, String> references) throws SerializationException {
        Field[] fields = FieldInspector.getAllFields(instance.getClass());

        for (Field field : fields) {
            String fieldName = field.getName();
            if (references.containsKey(fieldName)) {
                String refId = references.get(fieldName);
                Object referenced = objectRegistry.get(refId);
                if (referenced != null) {
                    try {
                        field.setAccessible(true);
                        field.set(instance, referenced);
                    } catch (IllegalAccessException e) {
                        trySetterMethod(instance, field, referenced);
                    }
                }
            }
        }
    }

    /**
     * Checks serialVersionUID compatibility.
     */
    private void checkSerialVersionUID(Class<?> clazz, Object serializedVersion) {
        try {
            Field field = clazz.getDeclaredField("serialVersionUID");
            field.setAccessible(true);
            long classVersion = field.getLong(null);
            long serializedVersionUID = parseSerialVersionUID(serializedVersion);

            if (classVersion != serializedVersionUID) {
                warnings.add("SerialVersionUID mismatch for class " + clazz.getName() +
                    ": class=" + classVersion + ", serialized=" + serializedVersionUID);
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            // Class doesn't have serialVersionUID - ignore
        }
    }

    /**
     * Parses serialVersionUID from various formats.
     */
    private long parseSerialVersionUID(Object value) {
        if (value instanceof Number) {
            return ((Number) value).longValue();
        }
        if (value instanceof String) {
            String str = (String) value;
            if (str.endsWith("L")) {
                str = str.substring(0, str.length() - 1);
            }
            return Long.parseLong(str);
        }
        return 0L;
    }

    // Type conversion methods

    private boolean convertToBoolean(Object value) {
        if (value instanceof Boolean) return (Boolean) value;
        if (value instanceof Number) return ((Number) value).intValue() != 0;
        return Boolean.parseBoolean(value.toString());
    }

    private byte convertToByte(Object value) {
        if (value instanceof Number) return ((Number) value).byteValue();
        return Byte.parseByte(value.toString());
    }

    private short convertToShort(Object value) {
        if (value instanceof Number) return ((Number) value).shortValue();
        return Short.parseShort(value.toString());
    }

    private int convertToInt(Object value) {
        if (value instanceof Number) return ((Number) value).intValue();
        return Integer.parseInt(value.toString());
    }

    private long convertToLong(Object value) {
        if (value instanceof Number) return ((Number) value).longValue();
        return Long.parseLong(value.toString());
    }

    private float convertToFloat(Object value) {
        if (value instanceof Number) return ((Number) value).floatValue();
        return Float.parseFloat(value.toString());
    }

    private double convertToDouble(Object value) {
        if (value instanceof Number) return ((Number) value).doubleValue();
        return Double.parseDouble(value.toString());
    }

    private char convertToChar(Object value) {
        if (value instanceof Character) return (Character) value;
        String str = value.toString();
        return str.isEmpty() ? '\0' : str.charAt(0);
    }

    private Object convertToEnum(Class<?> enumType, Object value) {
        if (value == null) return null;
        String enumName = value.toString();
        for (Object enumConstant : enumType.getEnumConstants()) {
            if (((Enum<?>) enumConstant).name().equals(enumName)) {
                return enumConstant;
            }
        }
        throw new IllegalArgumentException("No enum constant " + enumType.getName() + "." + enumName);
    }

    @SuppressWarnings("unchecked")
    private Object convertToArray(Class<?> arrayType, Object value) {
        if (value == null) return null;
        if (!(value instanceof List)) return null;

        List<Object> list = (List<Object>) value;
        Class<?> componentType = arrayType.getComponentType();
        Object array = Array.newInstance(componentType, list.size());

        for (int i = 0; i < list.size(); i++) {
            Array.set(array, i, convertValue(list.get(i), componentType));
        }

        return array;
    }

    @SuppressWarnings("unchecked")
    private Object convertToCollection(Class<?> collectionType, Object value) throws SerializationException {
        if (value == null) return null;
        if (!(value instanceof List)) return value;

        List<Object> list = (List<Object>) value;
        List<Object> result = new ArrayList<>();

        for (Object item : list) {
            // If the item is a Map, it might be a nested object or reference
            if (item instanceof Map) {
                Object deserialized = deserializeObject(item);
                result.add(deserialized);
            } else {
                result.add(item);
            }
        }

        return result;
    }

    @SuppressWarnings("unchecked")
    private Object convertToMap(Class<?> mapType, Object value) {
        if (value == null) return null;
        if (!(value instanceof Map)) return value;

        return new LinkedHashMap<>((Map<String, Object>) value);
    }

    /**
     * Returns any warnings generated during deserialization.
     *
     * @return list of warning messages
     */
    public List<String> getWarnings() {
        return new ArrayList<>(warnings);
    }
}
