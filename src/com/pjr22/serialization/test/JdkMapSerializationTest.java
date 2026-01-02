package com.pjr22.serialization.test;

import com.pjr22.serialization.core.Deserializer;
import com.pjr22.serialization.core.SerializationException;
import com.pjr22.serialization.core.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Test case for JDK Map serialization/deserialization issue.
 * This reproduces the bug where JDK Map implementations (like LinkedHashMap)
 * are incorrectly serialized as objects with $id, $class, and fields metadata
 * instead of as plain JSON maps.
 */
public class JdkMapSerializationTest extends TestCase {

    /**
     * Test class that represents an Item in the user's game.
     */
    public static class Item {
        private String id;
        private String name;

        public Item() {
        }

        public Item(String id, String name) {
            this.id = id;
            this.name = name;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }
    }

    /**
     * Test class that contains a Map of items.
     * This simulates the user's saved game data structure.
     */
    public static class GameState {
        private Map<String, Item> items;

        public GameState() {
        }

        public GameState(Map<String, Item> items) {
            this.items = items;
        }

        public Map<String, Item> getItems() {
            return items;
        }

        public void setItems(Map<String, Item> items) {
            this.items = items;
        }
    }

    /**
     * Test that JDK Map implementations in map values are not serialized with object metadata.
     * The bug causes LinkedHashMap to be serialized with $id, $class, and fields.
     */
    public void testMapWithJdkMapValues() throws SerializationException {
        // Create a map with LinkedHashMap as a value (simulating the user's scenario)
        Map<String, Object> data = new HashMap<>();
        Map<String, String> innerMap = new LinkedHashMap<>();
        innerMap.put("key1", "value1");
        innerMap.put("key2", "value2");
        data.put("innerMap", innerMap);

        // Serialize
        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(data, out);
        String json = out.toString();

        // Verify that innerMap is NOT serialized with object metadata
        // It should be serialized as a plain JSON object, not with $id, $class, fields
        assertFalse(json.contains("\"innerMap\":{\"$id\":"), 
            "JDK Map values should be serialized as plain JSON maps, not as objects with metadata");
        assertFalse(json.contains("\"innerMap\":{\"$class\":"), 
            "JDK Map values should be serialized as plain JSON maps, not as objects with metadata");
        assertTrue(json.contains("\"key1\":\"value1\""), 
            "Map content should be present");
    }

    /**
     * Test that custom objects in map values are serialized correctly.
     */
    public void testMapWithCustomObjectValues() throws SerializationException {
        // Create a GameState with items
        Map<String, Item> items = new HashMap<>();
        items.put("item1", new Item("item1", "Sword"));
        items.put("item2", new Item("item2", "Shield"));
        GameState gameState = new GameState(items);

        // Serialize
        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(gameState, out);
        String json = out.toString();

        // Verify that items are serialized correctly
        assertTrue(json.contains("\"items\""), "Items field should be present");
        assertTrue(json.contains("\"item1\""), "Item key should be present");
        assertTrue(json.contains("\"Sword\""), "Item name should be present");

        // Deserialize and verify
        Deserializer<GameState> deserializer = new Deserializer<>(GameState.class);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes());
        GameState result = deserializer.deserialize(in);

        assertNotNull(result, "Deserialized object should not be null");
        assertNotNull(result.getItems(), "Items map should not be null");
        assertEquals(2, result.getItems().size(), "Items map should have 2 entries");

        Item item1 = result.getItems().get("item1");
        assertNotNull(item1, "Item1 should not be null");
        assertEquals("item1", item1.getId(), "Item1 ID should match");
        assertEquals("Sword", item1.getName(), "Item1 name should match");
    }

    /**
     * Test that a Map<String, Object> with nested LinkedHashMap can be round-trip serialized.
     * This reproduces the exact scenario from the user's bug report.
     */
    public void testNestedLinkedHashMapRoundTrip() throws SerializationException {
        // Create the exact structure from the bug report
        Map<String, Object> root = new HashMap<>();
        Map<String, Object> items = new HashMap<>();
        Map<String, Object> itemData = new LinkedHashMap<>();
        itemData.put("name", "Test Item");
        itemData.put("value", 42);
        items.put("1aafbbb8-b0ed-414f-95ae-db2ba379d278", itemData);
        root.put("items", items);

        // Serialize
        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(root, out);
        String json = out.toString();

        // Verify that LinkedHashMap is NOT serialized with object metadata
        assertFalse(json.contains("\"1aafbbb8-b0ed-414f-95ae-db2ba379d278\":{\"$id\":"), 
            "JDK Map values should be serialized as plain JSON maps, not as objects with metadata");
        assertFalse(json.contains("\"1aafbbb8-b0ed-414f-95ae-db2ba379d278\":{\"$class\":\"java.util.LinkedHashMap\""), 
            "JDK Map values should be serialized as plain JSON maps, not as objects with metadata");

        // Deserialize
        @SuppressWarnings({"unchecked", "rawtypes"})
        Deserializer deserializer = new Deserializer(Map.class);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes());
        Map<String, Object> result = (Map<String, Object>) deserializer.deserialize(in);

        assertNotNull(result, "Deserialized object should not be null");
        assertNotNull(result.get("items"), "Items should not be null");

        @SuppressWarnings("unchecked")
        Map<String, Object> itemsResult = (Map<String, Object>) result.get("items");
        assertNotNull(itemsResult.get("1aafbbb8-b0ed-414f-95ae-db2ba379d278"), 
            "Item should not be null");

        // The item data should be a Map (LinkedHashMap is the default Map implementation used by deserializer)
        Object itemResult = itemsResult.get("1aafbbb8-b0ed-414f-95ae-db2ba379d278");
        assertTrue(itemResult instanceof Map,
            "Item data should be deserialized as a Map");
        assertTrue(itemResult instanceof LinkedHashMap,
            "Item data should be deserialized as a LinkedHashMap (default implementation)");
    }

    /**
     * Test that LinkedHashMap as a top-level object is serialized correctly.
     */
    public void testLinkedHashMapAsTopLevel() throws SerializationException {
        LinkedHashMap<String, String> map = new LinkedHashMap<>();
        map.put("a", "1");
        map.put("b", "2");
        map.put("c", "3");

        // Serialize
        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(map, out);
        String json = out.toString();

        // Verify LinkedHashMap is NOT serialized with object metadata
        assertFalse(json.contains("{\"$id\":"), 
            "JDK Map should be serialized as plain JSON map, not as object with metadata");
        assertFalse(json.contains("\"$class\":\"java.util.LinkedHashMap\""), 
            "JDK Map should be serialized as plain JSON map, not as object with metadata");
        assertTrue(json.contains("\"a\":\"1\""), 
            "Map content should be present");
        assertTrue(json.contains("\"b\":\"2\""), 
            "Map content should be present");
        assertTrue(json.contains("\"c\":\"3\""), 
            "Map content should be present");

        // Deserialize
        @SuppressWarnings({"unchecked", "rawtypes"})
        Deserializer deserializer = new Deserializer(LinkedHashMap.class);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes());
        LinkedHashMap<String, String> result = (LinkedHashMap<String, String>) deserializer.deserialize(in);

        assertNotNull(result, "Deserialized map should not be null");
        assertEquals(3, result.size(), "Map should have 3 entries");
        assertEquals("1", result.get("a"), "Value should match");
        assertEquals("2", result.get("b"), "Value should match");
        assertEquals("3", result.get("c"), "Value should match");
    }

    public static void main(String[] args) {
        JdkMapSerializationTest test = new JdkMapSerializationTest();
        test.run();
    }
}
