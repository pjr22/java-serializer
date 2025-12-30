package com.pjr22.serialization.test;

import com.pjr22.serialization.core.Deserializer;
import com.pjr22.serialization.core.SerializationException;
import com.pjr22.serialization.core.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tests deserialization of Maps with typed keys (e.g., Long, Integer).
 * This verifies that map keys are converted to the correct types during deserialization.
 */
public class MapKeyTypeDeserializationTest extends TestCase {

    /**
     * Test class with a Map<Long, String> field.
     */
    public static class PersonWithLongKeyMap {
        private static final long serialVersionUID = 1L;
        private final Map<Long, String> longKeyMap;

        public PersonWithLongKeyMap(Map<Long, String> longKeyMap) {
            this.longKeyMap = new LinkedHashMap<>(longKeyMap);
        }

        public Map<Long, String> getLongKeyMap() {
            return longKeyMap;
        }
    }

    /**
     * Test class with a Map<Integer, String> field.
     */
    public static class PersonWithIntegerKeyMap {
        private static final long serialVersionUID = 1L;
        private final Map<Integer, String> integerKeyMap;

        public PersonWithIntegerKeyMap(Map<Integer, String> integerKeyMap) {
            this.integerKeyMap = new LinkedHashMap<>(integerKeyMap);
        }

        public Map<Integer, String> getIntegerKeyMap() {
            return integerKeyMap;
        }
    }

    /**
     * Test class with a Map<Double, String> field.
     */
    public static class PersonWithDoubleKeyMap {
        private static final long serialVersionUID = 1L;
        private final Map<Double, String> doubleKeyMap;

        public PersonWithDoubleKeyMap(Map<Double, String> doubleKeyMap) {
            this.doubleKeyMap = new LinkedHashMap<>(doubleKeyMap);
        }

        public Map<Double, String> getDoubleKeyMap() {
            return doubleKeyMap;
        }
    }

    /**
     * Test that Long keys are properly deserialized.
     */
    public void testLongKeyMapDeserialization() throws SerializationException {
        // Create original object with Long keys
        Map<Long, String> originalMap = new LinkedHashMap<>();
        originalMap.put(1001L, "Room 1001");
        originalMap.put(1002L, "Room 1002");
        originalMap.put(1003L, "Room 1003");
        PersonWithLongKeyMap original = new PersonWithLongKeyMap(originalMap);

        // Serialize
        Serializer serializer = new Serializer("test", 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(original, out);
        String json = out.toString();

        // Deserialize
        Deserializer<PersonWithLongKeyMap> deserializer = new Deserializer<>(PersonWithLongKeyMap.class);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes());
        PersonWithLongKeyMap deserialized = deserializer.deserialize(in);

        // Verify keys are Long type, not String
        Map<Long, String> deserializedMap = deserialized.getLongKeyMap();
        for (Map.Entry<Long, String> entry : deserializedMap.entrySet()) {
            assertTrue(entry.getKey() instanceof Long, "Key should be Long type");
            assertEquals(entry.getValue(), originalMap.get(entry.getKey()), "Value should match");
        }
    }

    /**
     * Test that Integer keys are properly deserialized.
     */
    public void testIntegerKeyMapDeserialization() throws SerializationException {
        // Create original object with Integer keys
        Map<Integer, String> originalMap = new LinkedHashMap<>();
        originalMap.put(1, "First");
        originalMap.put(2, "Second");
        originalMap.put(3, "Third");
        PersonWithIntegerKeyMap original = new PersonWithIntegerKeyMap(originalMap);

        // Serialize
        Serializer serializer = new Serializer("test", 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(original, out);
        String json = out.toString();

        // Deserialize
        Deserializer<PersonWithIntegerKeyMap> deserializer = new Deserializer<>(PersonWithIntegerKeyMap.class);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes());
        PersonWithIntegerKeyMap deserialized = deserializer.deserialize(in);

        // Verify keys are Integer type, not String
        Map<Integer, String> deserializedMap = deserialized.getIntegerKeyMap();
        for (Map.Entry<Integer, String> entry : deserializedMap.entrySet()) {
            assertTrue(entry.getKey() instanceof Integer, "Key should be Integer type");
            assertEquals(entry.getValue(), originalMap.get(entry.getKey()), "Value should match");
        }
    }

    /**
     * Test that Double keys are properly deserialized.
     */
    public void testDoubleKeyMapDeserialization() throws SerializationException {
        // Create original object with Double keys
        Map<Double, String> originalMap = new LinkedHashMap<>();
        originalMap.put(1.5, "One point five");
        originalMap.put(2.5, "Two point five");
        originalMap.put(3.0, "Three point zero");
        PersonWithDoubleKeyMap original = new PersonWithDoubleKeyMap(originalMap);

        // Serialize
        Serializer serializer = new Serializer("test", 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(original, out);
        String json = out.toString();

        // Deserialize
        Deserializer<PersonWithDoubleKeyMap> deserializer = new Deserializer<>(PersonWithDoubleKeyMap.class);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes());
        PersonWithDoubleKeyMap deserialized = deserializer.deserialize(in);

        // Verify keys are Double type, not String
        Map<Double, String> deserializedMap = deserialized.getDoubleKeyMap();
        for (Map.Entry<Double, String> entry : deserializedMap.entrySet()) {
            assertTrue(entry.getKey() instanceof Double, "Key should be Double type");
            assertEquals(entry.getValue(), originalMap.get(entry.getKey()), "Value should match");
        }
    }

    /**
     * Test class with a nested Map containing typed keys.
     */
    public static class PersonWithNestedMap {
        private static final long serialVersionUID = 1L;
        private final Map<String, Map<Long, String>> nestedMap;

        public PersonWithNestedMap(Map<String, Map<Long, String>> nestedMap) {
            this.nestedMap = new LinkedHashMap<>(nestedMap);
        }

        public Map<String, Map<Long, String>> getNestedMap() {
            return nestedMap;
        }
    }

    /**
     * Test that nested maps with typed keys are properly deserialized.
     */
    public void testNestedMapWithTypedKeys() throws SerializationException {
        // Create original object with nested Map<Long, String>
        Map<Long, String> innerMap = new LinkedHashMap<>();
        innerMap.put(1001L, "Inner Value");
        
        Map<String, Map<Long, String>> outerMap = new LinkedHashMap<>();
        outerMap.put("nested", innerMap);
        
        PersonWithNestedMap original = new PersonWithNestedMap(outerMap);

        // Serialize
        Serializer serializer = new Serializer("test", 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(original, out);
        String json = out.toString();

        // Deserialize
        Deserializer<PersonWithNestedMap> deserializer = new Deserializer<>(PersonWithNestedMap.class);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes());
        PersonWithNestedMap deserialized = deserializer.deserialize(in);

        // Verify nested map keys are Long type
        Map<Long, String> deserializedInnerMap = deserialized.getNestedMap().get("nested");
        assertNotNull(deserializedInnerMap, "Nested map should not be null");
        
        for (Map.Entry<Long, String> entry : deserializedInnerMap.entrySet()) {
            assertTrue(entry.getKey() instanceof Long, "Nested key should be Long type");
            assertEquals(entry.getValue(), innerMap.get(entry.getKey()), "Nested value should match");
        }
    }

    /**
     * Test class with a Map<UUID, String> field.
     */
    public static class PersonWithUUIDKeyMap {
        private static final long serialVersionUID = 1L;
        private final Map<UUID, String> uuidKeyMap;

        public PersonWithUUIDKeyMap(Map<UUID, String> uuidKeyMap) {
            this.uuidKeyMap = new LinkedHashMap<>(uuidKeyMap);
        }

        public Map<UUID, String> getUuidKeyMap() {
            return uuidKeyMap;
        }
    }

    /**
     * Test that UUID keys are properly deserialized.
     */
    public void testUUIDKeyMapDeserialization() throws SerializationException {
        // Create original object with UUID keys
        Map<UUID, String> originalMap = new LinkedHashMap<>();
        UUID uuid1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
        UUID uuid2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
        UUID uuid3 = UUID.fromString("00000000-0000-0000-0000-000000000003");
        
        originalMap.put(uuid1, "Quest 1");
        originalMap.put(uuid2, "Quest 2");
        originalMap.put(uuid3, "Quest 3");
        
        PersonWithUUIDKeyMap original = new PersonWithUUIDKeyMap(originalMap);

        // Serialize
        Serializer serializer = new Serializer("test", 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(original, out);
        String json = out.toString();

        // Deserialize
        Deserializer<PersonWithUUIDKeyMap> deserializer = new Deserializer<>(PersonWithUUIDKeyMap.class);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes());
        PersonWithUUIDKeyMap deserialized = deserializer.deserialize(in);

        // Verify keys are UUID type, not String
        Map<UUID, String> deserializedMap = deserialized.getUuidKeyMap();
        assertNotNull(deserializedMap, "Deserialized map should not be null");
        
        for (Map.Entry<UUID, String> entry : deserializedMap.entrySet()) {
            assertTrue(entry.getKey() instanceof UUID, "Key should be UUID type");
            assertEquals(entry.getValue(), originalMap.get(entry.getKey()), "Value should match");
        }
        
        // Verify we can look up by UUID (this would fail if keys were Strings)
        assertEquals("Quest 1", deserializedMap.get(uuid1), "Should be able to lookup by UUID");
        assertEquals("Quest 2", deserializedMap.get(uuid2), "Should be able to lookup by UUID");
        assertEquals("Quest 3", deserializedMap.get(uuid3), "Should be able to lookup by UUID");
    }

    /**
     * Main method to run all tests.
     */
    public static void main(String[] args) throws SerializationException {
        MapKeyTypeDeserializationTest test = new MapKeyTypeDeserializationTest();
        test.testLongKeyMapDeserialization();
        test.testIntegerKeyMapDeserialization();
        test.testDoubleKeyMapDeserialization();
        test.testNestedMapWithTypedKeys();
        test.testUUIDKeyMapDeserialization();
        System.out.println("All Map key type deserialization tests passed!");
    }
}
