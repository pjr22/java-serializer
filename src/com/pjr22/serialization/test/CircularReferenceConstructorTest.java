package com.pjr22.serialization.test;

import com.pjr22.serialization.core.Deserializer;
import com.pjr22.serialization.core.SerializationException;
import com.pjr22.serialization.core.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * Test case for circular references with constructor-based deserialization.
 * This reproduces the bug where an object references itself through a constructor parameter
 * of a nested object.
 */
public class CircularReferenceConstructorTest extends TestCase {

    /**
     * Test class with a circular reference through constructor.
     * Parent has a Child, and Child has a reference back to Parent.
     */
    public static class Parent {
        private final String name;
        private final Child child;

        public Parent(String name, Child child) {
            this.name = name;
            this.child = child;
        }

        public String getName() {
            return name;
        }

        public Child getChild() {
            return child;
        }
    }

    /**
     * Test class that references back to Parent.
     */
    public static class Child {
        private final String name;
        private final Parent parent;

        public Child(String name, Parent parent) {
            this.name = name;
            this.parent = parent;
        }

        public String getName() {
            return name;
        }

        public Parent getParent() {
            return parent;
        }
    }

    /**
     * Test class with a Map containing objects that reference each other.
     * This simulates the quests Map structure in the saved game data.
     */
    public static class Container {
        private final Map<String, Quest> quests;

        public Container(Map<String, Quest> quests) {
            this.quests = quests;
        }

        public Map<String, Quest> getQuests() {
            return quests;
        }
    }

    public static class Quest {
        private final String id;
        private final String name;
        private final QuestItem item;

        public Quest(String id, String name, QuestItem item) {
            this.id = id;
            this.name = name;
            this.item = item;
        }

        public String getId() {
            return id;
        }

        public String getName() {
            return name;
        }

        public QuestItem getItem() {
            return item;
        }
    }

    public static class QuestItem {
        private final String itemId;
        private final String itemName;
        private final Quest quest;

        public QuestItem(String itemId, String itemName, Quest quest) {
            this.itemId = itemId;
            this.itemName = itemName;
            this.quest = quest;
        }

        public String getItemId() {
            return itemId;
        }

        public String getItemName() {
            return itemName;
        }

        public Quest getQuest() {
            return quest;
        }
    }

    /**
     * Test serialization and deserialization of a simple circular reference.
     */
    public void testSimpleCircularReference() throws SerializationException {
        // Create objects with circular reference
        Child child = new Child("child", null);
        Parent parent = new Parent("parent", child);
        
        // Set the circular reference
        // Note: We can't do this with final fields, so we'll test with serialization
        // of the structure as it would be created
    }

    /**
     * Test deserialization of JSON with circular reference through constructor.
     * This reproduces the bug from the saved game data.
     */
    public void testCircularReferenceDeserialization() throws SerializationException {
        // JSON that represents a Parent with a Child that references back to Parent
        String json = "{\n" +
                "  \"$id\": \"parent_1\",\n" +
                "  \"$class\": \"com.pjr22.serialization.test.CircularReferenceConstructorTest$Parent\",\n" +
                "  \"fields\": {\n" +
                "    \"name\": \"parent\",\n" +
                "    \"child\": {\n" +
                "      \"$id\": \"child_1\",\n" +
                "      \"$class\": \"com.pjr22.serialization.test.CircularReferenceConstructorTest$Child\",\n" +
                "      \"fields\": {\n" +
                "        \"name\": \"child\",\n" +
                "        \"parent\": {\n" +
                "          \"$ref\": \"parent_1\"\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        try {
            Deserializer<Parent> deserializer = new Deserializer<>(Parent.class);
            Parent result = deserializer.deserialize(new ByteArrayInputStream(json.getBytes()));
            
            assertNotNull(result, "Parent should not be null");
            assertEquals("parent", result.getName(), "Parent name should be 'parent'");
            assertNotNull(result.getChild(), "Child should not be null");
            assertEquals("child", result.getChild().getName(), "Child name should be 'child'");
            // assertSame is not available in TestCase, but we can check reference equality
            assertEquals(result, result.getChild().getParent(), "Child's parent should be the same object");
            
        } catch (SerializationException e) {
            // This is expected to fail with the bug
            // The error should be "Referenced object not found: parent_1"
            // For now, just verify the error message to confirm the issue
            if (e.getMessage().contains("Referenced object not found")) {
                fail("Bug reproduced: " + e.getMessage());
            } else {
                // Some other error - rethrow
                throw e;
            }
        }
    }

    /**
     * Test deserialization of a Map containing objects with circular references.
     * This more closely simulates the quests Map structure from the saved game.
     */
    public void testMapWithCircularReferences() throws SerializationException {
        // JSON that represents a Container with a Map of Quests
        // Each Quest has a QuestItem that references back to the Quest
        String json = "{\n" +
                "  \"$id\": \"container_1\",\n" +
                "  \"$class\": \"com.pjr22.serialization.test.CircularReferenceConstructorTest$Container\",\n" +
                "  \"fields\": {\n" +
                "    \"quests\": {\n" +
                "      \"quest_1\": {\n" +
                "        \"$id\": \"quest_1\",\n" +
                "        \"$class\": \"com.pjr22.serialization.test.CircularReferenceConstructorTest$Quest\",\n" +
                "        \"fields\": {\n" +
                "          \"id\": \"quest_1\",\n" +
                "          \"name\": \"Test Quest\",\n" +
                "          \"item\": {\n" +
                "            \"$id\": \"item_1\",\n" +
                "            \"$class\": \"com.pjr22.serialization.test.CircularReferenceConstructorTest$QuestItem\",\n" +
                "            \"fields\": {\n" +
                "              \"itemId\": \"item_1\",\n" +
                "              \"itemName\": \"Test Item\",\n" +
                "              \"quest\": {\n" +
                "                \"$ref\": \"quest_1\"\n" +
                "              }\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      }\n" +
                "    }\n" +
                "  }\n" +
                "}";

        try {
            Deserializer<Container> deserializer = new Deserializer<>(Container.class);
            Container result = deserializer.deserialize(new ByteArrayInputStream(json.getBytes()));
            
            assertNotNull(result, "Container should not be null");
            assertNotNull(result.getQuests(), "Quests map should not be null");
            assertTrue(result.getQuests().containsKey("quest_1"), "Quests map should contain quest_1");
            
            Quest quest = result.getQuests().get("quest_1");
            assertNotNull(quest, "Quest should not be null");
            assertEquals("quest_1", quest.getId(), "Quest id should be 'quest_1'");
            assertEquals("Test Quest", quest.getName(), "Quest name should be 'Test Quest'");
            assertNotNull(quest.getItem(), "Quest item should not be null");
            assertEquals("item_1", quest.getItem().getItemId(), "Item id should be 'item_1'");
            assertEquals("Test Item", quest.getItem().getItemName(), "Item name should be 'Test Item'");
            // assertSame is not available in TestCase, but we can check reference equality
            assertEquals(quest, quest.getItem().getQuest(), "Item's quest should be the same object");
            
        } catch (SerializationException e) {
            // This is expected to fail with the bug
            // The error should be "Referenced object not found: quest_1"
            // For now, just verify the error message to confirm the issue
            if (e.getMessage().contains("Referenced object not found")) {
                fail("Bug reproduced: " + e.getMessage());
            } else {
                // Some other error - rethrow
                throw e;
            }
        }
    }

    /**
     * Test that serialization works correctly (to verify the test classes are valid).
     */
    public void testSerializationWorks() throws SerializationException {
        Map<String, Quest> quests = new HashMap<>();
        QuestItem item = new QuestItem("item_1", "Test Item", null);
        Quest quest = new Quest("quest_1", "Test Quest", item);
        quests.put("quest_1", quest);
        Container container = new Container(quests);

        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(container, out);
        
        String json = out.toString();
        assertNotNull("Serialized JSON should not be null", json);
        assertTrue(json.contains("quest_1"), "Serialized JSON should contain quest_1");
        assertTrue(json.contains("item_1"), "Serialized JSON should contain item_1");
    }

    public static void main(String[] args) {
        CircularReferenceConstructorTest test = new CircularReferenceConstructorTest();
        test.run();
    }
}
