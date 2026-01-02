package com.pjr22.serialization.test;

import com.pjr22.serialization.core.Deserializer;
import com.pjr22.serialization.core.SerializationException;
import com.pjr22.serialization.core.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Tests deserialization of Maps with object values (e.g., Map<UUID, Item>).
 * This verifies that map values containing complex objects are properly deserialized.
 */
public class MapValueObjectDeserializationTest extends TestCase {

    /**
     * Simple item class for testing.
     */
    public static class Item {
        private static final long serialVersionUID = 1L;
        private UUID id;
        private String name;
        private double value;

        public Item() {}

        public Item(UUID id, String name, double value) {
            this.id = id;
            this.name = name;
            this.value = value;
        }

        public UUID getId() { return id; }
        public String getName() { return name; }
        public double getValue() { return value; }
    }

    /**
     * Specialized item class (subclass) for testing polymorphism.
     */
    public static class SpecialItem extends Item {
        private static final long serialVersionUID = 1L;
        private String special;

        public SpecialItem() {}

        public SpecialItem(UUID id, String name, double value, String special) {
            super(id, name, value);
            this.special = special;
        }

        public String getSpecial() { return special; }
    }

    /**
     * Container class with Map<UUID, Item> field.
     */
    public static class Quest {
        private static final long serialVersionUID = 1L;
        private UUID id;
        private String name;
        private Map<UUID, Item> items = new HashMap<>();

        public Quest() {}

        public Quest(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        public void addItem(Item item) {
            items.put(item.getId(), item);
        }

        public UUID getId() { return id; }
        public String getName() { return name; }
        public Map<UUID, Item> getItems() { return items; }
    }

    /**
     * Container class with synchronized Map<UUID, Item> field (like the real Quest class).
     */
    public static class QuestWithSynchronizedMap {
        private static final long serialVersionUID = 1L;
        protected UUID id;
        protected String name;
        protected final Map<UUID, Item> items = java.util.Collections.synchronizedMap(new HashMap<>());

        public QuestWithSynchronizedMap() {}

        public QuestWithSynchronizedMap(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        public void addItem(Item item) {
            items.put(item.getId(), item);
        }

        public UUID getId() { return id; }
        public String getName() { return name; }
        public Map<UUID, Item> getItems() { return items; }
    }

    /**
     * SavedGame with synchronized maps (matching real structure).
     */
    public static class SavedGameWithSynchronizedMaps {
        private static final long serialVersionUID = 1L;
        private String name;
        private final Map<UUID, QuestWithFinalMap> quests = java.util.Collections.synchronizedMap(new HashMap<>());

        public SavedGameWithSynchronizedMaps() {}

        public SavedGameWithSynchronizedMaps(String name) {
            this.name = name;
        }

        public void addQuest(QuestWithFinalMap quest) {
            quests.put(quest.getId(), quest);
        }

        public String getName() { return name; }
        public Map<UUID, QuestWithFinalMap> getQuests() { return quests; }
    }

    /**
     * Quest with final synchronized items map (matching real structure).
     */
    public static class QuestWithFinalMap {
        private static final long serialVersionUID = 1L;
        protected UUID id;
        protected String name;
        protected final Map<UUID, Item> items = java.util.Collections.synchronizedMap(new HashMap<>());

        public QuestWithFinalMap() {}

        public QuestWithFinalMap(UUID id, String name) {
            this.id = id;
            this.name = name;
        }

        public void addItem(Item item) {
            items.put(item.getId(), item);
        }

        public UUID getId() { return id; }
        public String getName() { return name; }
        public Map<UUID, Item> getItems() { return items; }
    }

    /**
     * Test that Map<UUID, Item> values are properly deserialized.
     */
    public void testMapWithObjectValues() throws SerializationException {
        // Create quest with items
        Quest original = new Quest(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "Test Quest"
        );

        Item item1 = new Item(
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            "Sword",
            100.0
        );
        Item item2 = new Item(
            UUID.fromString("22222222-2222-2222-2222-222222222222"),
            "Shield",
            75.0
        );

        original.addItem(item1);
        original.addItem(item2);

        // Serialize
        Serializer serializer = new Serializer("test", 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(original, out);
        String json = out.toString();

        // Deserialize
        Deserializer<Quest> deserializer = new Deserializer<>(Quest.class);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes());
        Quest deserialized = deserializer.deserialize(in);

        // Verify items map is correctly deserialized
        Map<UUID, Item> deserializedItems = deserialized.getItems();
        assertNotNull(deserializedItems, "Items map should not be null");
        assertEquals(2, deserializedItems.size(), "Should have 2 items");

        // Verify each item is properly deserialized (not a LinkedHashMap)
        for (Map.Entry<UUID, Item> entry : deserializedItems.entrySet()) {
            assertTrue(entry.getKey() instanceof UUID, "Key should be UUID type");
            assertTrue(entry.getValue() instanceof Item, 
                "Value should be Item type, but was: " + entry.getValue().getClass().getName());
        }

        // Verify specific items
        Item sword = deserializedItems.get(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertNotNull(sword, "Sword item should exist");
        assertEquals("Sword", sword.getName(), "Item name should match");
        assertEquals(100.0, sword.getValue(), "Item value should match");
    }

    /**
     * Test that polymorphic map values are properly deserialized.
     */
    public void testMapWithPolymorphicValues() throws SerializationException {
        // Create quest with mixed item types
        Quest original = new Quest(
            UUID.fromString("00000000-0000-0000-0000-000000000002"),
            "Polymorphic Quest"
        );

        Item regularItem = new Item(
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            "Regular Item",
            50.0
        );
        SpecialItem specialItem = new SpecialItem(
            UUID.fromString("22222222-2222-2222-2222-222222222222"),
            "Special Item",
            200.0,
            "Magic"
        );

        original.addItem(regularItem);
        original.addItem(specialItem);

        // Serialize
        Serializer serializer = new Serializer("test", 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(original, out);
        String json = out.toString();

        // Deserialize
        Deserializer<Quest> deserializer = new Deserializer<>(Quest.class);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes());
        Quest deserialized = deserializer.deserialize(in);

        // Verify special item retains its subclass type
        Map<UUID, Item> deserializedItems = deserialized.getItems();
        
        Item regular = deserializedItems.get(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertNotNull(regular, "Regular item should exist");
        assertEquals(Item.class, regular.getClass(), "Regular item should be Item class");

        Item special = deserializedItems.get(UUID.fromString("22222222-2222-2222-2222-222222222222"));
        assertNotNull(special, "Special item should exist");
        assertTrue(special instanceof SpecialItem, 
            "Special item should be SpecialItem, but was: " + special.getClass().getName());
        assertEquals("Magic", ((SpecialItem) special).getSpecial(), "Special property should match");
    }

    /**
     * Test that synchronized Map<UUID, Item> values are properly deserialized.
     * This mirrors the real Quest class structure.
     */
    public void testSynchronizedMapWithObjectValues() throws SerializationException {
        // Create quest with items
        QuestWithSynchronizedMap original = new QuestWithSynchronizedMap(
            UUID.fromString("00000000-0000-0000-0000-000000000003"),
            "Synchronized Quest"
        );

        Item item1 = new Item(
            UUID.fromString("33333333-3333-3333-3333-333333333333"),
            "Axe",
            150.0
        );
        Item item2 = new Item(
            UUID.fromString("44444444-4444-4444-4444-444444444444"),
            "Helmet",
            80.0
        );

        original.addItem(item1);
        original.addItem(item2);

        // Serialize
        Serializer serializer = new Serializer("test", 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(original, out);
        String json = out.toString();

        // Deserialize
        Deserializer<QuestWithSynchronizedMap> deserializer = new Deserializer<>(QuestWithSynchronizedMap.class);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes());
        QuestWithSynchronizedMap deserialized = deserializer.deserialize(in);

        // Verify items map is correctly deserialized
        Map<UUID, Item> deserializedItems = deserialized.getItems();
        assertNotNull(deserializedItems, "Items map should not be null");
        assertEquals(2, deserializedItems.size(), "Should have 2 items");

        // Verify each item is properly deserialized (not a LinkedHashMap)
        for (Map.Entry<UUID, Item> entry : deserializedItems.entrySet()) {
            assertTrue(entry.getKey() instanceof UUID, "Key should be UUID type");
            assertTrue(entry.getValue() instanceof Item, 
                "Value should be Item type, but was: " + entry.getValue().getClass().getName());
        }

        // Verify specific items
        Item axe = deserializedItems.get(UUID.fromString("33333333-3333-3333-3333-333333333333"));
        assertNotNull(axe, "Axe item should exist");
        assertEquals("Axe", axe.getName(), "Item name should match");
    }

    /**
     * A SavedGame-like container with Map<UUID, Quest>.
     * This mirrors the actual structure where quests contain items.
     */
    public static class SavedGame {
        private static final long serialVersionUID = 1L;
        private String name;
        private Map<UUID, Quest> quests = new HashMap<>();

        public SavedGame() {}

        public SavedGame(String name) {
            this.name = name;
        }

        public void addQuest(Quest quest) {
            quests.put(quest.getId(), quest);
        }

        public String getName() { return name; }
        public Map<UUID, Quest> getQuests() { return quests; }
    }

    /**
     * Test deeply nested structure: SavedGame -> Map<UUID, Quest> -> Map<UUID, Item>
     * This mirrors the actual application structure.
     */
    public void testDeeplyNestedMapsWithObjectValues() throws SerializationException {
        // Create saved game with quests that contain items
        SavedGame original = new SavedGame("Test Game");

        Quest quest = new Quest(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "The Lost Prayer Book"
        );

        Item item1 = new Item(
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            "Book Fragment 1",
            0.0
        );
        Item item2 = new Item(
            UUID.fromString("22222222-2222-2222-2222-222222222222"),
            "Book Fragment 2",
            0.0
        );

        quest.addItem(item1);
        quest.addItem(item2);
        original.addQuest(quest);

        // Serialize
        Serializer serializer = new Serializer("test", 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(original, out);
        String json = out.toString();

        // Deserialize
        Deserializer<SavedGame> deserializer = new Deserializer<>(SavedGame.class);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes());
        SavedGame deserialized = deserializer.deserialize(in);

        // Verify quests map
        Map<UUID, Quest> deserializedQuests = deserialized.getQuests();
        assertNotNull(deserializedQuests, "Quests map should not be null");
        assertEquals(1, deserializedQuests.size(), "Should have 1 quest");

        // Verify quest is properly deserialized
        Quest deserializedQuest = deserializedQuests.get(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        assertNotNull(deserializedQuest, "Quest should exist");
        assertTrue(deserializedQuest instanceof Quest, 
            "Quest value should be Quest type, but was: " + deserializedQuest.getClass().getName());

        // Verify nested items map
        Map<UUID, Item> deserializedItems = deserializedQuest.getItems();
        assertNotNull(deserializedItems, "Items map should not be null");
        assertEquals(2, deserializedItems.size(), "Should have 2 items");

        // Verify each item is properly deserialized (not a LinkedHashMap)
        for (Map.Entry<UUID, Item> entry : deserializedItems.entrySet()) {
            assertTrue(entry.getKey() instanceof UUID, "Key should be UUID type");
            assertTrue(entry.getValue() instanceof Item,
                "Value should be Item type, but was: " + entry.getValue().getClass().getName());
        }

        // Verify specific item
        Item fragment1 = deserializedItems.get(UUID.fromString("11111111-1111-1111-1111-111111111111"));
        assertNotNull(fragment1, "Book Fragment 1 should exist");
        assertEquals("Book Fragment 1", fragment1.getName(), "Item name should match");
    }

    /**
     * Test that map values which are references ($ref) are properly resolved.
     * This is the actual bug scenario: mobs map contains references to Mob objects
     * defined elsewhere in the object graph.
     */
    public void testMapWithReferenceValues() throws SerializationException {
        // Create items that will be referenced
        Item sharedItem = new Item(
            UUID.fromString("99999999-9999-9999-9999-999999999999"),
            "Shared Item",
            999.0
        );

        // Create two quests that both reference the same item
        Quest quest1 = new Quest(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "Quest 1"
        );
        Quest quest2 = new Quest(
            UUID.fromString("00000000-0000-0000-0000-000000000002"),
            "Quest 2"
        );

        // Both quests share the same item reference
        quest1.addItem(sharedItem);
        quest2.addItem(sharedItem);

        // Create a container that holds both quests
        SavedGame original = new SavedGame("Reference Test");
        original.addQuest(quest1);
        original.addQuest(quest2);

        // Serialize
        Serializer serializer = new Serializer("test", 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(original, out);
        String json = out.toString();

        // Verify the JSON contains $ref (the second occurrence of the shared item)
        assertTrue(json.contains("$ref"), "JSON should contain $ref for shared object");

        // Deserialize
        Deserializer<SavedGame> deserializer = new Deserializer<>(SavedGame.class);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes());
        SavedGame deserialized = deserializer.deserialize(in);

        // Verify quests
        Map<UUID, Quest> deserializedQuests = deserialized.getQuests();
        assertEquals(2, deserializedQuests.size(), "Should have 2 quests");

        Quest deserializedQuest1 = deserializedQuests.get(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        Quest deserializedQuest2 = deserializedQuests.get(UUID.fromString("00000000-0000-0000-0000-000000000002"));

        assertNotNull(deserializedQuest1, "Quest 1 should exist");
        assertNotNull(deserializedQuest2, "Quest 2 should exist");

        // Both quests should have the shared item
        Item item1 = deserializedQuest1.getItems().get(UUID.fromString("99999999-9999-9999-9999-999999999999"));
        Item item2 = deserializedQuest2.getItems().get(UUID.fromString("99999999-9999-9999-9999-999999999999"));

        assertNotNull(item1, "Item in Quest 1 should exist");
        assertNotNull(item2, "Item in Quest 2 should exist");

        // Both should be Item instances (not LinkedHashMap)
        assertTrue(item1 instanceof Item, 
            "Item in Quest 1 should be Item type, but was: " + item1.getClass().getName());
        assertTrue(item2 instanceof Item, 
            "Item in Quest 2 should be Item type, but was: " + item2.getClass().getName());

        // Both should reference the same object (identity check)
        assertTrue(item1 == item2, "Both quests should reference the same Item object");
    }

    /**
     * Test matching actual structure: SavedGame with synchronized maps
     * containing Quest with final synchronized items map.
     */
    public void testRealWorldStructure() throws SerializationException {
        SavedGameWithSynchronizedMaps original = new SavedGameWithSynchronizedMaps("Real World Test");

        QuestWithFinalMap quest = new QuestWithFinalMap(
            UUID.fromString("00000000-0000-0000-0000-000000000001"),
            "The Lost Prayer Book"
        );

        Item item1 = new Item(
            UUID.fromString("11111111-1111-1111-1111-111111111111"),
            "Book Fragment 1",
            0.0
        );
        SpecialItem item2 = new SpecialItem(
            UUID.fromString("22222222-2222-2222-2222-222222222222"),
            "Book Fragment 2",
            0.0,
            "Holy"
        );

        quest.addItem(item1);
        quest.addItem(item2);
        original.addQuest(quest);

        // Serialize
        Serializer serializer = new Serializer("test", 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(original, out);
        String json = out.toString();

        // Deserialize
        Deserializer<SavedGameWithSynchronizedMaps> deserializer = new Deserializer<>(SavedGameWithSynchronizedMaps.class);
        ByteArrayInputStream in = new ByteArrayInputStream(json.getBytes());
        SavedGameWithSynchronizedMaps deserialized = deserializer.deserialize(in);

        // Verify quests
        Map<UUID, QuestWithFinalMap> deserializedQuests = deserialized.getQuests();
        assertNotNull(deserializedQuests, "Quests map should not be null");
        assertEquals(1, deserializedQuests.size(), "Should have 1 quest");

        // Verify quest is properly deserialized
        QuestWithFinalMap deserializedQuest = deserializedQuests.get(UUID.fromString("00000000-0000-0000-0000-000000000001"));
        assertNotNull(deserializedQuest, "Quest should exist");
        assertTrue(deserializedQuest instanceof QuestWithFinalMap,
            "Quest value should be QuestWithFinalMap type, but was: " + deserializedQuest.getClass().getName());

        // Verify nested items map
        Map<UUID, Item> deserializedItems = deserializedQuest.getItems();
        assertNotNull(deserializedItems, "Items map should not be null");
        assertEquals(2, deserializedItems.size(), "Should have 2 items");

        // Verify each item is properly deserialized
        for (Map.Entry<UUID, Item> entry : deserializedItems.entrySet()) {
            assertTrue(entry.getKey() instanceof UUID, "Key should be UUID type");
            assertTrue(entry.getValue() instanceof Item,
                "Value should be Item type, but was: " + entry.getValue().getClass().getName());
        }
    }

    /**
     * Main method to run all tests.
     */
    public static void main(String[] args) throws SerializationException {
        MapValueObjectDeserializationTest test = new MapValueObjectDeserializationTest();
        test.testMapWithObjectValues();
        test.testMapWithPolymorphicValues();
        test.testSynchronizedMapWithObjectValues();
        test.testDeeplyNestedMapsWithObjectValues();
        test.testMapWithReferenceValues();
        test.testRealWorldStructure();
        System.out.println("All Map value object deserialization tests passed!");
    }
}
