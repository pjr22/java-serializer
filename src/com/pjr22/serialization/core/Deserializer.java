package com.pjr22.serialization.core;

import com.pjr22.serialization.format.JsonParser;
import com.pjr22.serialization.inspector.ConstructorAnalyzer;
import com.pjr22.serialization.inspector.FieldInspector;
import com.pjr22.serialization.registry.ObjectRegistry;
import com.pjr22.serialization.util.CollectionFactory;
import com.pjr22.serialization.util.ValueSerializer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Deserializes Java objects from JSON format.
 * Handles object references, circular references, and complex object graphs.
 */
public class Deserializer<T> {

    private final Class<T> targetType;
    private final ObjectRegistry objectRegistry;
    private final List<String> warnings;
    
    // Placeholder marker for objects being constructed (to handle circular references)
    private static final Object PLACEHOLDER = new Object();
    
    // Track unresolved references: maps target object ID to list of (source object, field) pairs
    // that need to be resolved after the target object is fully constructed
    private final Map<String, List<UnresolvedReference>> unresolvedReferences;
    
    /**
     * Represents an unresolved reference that needs to be resolved after construction.
     */
    private static class UnresolvedReference {
        final Object sourceObject;
        final Field field;
        
        UnresolvedReference(Object sourceObject, Field field) {
            this.sourceObject = sourceObject;
            this.field = field;
        }
    }

    /**
     * Creates a new Deserializer for the specified target type.
     *
     * @param targetType the class to deserialize to
     */
    public Deserializer(Class<T> targetType) {
        this.targetType = targetType;
        this.objectRegistry = new ObjectRegistry();
        this.warnings = new ArrayList<>();
        this.unresolvedReferences = new HashMap<>();
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
                Object instance = createInstance(clazz, fields);
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
                // If the referenced object is a placeholder (still being constructed),
                // return a special marker to indicate this is an unresolved reference
                // The caller will track this and resolve it after the target is constructed
                if (referenced == PLACEHOLDER) {
                    return new UnresolvedReferenceMarker(refId);
                }
                return referenced;
            }

            // Check if this is an object definition
            if (map.containsKey("$id") && map.containsKey("$class")) {
                String objectId = (String) map.get("$id");
                String className = (String) map.get("$class");

                // Check if this is a simple value format (for JDK classes)
                if (map.containsKey("$value")) {
                    try {
                        Class<?> clazz = Class.forName(className);
                        Object value = map.get("$value");
                        // Strip quotes from JSON string values if present
                        Object rawValue = value;
                        if (value instanceof String) {
                            String strValue = (String) value;
                            if (strValue.startsWith("\"") && strValue.endsWith("\"") && strValue.length() > 1) {
                                rawValue = strValue.substring(1, strValue.length() - 1);
                            }
                        }
                        Object instance = ValueSerializer.deserializeFromValue(rawValue, clazz);
                        
                        if (instance != null) {
                            // Successfully deserialized from value
                            objectRegistry.register(objectId, instance);
                            return instance;
                        } else {
                            // Fallback to regular object deserialization
                                    Map<String, Object> fields = (Map<String, Object>) map.get("fields");
                                    Object existing = objectRegistry.get(objectId);
                                    if (existing != null) {
                                        return existing;
                                    }
                                      
                                    // Check serialVersionUID
                                    if (map.containsKey("serialVersionUID")) {
                                        checkSerialVersionUID(clazz, map.get("serialVersionUID"));
                                    }
                                      
                                    instance = createInstance(clazz, fields);
                                    objectRegistry.register(objectId, instance);
                                    setFields(instance, clazz, fields, new HashMap<>());
                                    return instance;
                        }
                    } catch (ClassNotFoundException e) {
                        throw new SerializationException("Class not found: " + className, e);
                    }
                }

                // Regular object with fields
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

                    // Register a placeholder BEFORE creating instance to handle circular references
                    // When nested objects reference back to this object during construction,
                    // they will get PLACEHOLDER instead of throwing "Referenced object not found"
                    objectRegistry.register(objectId, PLACEHOLDER);
                    
                    // Create instance
                    Object instance = createInstance(clazz, fields);
                    
                    // Replace placeholder with actual instance
                    objectRegistry.register(objectId, instance);
                    
                    // Resolve any unresolved references to this object
                    resolveUnresolvedReferences(objectId, instance);

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
    private Object createInstance(Class<?> clazz, Map<String, Object> fields) throws SerializationException {
        try {
            // Try to find a constructor with matching parameter names
            Constructor<?> constructor = ConstructorAnalyzer.selectBestConstructor(clazz, fields.keySet());

            if (constructor != null && constructor.getParameterCount() > 0) {
                // Use parameterized constructor
                return createWithConstructor(constructor, fields);
            }

            // Try default constructor (no-args)
            try {
                Constructor<?> defaultConstructor = clazz.getDeclaredConstructor();
                defaultConstructor.setAccessible(true);
                return defaultConstructor.newInstance();
            } catch (NoSuchMethodException e) {
                // No default constructor - try to use first constructor with default values
                Constructor<?>[] constructors = clazz.getDeclaredConstructors();
                if (constructors.length > 0) {
                    Constructor<?> fallbackConstructor = constructors[0];
                    fallbackConstructor.setAccessible(true);
                    try {
                        // Try to instantiate with default values for all parameters
                        return fallbackConstructor.newInstance(getDefaultValues(fallbackConstructor.getParameterTypes()));
                    } catch (Exception ex) {
                        // If default values don't work, try to find suitable values
                        Class<?>[] paramTypes = fallbackConstructor.getParameterTypes();
                        Object[] fallbackValues = new Object[paramTypes.length];
                        for (int i = 0; i < paramTypes.length; i++) {
                            Class<?> paramType = paramTypes[i];
                            // For enum types, try to find a matching constant
                            if (paramType.isEnum()) {
                                // Try to find an enum constant from fields that matches the parameter type
                                for (String fieldName : fields.keySet()) {
                                    try {
                                        Class<?> fieldClass = Class.forName(fieldName);
                                        if (fieldClass.isEnum() && fieldClass.equals(paramType)) {
                                            // Found a matching enum field, use its first constant as fallback
                                            Object[] enumConstants = fieldClass.getEnumConstants();
                                            if (enumConstants.length > 0) {
                                                fallbackValues[i] = enumConstants[0];
                                                break;
                                            }
                                        }
                                    } catch (ClassNotFoundException cnfe) {
                                        // Ignore, field name might not be a class name
                                    }
                                }
                            }
                            // If no enum match found, use default value
                            if (fallbackValues[i] == null) {
                                fallbackValues[i] = getDefaultValue(paramTypes[i]);
                            }
                        }
                        return fallbackConstructor.newInstance(fallbackValues);
                    }
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
    private Object createWithConstructor(Constructor<?> constructor, Map<String, Object> fields) throws SerializationException {
        try {
            constructor.setAccessible(true);
            Class<?>[] paramTypes = constructor.getParameterTypes();
            Object[] args = new Object[paramTypes.length];
            Parameter[] parameters = constructor.getParameters();

            for (int i = 0; i < paramTypes.length; i++) {
                String paramName = parameters[i].getName();
                Object fieldValue = fields.get(paramName);
                
                if (fieldValue != null) {
                    // Deserialize the field value to the expected parameter type
                    args[i] = deserializeValueForConstructor(fieldValue, paramTypes[i], constructor.getParameters()[i]);
                } else {
                    // Use default value if field is not present
                    args[i] = getDefaultValue(paramTypes[i]);
                }
            }
            
            // Handle unresolved reference markers in constructor arguments
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof UnresolvedReferenceMarker) {
                    args[i] = null;
                }
            }

            return constructor.newInstance(args);
        } catch (Exception e) {
            throw new SerializationException("Error creating instance with constructor", e);
        }
    }

    /**
     * Deserializes a value for use in a constructor parameter.
     * This method handles Maps, Collections, and primitive types appropriately.
     */
    private Object deserializeValueForConstructor(Object value, Class<?> targetType, Parameter parameter) throws SerializationException {
        if (value == null) {
            return getDefaultValue(targetType);
        }
        
        // Handle unresolved reference markers
        if (value instanceof UnresolvedReferenceMarker) {
            // For constructor parameters, we can't resolve this now
            // Return null and track it for later resolution
            // Note: This won't work for final fields in constructor parameters
            // because we can't change them after construction
            return null;
        }

        // Handle array types
        if (targetType.isArray()) {
            if (value instanceof List) {
                return convertToArray(targetType, value);
            }
            return value;
        }

        // Handle Map types - create the appropriate Map implementation
        if (Map.class.isAssignableFrom(targetType)) {
            if (value instanceof Map) {
                Map<String, Object> valueMap = (Map<String, Object>) value;
                // Create a LinkedHashMap for the constructor parameter
                Map<Object, Object> result = new LinkedHashMap<>();
                
                // Get the generic type from the parameter to find key and value types
                Class<?> keyType = String.class; // default
                java.lang.reflect.Type valueType = null; // unknown by default
                java.lang.reflect.Type genericType = parameter.getParameterizedType();
                if (genericType instanceof java.lang.reflect.ParameterizedType) {
                    java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) genericType;
                    java.lang.reflect.Type[] typeArgs = paramType.getActualTypeArguments();
                    if (typeArgs.length >= 1 && typeArgs[0] instanceof Class) {
                        keyType = (Class<?>) typeArgs[0];
                    }
                    if (typeArgs.length >= 2) {
                        valueType = typeArgs[1]; // Keep as Type for ParameterizedType support
                    }
                }
                
                // Handle nested objects in map values
                for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                    // Convert the key to the appropriate type
                    Object convertedKey = convertMapKey(entry.getKey(), keyType);
                    
                    // Deserialize the value based on its type (pass full Type for ParameterizedType support)
                    Object mapValue = deserializeMapValue(entry.getValue(), valueType);
                    
                    result.put(convertedKey, mapValue);
                }
                return result;
            }
            return value;
        }

        // Handle Collection types
        if (Collection.class.isAssignableFrom(targetType)) {
            if (value instanceof List) {
                Collection<Object> result = CollectionFactory.createCollection(targetType);
                for (Object item : (List<?>) value) {
                    if (item instanceof Map) {
                        result.add(deserializeObject(item));
                    } else {
                        result.add(item);
                    }
                }
                return result;
            }
            return value;
        }

        // Handle JDK classes that can be deserialized from a simple value
        if (ValueSerializer.canSerializeAsValue(targetType)) {
            Object deserialized = ValueSerializer.deserializeFromValue(value, targetType);
            if (deserialized != null) {
                return deserialized;
            }
        }

        // Handle primitive and wrapper types
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
        } else if (targetType == BigDecimal.class) {
            if (value instanceof Number) {
                return new BigDecimal(value.toString());
            } else {
                return new BigDecimal(value.toString());
            }
        } else if (targetType.isEnum()) {
            return convertToEnum(targetType, value);
        } else if (value instanceof Map) {
            // Nested object
            return deserializeObject(value);
        }

        return value;
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
        
        // Handle unresolved reference markers
        if (value instanceof UnresolvedReferenceMarker) {
            UnresolvedReferenceMarker marker = (UnresolvedReferenceMarker) value;
            // Set to null for now, but track this as an unresolved reference
            field.set(instance, null);
            unresolvedReferences.computeIfAbsent(marker.targetObjectId, k -> new ArrayList<>())
                .add(new UnresolvedReference(instance, field));
            return;
        }

        // Handle AtomicReference
        if (fieldType == AtomicReference.class) {
            Object refValue;
            if (value instanceof Map) {
                // Deserialize the nested object
                refValue = deserializeObject(value);
            } else {
                refValue = value;
            }
            
            // Get the generic type parameter from the field to determine the correct type
            Class<?> genericType = null;
            java.lang.reflect.Type genericFieldType = field.getGenericType();
            if (genericFieldType instanceof java.lang.reflect.ParameterizedType) {
                java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) genericFieldType;
                java.lang.reflect.Type[] typeArgs = paramType.getActualTypeArguments();
                if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                    genericType = (Class<?>) typeArgs[0];
                }
            }
            
            // Convert the value to the expected generic type if known
            Object convertedValue = refValue;
            if (genericType != null && refValue != null) {
                convertedValue = convertValueToType(refValue, genericType);
            }
            
            field.set(instance, new AtomicReference<>(convertedValue));
            return;
        }

        if (value == null) {
            field.set(instance, null);
            return;
        }

        // Handle JDK classes that can be deserialized from a simple value
        if (ValueSerializer.canSerializeAsValue(fieldType)) {
            Object deserialized = ValueSerializer.deserializeFromValue(value, fieldType);
            if (deserialized != null) {
                field.set(instance, deserialized);
                return;
            }
        }

        // Handle AtomicReference
        if (fieldType == AtomicReference.class) {
            Object refValue;
            if (value instanceof Map) {
                // Deserialize the nested object
                refValue = deserializeObject(value);
            } else {
                refValue = value;
            }
            
            // Get the generic type parameter from the field to determine the correct type
            Class<?> genericType = null;
            java.lang.reflect.Type genericFieldType = field.getGenericType();
            if (genericFieldType instanceof java.lang.reflect.ParameterizedType) {
                java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) genericFieldType;
                java.lang.reflect.Type[] typeArgs = paramType.getActualTypeArguments();
                if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                    genericType = (Class<?>) typeArgs[0];
                }
            }
            
            // Convert the value to the expected generic type if known
            Object convertedValue = refValue;
            if (genericType != null && refValue != null) {
                convertedValue = convertValueToType(refValue, genericType);
            }
            
            field.set(instance, new AtomicReference<>(convertedValue));
            return;
        }

        // Handle AtomicBoolean
        if (fieldType == AtomicBoolean.class) {
            boolean boolValue = convertToBoolean(value);
            field.set(instance, new AtomicBoolean(boolValue));
            return;
        }

        // Handle AtomicInteger
        if (fieldType == AtomicInteger.class) {
            int intValue = convertToInt(value);
            field.set(instance, new AtomicInteger(intValue));
            return;
        }

        // Handle AtomicLong
        if (fieldType == AtomicLong.class) {
            long longValue = convertToLong(value);
            field.set(instance, new AtomicLong(longValue));
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
            field.set(instance, convertToMap(field, value));
        } else if (value instanceof Map) {
            // This might be a nested object
            Object nested = deserializeObject(value);
            // Handle unresolved reference markers
            if (nested instanceof UnresolvedReferenceMarker) {
                UnresolvedReferenceMarker marker = (UnresolvedReferenceMarker) nested;
                // Set to null for now, but track this as an unresolved reference
                field.set(instance, null);
                unresolvedReferences.computeIfAbsent(marker.targetObjectId, k -> new ArrayList<>())
                    .add(new UnresolvedReference(instance, field));
            } else {
                field.set(instance, nested);
            }
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
     * Converts a value to the specified type, with support for enums and other complex types.
     * This method is used for converting values when the target type is known via reflection.
     *
     * @param value the value to convert
     * @param targetType the target type to convert to
     * @return the converted value
     * @throws SerializationException if conversion fails
     */
    private Object convertValueToType(Object value, Class<?> targetType) throws SerializationException {
        if (value == null) {
            return null;
        }

        // Handle primitive and wrapper types
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
        } else if (targetType == BigDecimal.class) {
            if (value instanceof Number) {
                return new BigDecimal(value.toString());
            } else {
                return new BigDecimal(value.toString());
            }
        } else if (targetType.isEnum()) {
            return convertToEnum(targetType, value);
        } else if (value instanceof Map) {
            // Nested object - deserialize it
            return deserializeObject(value);
        }

        // Return value as-is if no conversion needed
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
     * Resolves unresolved references to a specific object.
     * Called after an object is fully constructed to resolve any references
     * that were deferred because the target was still being constructed.
     *
     * @param targetObjectId the ID of the object that was just constructed
     * @param targetInstance the actual instance of the constructed object
     */
    private void resolveUnresolvedReferences(String targetObjectId, Object targetInstance) {
        List<UnresolvedReference> refs = unresolvedReferences.get(targetObjectId);
        if (refs == null || refs.isEmpty()) {
            return;
        }
        
        for (UnresolvedReference ref : refs) {
            try {
                ref.field.setAccessible(true);
                ref.field.set(ref.sourceObject, targetInstance);
            } catch (IllegalAccessException e) {
                // Ignore - can't set the field (e.g., final field that's already set)
            }
        }
        
        // Clear the resolved references
        unresolvedReferences.remove(targetObjectId);
    }
    
    /**
     * Marker class to indicate a reference that couldn't be resolved
     * because the target object was still being constructed.
     */
    private static class UnresolvedReferenceMarker {
        final String targetObjectId;
        
        UnresolvedReferenceMarker(String targetObjectId) {
            this.targetObjectId = targetObjectId;
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
        
        // Create the appropriate collection type based on the field type
        Collection<Object> result = CollectionFactory.createCollection(collectionType);

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

    /**
     * Converts a string key from JSON to the appropriate type for a Map.
     * Handles primitive types, wrapper types, enums, and other common types.
     * Uses ValueSerializer for complex types like UUID that can be constructed from a string.
     *
     * @param keyString string key from JSON
     * @param keyType target type for the key
     * @return the converted key
     */
    private Object convertMapKey(String keyString, Class<?> keyType) {
        if (keyType == String.class) {
            return keyString;
        } else if (keyType == Long.class || keyType == long.class) {
            return convertToLong(keyString);
        } else if (keyType == Integer.class || keyType == int.class) {
            return convertToInt(keyString);
        } else if (keyType == Short.class || keyType == short.class) {
            return convertToShort(keyString);
        } else if (keyType == Byte.class || keyType == byte.class) {
            return convertToByte(keyString);
        } else if (keyType == Double.class || keyType == double.class) {
            return convertToDouble(keyString);
        } else if (keyType == Float.class || keyType == float.class) {
            return convertToFloat(keyString);
        } else if (keyType == Boolean.class || keyType == boolean.class) {
            return convertToBoolean(keyString);
        } else if (keyType == Character.class || keyType == char.class) {
            return convertToChar(keyString);
        } else if (keyType.isEnum()) {
            return convertToEnum(keyType, keyString);
        }
        
        // Try to use ValueSerializer for complex types like UUID, Date, etc.
        // This handles JDK classes with fromString(String) or single-parameter constructors
        Object converted = ValueSerializer.deserializeFromValue(keyString, keyType);
        if (converted != null) {
            return converted;
        }
        
        // Default to string for unknown types
        return keyString;
    }

    /**
     * Deserializes a map value based on its expected type.
     *
     * @param mapValue value from the parsed map
     * @param valueType expected value type (may be null)
     * @return the deserialized value
     * @throws SerializationException if deserialization fails
     */
    private Object deserializeMapValue(Object mapValue, Class<?> valueType) throws SerializationException {
        return deserializeMapValue(mapValue, (java.lang.reflect.Type) valueType);
    }

    /**
     * Deserializes a map value based on its expected type.
     * This overload accepts a Type parameter to handle ParameterizedType values
     * (e.g., Map<Long, String>) for nested maps with typed keys.
     *
     * @param mapValue value from the parsed map
     * @param valueType expected value type (may be null)
     * @return the deserialized value
     * @throws SerializationException if deserialization fails
     */
    @SuppressWarnings("unchecked")
    private Object deserializeMapValue(Object mapValue, java.lang.reflect.Type valueType) throws SerializationException {
        if (mapValue == null) {
            return null;
        }
        
        if (mapValue instanceof Map) {
            Map<String, Object> valueMap = (Map<String, Object>) mapValue;
            // Check if this is an object definition with $id and $class
            if (valueMap.containsKey("$id") && valueMap.containsKey("$class")) {
                Object result = deserializeObject(mapValue);
                // Handle unresolved reference markers
                if (result instanceof UnresolvedReferenceMarker) {
                    // For constructor parameters, return null
                    return null;
                }
                return result;
            }
            
            // Handle nested maps with typed keys
            // If valueType is a ParameterizedType with raw type Map, convert keys to appropriate types
            if (valueType instanceof java.lang.reflect.ParameterizedType) {
                java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) valueType;
                java.lang.reflect.Type rawType = paramType.getRawType();
                if (rawType instanceof Class && Map.class.isAssignableFrom((Class<?>) rawType)) {
                    // This is a Map type - extract key and value type parameters
                    java.lang.reflect.Type[] typeArgs = paramType.getActualTypeArguments();
                    Class<?> keyType = String.class; // default
                    Class<?> nestedValueType = null; // unknown by default
                    java.lang.reflect.Type nestedValueTypeType = null; // for nested ParameterizedType
                    
                    if (typeArgs.length >= 1 && typeArgs[0] instanceof Class) {
                        keyType = (Class<?>) typeArgs[0];
                    }
                    if (typeArgs.length >= 2) {
                        if (typeArgs[1] instanceof Class) {
                            nestedValueType = (Class<?>) typeArgs[1];
                        } else {
                            nestedValueTypeType = typeArgs[1]; // ParameterizedType for nested maps
                        }
                    }
                    
                    // Create a new map with properly typed keys
                    Map<Object, Object> result = new LinkedHashMap<>();
                    for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                        // Convert the key to the appropriate type (handles Long, Integer, UUID, Enum, etc.)
                        Object convertedKey = convertMapKey(entry.getKey(), keyType);
                        
                        // Recursively deserialize the value with its type information
                        Object nestedValue;
                        if (nestedValueTypeType != null) {
                            nestedValue = deserializeMapValue(entry.getValue(), nestedValueTypeType);
                        } else {
                            nestedValue = deserializeMapValue(entry.getValue(), nestedValueType);
                        }
                        
                        result.put(convertedKey, nestedValue);
                    }
                    return result;
                }
            }
            
            // Otherwise, it's a nested map - return as-is
            return mapValue;
        } else if (mapValue instanceof List) {
            // List values may contain nested objects - deserialize each element
            List<Object> deserializedList = new ArrayList<>();
            for (Object item : (List<?>) mapValue) {
                if (item instanceof Map) {
                    Map<String, Object> itemMap = (Map<String, Object>) item;
                    if (itemMap.containsKey("$id") && itemMap.containsKey("$class")) {
                        deserializedList.add(deserializeObject(item));
                    } else {
                        deserializedList.add(item);
                    }
                } else {
                    deserializedList.add(item);
                }
            }
            return deserializedList;
        }
        
        // For primitive types, try to convert if valueType is known
        if (valueType != null) {
            if (valueType == Long.class || valueType == long.class) {
                return convertToLong(mapValue);
            } else if (valueType == Integer.class || valueType == int.class) {
                return convertToInt(mapValue);
            } else if (valueType == Double.class || valueType == double.class) {
                return convertToDouble(mapValue);
            } else if (valueType == Float.class || valueType == float.class) {
                return convertToFloat(mapValue);
            } else if (valueType == Boolean.class || valueType == boolean.class) {
                return convertToBoolean(mapValue);
            }
        }
        
        return mapValue;
    }

    @SuppressWarnings("unchecked")
    private Object convertToMap(Field field, Object value) throws SerializationException {
        if (value == null) return null;
        if (!(value instanceof Map)) return value;

        Map<String, Object> parsedMap = (Map<String, Object>) value;
        
        try {
            // Get the generic type from the field to find key and value types
            java.lang.reflect.Type genericType = field.getGenericType();
            Class<?> keyType = String.class; // default
            java.lang.reflect.Type valueType = null; // unknown by default
            
            if (genericType instanceof java.lang.reflect.ParameterizedType) {
                java.lang.reflect.ParameterizedType paramType = (java.lang.reflect.ParameterizedType) genericType;
                java.lang.reflect.Type[] typeArgs = paramType.getActualTypeArguments();
                if (typeArgs.length >= 1 && typeArgs[0] instanceof Class) {
                    keyType = (Class<?>) typeArgs[0];
                }
                if (typeArgs.length >= 2) {
                    valueType = typeArgs[1]; // Keep as Type for ParameterizedType support
                }
            }
            
            // Create the result map with appropriate key types
            Map<Object, Object> result = new LinkedHashMap<>();
            for (Map.Entry<String, Object> entry : parsedMap.entrySet()) {
                // Convert the key to the appropriate type
                Object convertedKey = convertMapKey(entry.getKey(), keyType);
                
                // Deserialize the value based on its type (pass full Type for ParameterizedType support)
                Object mapValue = deserializeMapValue(entry.getValue(), valueType);
                
                result.put(convertedKey, mapValue);
            }
            return result;
        } catch (Exception e) {
            // If we can't determine the key type, just return the map as-is
            return new LinkedHashMap<>(parsedMap);
        }
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
