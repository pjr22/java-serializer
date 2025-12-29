package com.pjr22.serialization.format;

import java.util.Collection;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Serializes Java objects to JSON format.
 * Handles primitives, strings, numbers, arrays, collections, maps, and null values.
 */
public class JsonSerializer {

    /**
     * Serializes an object to its JSON string representation.
     *
     * @param value the object to serialize
     * @return the JSON string representation
     */
    public static String serialize(Object value) {
        if (value == null) {
            return "null";
        }

        Class<?> clazz = value.getClass();

        // Handle primitives and primitive wrappers
        if (value instanceof Boolean) {
            return value.toString();
        }
        if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
            return value.toString();
        }
        if (value instanceof Float || value instanceof Double) {
            return value.toString();
        }
        if (value instanceof Character) {
            return "\"" + escapeJson((Character) value) + "\"";
        }

        // Handle String
        if (value instanceof String) {
            return "\"" + escapeJson((String) value) + "\"";
        }

        // Handle Number types (including BigDecimal)
        if (value instanceof Number) {
            return value.toString();
        }

        // Handle Atomic types
        if (value instanceof AtomicBoolean) {
            return String.valueOf(((AtomicBoolean) value).get());
        }
        if (value instanceof AtomicInteger) {
            return String.valueOf(((AtomicInteger) value).get());
        }
        if (value instanceof AtomicLong) {
            return String.valueOf(((AtomicLong) value).get());
        }
        if (value instanceof AtomicReference) {
            Object refValue = ((AtomicReference<?>) value).get();
            return refValue != null ? serialize(refValue) : "null";
        }

        // Handle arrays
        if (clazz.isArray()) {
            return serializeArray(value);
        }

        // Handle collections
        if (value instanceof Collection) {
            return serializeCollection((Collection<?>) value);
        }

        // Handle maps
        if (value instanceof Map) {
            return serializeMap((Map<?, ?>) value);
        }

        // Handle enums
        if (clazz.isEnum()) {
            return "\"" + ((Enum<?>) value).name() + "\"";
        }

        // For other object types, return as string representation
        // This is a simple fallback - full object serialization is handled by Serializer class
        return "\"" + escapeJson(value.toString()) + "\"";
    }

    /**
     * Serializes an array to JSON array format.
     *
     * @param array the array to serialize
     * @return the JSON array string
     */
    private static String serializeArray(Object array) {
        if (array == null) {
            return "null";
        }

        int length = java.lang.reflect.Array.getLength(array);
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < length; i++) {
            if (i > 0) {
                sb.append(",");
            }
            Object element = java.lang.reflect.Array.get(array, i);
            sb.append(serialize(element));
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Serializes a collection to JSON array format.
     *
     * @param collection the collection to serialize
     * @return the JSON array string
     */
    private static String serializeCollection(Collection<?> collection) {
        if (collection == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder("[");
        boolean first = true;
        for (Object element : collection) {
            if (!first) {
                sb.append(",");
            }
            sb.append(serialize(element));
            first = false;
        }
        sb.append("]");
        return sb.toString();
    }

    /**
     * Serializes a map to JSON object format.
     *
     * @param map the map to serialize
     * @return the JSON object string
     */
    private static String serializeMap(Map<?, ?> map) {
        if (map == null) {
            return "null";
        }

        StringBuilder sb = new StringBuilder("{");
        boolean first = true;
        for (Map.Entry<?, ?> entry : map.entrySet()) {
            if (!first) {
                sb.append(",");
            }
            Object key = entry.getKey();
            String keyString;
            // For enum keys, use the name() method to avoid potential issues with toString()
            if (key != null && key.getClass().isEnum()) {
                keyString = ((Enum<?>) key).name();
            } else {
                keyString = key.toString();
            }
            sb.append("\"").append(escapeJson(keyString)).append("\":");
            sb.append(serialize(entry.getValue()));
            first = false;
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * Escapes special characters in a string for JSON.
     *
     * @param str the string to escape
     * @return the escaped string
     */
    private static String escapeJson(String str) {
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
     * Escapes special characters in a character for JSON.
     *
     * @param c the character to escape
     * @return the escaped string
     */
    private static String escapeJson(char c) {
        return escapeJson(String.valueOf(c));
    }
}
