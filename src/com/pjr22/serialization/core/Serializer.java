package com.pjr22.serialization.core;

import com.pjr22.serialization.format.JsonSerializer;
import com.pjr22.serialization.inspector.FieldClassifier;
import com.pjr22.serialization.inspector.FieldInspector;
import com.pjr22.serialization.registry.ObjectIdGenerator;
import com.pjr22.serialization.registry.ObjectRegistry;
import com.pjr22.serialization.util.ValueSerializer;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.IdentityHashMap;
import java.util.Map;

/**
 * Serializes Java objects to JSON format.
 * Handles object references, circular references, and complex object graphs.
 */
public class Serializer {

    private final ObjectIdGenerator idGenerator;
    private final ObjectRegistry objectRegistry;
    private final Map<Object, String> objectToIdMap;
    
    // Track complex objects used as map keys
    private final Map<Object, String> mapKeyToIdMap = new IdentityHashMap<>();

    /**
     * Creates a new Serializer with the specified serialization key and starting ID.
     *
     * @param serializationKey the prefix to use for all generated object IDs
     * @param startingId the starting value for the object ID counter
     */
    public Serializer(String serializationKey, int startingId) {
        this.idGenerator = new ObjectIdGenerator(serializationKey, startingId);
        this.objectRegistry = new ObjectRegistry();
        this.objectToIdMap = new IdentityHashMap<>();
    }

    /**
     * Serializes an object to JSON format and writes it to the output stream.
     *
     * @param object the object to serialize
     * @param outputStream the output stream to write to
     * @throws SerializationException if a serialization error occurs
     */
    public void serialize(Object object, OutputStream outputStream) throws SerializationException {
        try (OutputStreamWriter writer = new OutputStreamWriter(outputStream, StandardCharsets.UTF_8)) {
            String json = serializeObject(object);
            writer.write(json);
            writer.flush();
        } catch (IOException e) {
            throw new SerializationException("Error writing to output stream", e);
        }
    }

    /**
     * Serializes an object to JSON format.
     *
     * @param object the object to serialize
     * @return the JSON string representation
     * @throws SerializationException if a serialization error occurs
     */
    private String serializeObject(Object object) throws SerializationException {
        if (object == null) {
            return "null";
        }

        // Check if this object has already been serialized
        if (objectToIdMap.containsKey(object)) {
            return "{\"$ref\":\"" + objectToIdMap.get(object) + "\"}";
        }

        // Check if this is a JDK Map implementation - serialize as plain JSON map
        // JDK Maps (LinkedHashMap, HashMap, TreeMap, etc.) should be serialized as plain JSON maps,
        // not as objects with $id, $class, and fields metadata
        if (object instanceof Map) {
            return serializeMap(object);
        }

        // Check if this is a JDK class that can be serialized as a simple value
        if (ValueSerializer.canSerializeAsValue(object.getClass())) {
            Object value = ValueSerializer.serializeAsValue(object);
            if (value != null) {
                // Serialize as a simple value with class metadata
                String objectId = idGenerator.generateId();
                objectToIdMap.put(object, objectId);
                objectRegistry.register(objectId, object);
                
                StringBuilder sb = new StringBuilder();
                sb.append("{");
                sb.append("\"$id\":\"").append(objectId).append("\",");
                sb.append("\"$class\":\"").append(object.getClass().getName()).append("\",");
                sb.append("\"$value\":").append(JsonSerializer.serialize(value)).append("}");
                return sb.toString();
            }
        }

        // Generate object ID and register it
        String objectId = idGenerator.generateId();
        objectToIdMap.put(object, objectId);
        objectRegistry.register(objectId, object);

        StringBuilder sb = new StringBuilder();
        sb.append("{");

        // Add object ID
        sb.append("\"$id\":\"").append(objectId).append("\",");

        // Add class name
        sb.append("\"$class\":\"").append(object.getClass().getName()).append("\",");

        // Add serialVersionUID if present
        Long serialVersionUID = getSerialVersionUID(object.getClass());
        if (serialVersionUID != null) {
            sb.append("\"serialVersionUID\":").append(serialVersionUID).append(",");
        }

        // Serialize fields
        sb.append("\"fields\":{");
        serializeFields(object, sb);
        sb.append("}");

        // Add $mapKeys section if there are complex objects used as map keys
        if (!mapKeyToIdMap.isEmpty()) {
            sb.append(",");
            serializeMapKeys(sb);
        }

        sb.append("}");

        return sb.toString();
    }

    /**
     * Serializes all fields of an object.
     *
     * @param object the object whose fields to serialize
     * @param sb the StringBuilder to append to
     * @throws SerializationException if a serialization error occurs
     */
    private void serializeFields(Object object, StringBuilder sb) throws SerializationException {
        Field[] fields = FieldInspector.getAllFields(object.getClass());
        boolean first = true;

        for (Field field : fields) {
            try {
                if (!first) {
                    sb.append(",");
                }
                first = false;

                String fieldName = field.getName();
                Object fieldValue = field.get(object);

                sb.append("\"").append(fieldName).append("\":");

                FieldClassifier.FieldCategory category = FieldClassifier.classify(field);

                switch (category) {
                    case PRIMITIVE:
                    case STRING:
                    case NUMBER:
                    case BIG_DECIMAL:
                    case ATOMIC_BOOLEAN:
                    case ATOMIC_INTEGER:
                    case ATOMIC_LONG:
                    case ENUM:
                        // Native JSON types - serialize directly
                        sb.append(JsonSerializer.serialize(fieldValue));
                        break;
                    
                    case VALUE_SERIALIZABLE:
                        // Value-serializable types - serialize using ValueSerializer
                        Object value = ValueSerializer.serializeAsValue(fieldValue);
                        if (value != null) {
                            // Serialize the value (which may be a String or Number)
                            sb.append(JsonSerializer.serialize(value));
                        } else {
                            // Fallback to object serialization
                            sb.append(serializeObject(fieldValue));
                        }
                        break;
                    
                    case ATOMIC_REFERENCE:
                        // AtomicReference - extract the referenced value and serialize appropriately
                        if (fieldValue == null) {
                            sb.append("null");
                        } else {
                            Object refValue = ((java.util.concurrent.atomic.AtomicReference<?>) fieldValue).get();
                            if (refValue == null) {
                                sb.append("null");
                            } else if (isSimpleType(refValue)) {
                                // Simple type - use JsonSerializer
                                sb.append(JsonSerializer.serialize(refValue));
                            } else {
                                // Complex object - serialize as nested object
                                sb.append(serializeObject(refValue));
                            }
                        }
                        break;

                    case COLLECTION:
                        // Collection - serialize elements, handling nested objects
                        sb.append(serializeCollection(fieldValue));
                        break;

                    case MAP:
                        // Map - serialize entries, handling nested objects
                        sb.append(serializeMap(fieldValue));
                        break;

                    case ARRAY:
                        // Array - serialize elements, handling nested objects
                        sb.append(serializeArray(fieldValue));
                        break;

                    case OBJECT_REFERENCE:
                        // Object reference - serialize as nested object
                        if (fieldValue == null) {
                            sb.append("null");
                        } else {
                            sb.append(serializeObject(fieldValue));
                        }
                        break;
                }
            } catch (IllegalAccessException e) {
                throw new SerializationException("Error accessing field: " + field.getName(), e);
            }
        }
    }

    /**
     * Serializes a collection, handling nested objects.
     *
     * @param collection the collection to serialize
     * @return the JSON string representation
     * @throws SerializationException if a serialization error occurs
     */
    private String serializeCollection(Object collection) throws SerializationException {
        if (collection == null) {
            return "null";
        }

        Collection<?> coll = (Collection<?>) collection;
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;

        for (Object element : coll) {
            if (!first) {
                sb.append(",");
            }
            first = false;

            if (element == null) {
                sb.append("null");
            } else if (isSimpleType(element)) {
                // Simple type - use JsonSerializer
                sb.append(JsonSerializer.serialize(element));
            } else {
                // Complex object - serialize as nested object
                sb.append(serializeObject(element));
            }
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Serializes a map, handling nested objects.
     *
     * @param map the map to serialize
     * @return the JSON string representation
     * @throws SerializationException if a serialization error occurs
     */
    private String serializeMap(Object map) throws SerializationException {
        if (map == null) {
            return "null";
        }

        Map<?, ?> mapObj = (Map<?, ?>) map;
        StringBuilder sb = new StringBuilder("{");
        boolean first = true;

        for (Map.Entry<?, ?> entry : mapObj.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;

            // Key - serialize as string without double-quoting
            Object key = entry.getKey();
            String keyString;
            // For enum keys, use the name() method to get the enum constant name
            if (key != null && key.getClass().isEnum()) {
                keyString = ((Enum<?>) key).name();
            } else if (key != null && isSimpleType(key)) {
                // Simple types (String, Number, etc.) - use toString()
                keyString = key.toString();
            } else if (key != null) {
                // Complex object key - register and use reference
                String keyId;
                if (mapKeyToIdMap.containsKey(key)) {
                    keyId = mapKeyToIdMap.get(key);
                } else {
                    keyId = idGenerator.generateId();
                    mapKeyToIdMap.put(key, keyId);
                    // Also register in the main object registry
                    objectRegistry.register(keyId, key);
                }
                keyString = "$ref:" + keyId;
            } else {
                keyString = "null";
            }
            sb.append("\"").append(escapeJsonKey(keyString)).append("\":");

            // Value
            Object value = entry.getValue();
            if (value == null) {
                sb.append("null");
            } else if (isSimpleType(value)) {
                // Simple type - use JsonSerializer
                sb.append(JsonSerializer.serialize(value));
            } else if (value instanceof Collection) {
                // Collection - serialize as JSON array
                sb.append(serializeCollection(value));
            } else if (value instanceof Map) {
                // Nested Map - serialize as JSON map (recursively)
                // This handles JDK Map implementations (LinkedHashMap, HashMap, etc.)
                // which should be serialized as plain JSON maps, not as objects with metadata
                sb.append(serializeMap(value));
            } else if (value.getClass().isArray()) {
                // Array - serialize as JSON array
                sb.append(serializeArray(value));
            } else {
                // Complex object - serialize as nested object
                sb.append(serializeObject(value));
            }
        }

        sb.append("}");
        return sb.toString();
    }

    /**
     * Serializes an array, handling nested objects.
     *
     * @param array the array to serialize
     * @return the JSON string representation
     * @throws SerializationException if a serialization error occurs
     */
    private String serializeArray(Object array) throws SerializationException {
        if (array == null) {
            return "null";
        }

        int length = Array.getLength(array);
        StringBuilder sb = new StringBuilder("[");
        boolean first = true;

        for (int i = 0; i < length; i++) {
            if (!first) {
                sb.append(",");
            }
            first = false;

            Object element = Array.get(array, i);
            if (element == null) {
                sb.append("null");
            } else if (isSimpleType(element)) {
                // Simple type - use JsonSerializer
                sb.append(JsonSerializer.serialize(element));
            } else {
                // Complex object - serialize as nested object
                sb.append(serializeObject(element));
            }
        }

        sb.append("]");
        return sb.toString();
    }

    /**
     * Checks if an object is a simple type that can be serialized by JsonSerializer.
     *
     * @param obj the object to check
     * @return true if the object is a simple type, false otherwise
     */
    private boolean isSimpleType(Object obj) {
        if (obj == null) {
            return true;
        }

        Class<?> clazz = obj.getClass();

        // Primitives and their wrappers
        if (obj instanceof Boolean || obj instanceof Byte || obj instanceof Short ||
            obj instanceof Integer || obj instanceof Long || obj instanceof Float ||
            obj instanceof Double || obj instanceof Character) {
            return true;
        }

        // String
        if (obj instanceof String) {
            return true;
        }

        // Number types (including BigDecimal)
        if (obj instanceof Number) {
            return true;
        }

        // Atomic types
        if (obj instanceof java.util.concurrent.atomic.AtomicBoolean ||
            obj instanceof java.util.concurrent.atomic.AtomicInteger ||
            obj instanceof java.util.concurrent.atomic.AtomicLong ||
            obj instanceof java.util.concurrent.atomic.AtomicReference) {
            return true;
        }

        // Enum
        if (clazz.isEnum()) {
            return true;
        }

        // Everything else is a complex object
        return false;
    }

    /**
     * Escapes special characters in a string for JSON key use.
     *
     * @param str the string to escape
     * @return the escaped string
     */
    private String escapeJsonKey(String str) {
        if (str == null) {
            return "";
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length(); i++) {
            char c = str.charAt(i);
            switch (c) {
                case '"':
                    sb.append("\\\"");
                    break;
                case '\\':
                    sb.append("\\\\");
                    break;
                case '\b':
                    sb.append("\\b");
                    break;
                case '\f':
                    sb.append("\\f");
                    break;
                case '\n':
                    sb.append("\\n");
                    break;
                case '\r':
                    sb.append("\\r");
                    break;
                case '\t':
                    sb.append("\\t");
                    break;
                default:
                    if (c < ' ') {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.toString();
    }

    /**
     * Serializes the $mapKeys section containing complex objects used as map keys.
     *
     * @param sb the StringBuilder to append to
     * @throws SerializationException if a serialization error occurs
     */
    private void serializeMapKeys(StringBuilder sb) throws SerializationException {
        sb.append("\"$mapKeys\":{");
        boolean first = true;
        
        for (Map.Entry<Object, String> entry : mapKeyToIdMap.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            first = false;
            
            Object key = entry.getKey();
            String keyId = entry.getValue();
            
            sb.append("\"").append(keyId).append("\":");
            
            // Serialize the key object as a full object definition
            // Note: We use the same ID that was generated in serializeMap() for consistency
            String objectId = keyId; // Use the same ID as the map key reference
            objectToIdMap.put(key, objectId);
            objectRegistry.register(objectId, key);
            
            sb.append("{");
            sb.append("\"$id\":\"").append(objectId).append("\",");
            sb.append("\"$class\":\"").append(key.getClass().getName()).append("\",");
            
            // Add serialVersionUID if present
            Long serialVersionUID = getSerialVersionUID(key.getClass());
            if (serialVersionUID != null) {
                sb.append("\"serialVersionUID\":").append(serialVersionUID).append(",");
            }
            
            // Serialize fields
            sb.append("\"fields\":{");
            serializeFields(key, sb);
            sb.append("}");
            
            sb.append("}");
        }
        
        sb.append("}");
    }

    /**
     * Gets the serialVersionUID from a class if it exists.
     * Skips JDK and system classes to avoid module system access restrictions.
     *
     * @param clazz the class to check
     * @return the serialVersionUID value, or null if not present
     */
    private Long getSerialVersionUID(Class<?> clazz) {
        // Skip JDK and system classes - they don't need serialVersionUID for this library
        // This prevents InaccessibleObjectException in Java 9+ when trying to access
        // private fields in java.* packages via reflection
        if (clazz.getClassLoader() == null ||
            clazz.getName().startsWith("java.") ||
            clazz.getName().startsWith("javax.") ||
            clazz.getName().startsWith("sun.")) {
            return null;
        }
        
        try {
            Field field = clazz.getDeclaredField("serialVersionUID");
            field.setAccessible(true);
            return field.getLong(null);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            return null;
        }
    }

    /**
     * Returns the current object ID counter value.
     *
     * @return the current counter value
     */
    public int getCounter() {
        return idGenerator.getCounter();
    }

    /**
     * Returns the serialization key used by this serializer.
     *
     * @return the serialization key
     */
    public String getSerializationKey() {
        return idGenerator.getSerializationKey();
    }
}
