package com.pjr22.serialization.test;

import com.pjr22.serialization.core.Deserializer;
import com.pjr22.serialization.core.Serializer;
import com.pjr22.serialization.test.data.PersonWithVariousCollections;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Tests for flexible collection deserialization.
 * Verifies that collections are deserialized to the correct types based on field types.
 */
public class CollectionDeserializationTest extends TestCase {

    public void testQueueDeserialization() throws Exception {
        PersonWithVariousCollections original = new PersonWithVariousCollections();
        original.setName("TestPerson");
        original.getActiveEffects().add("effect1");
        original.getActiveEffects().add("effect2");
        original.getActiveEffects().add("effect3");

        // Serialize
        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);
        String json = outputStream.toString();

        // Deserialize
        Deserializer<PersonWithVariousCollections> deserializer =
            new Deserializer<>(PersonWithVariousCollections.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithVariousCollections deserialized = deserializer.deserialize(inputStream);

        // Verify
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals("TestPerson", deserialized.getName());
        assertNotNull(deserialized.getActiveEffects(), "ActiveEffects should not be null");
        assertEquals(3, deserialized.getActiveEffects().size(), "ActiveEffects should have 3 elements");
        assertTrue(deserialized.getActiveEffects().contains("effect1"), "ActiveEffects should contain effect1");
        assertTrue(deserialized.getActiveEffects().contains("effect2"), "ActiveEffects should contain effect2");
        assertTrue(deserialized.getActiveEffects().contains("effect3"), "ActiveEffects should contain effect3");
        
        // Verify the type is correct (Queue interface should deserialize to LinkedList)
        assertTrue(deserialized.getActiveEffects() instanceof java.util.LinkedList, "ActiveEffects should be LinkedList");
    }

    public void testDequeDeserialization() throws Exception {
        PersonWithVariousCollections original = new PersonWithVariousCollections();
        original.setName("TestPerson");
        original.getHistory().addFirst("event1");
        original.getHistory().addLast("event2");
        original.getHistory().addFirst("event0");

        // Serialize
        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);
        String json = outputStream.toString();

        // Deserialize
        Deserializer<PersonWithVariousCollections> deserializer =
            new Deserializer<>(PersonWithVariousCollections.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithVariousCollections deserialized = deserializer.deserialize(inputStream);

        // Verify
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertNotNull(deserialized.getHistory(), "History should not be null");
        assertEquals(3, deserialized.getHistory().size(), "History should have 3 elements");
        assertTrue(deserialized.getHistory().contains("event0"), "History should contain event0");
        assertTrue(deserialized.getHistory().contains("event1"), "History should contain event1");
        assertTrue(deserialized.getHistory().contains("event2"), "History should contain event2");
        
        // Verify the type is correct (Deque interface should deserialize to ArrayDeque)
        assertTrue(deserialized.getHistory() instanceof java.util.ArrayDeque, "History should be ArrayDeque");
    }

    public void testListDeserialization() throws Exception {
        PersonWithVariousCollections original = new PersonWithVariousCollections();
        original.setName("TestPerson");
        original.getFriends().add("friend1");
        original.getFriends().add("friend2");
        original.getFriends().add("friend3");

        // Serialize
        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);
        String json = outputStream.toString();

        // Deserialize
        Deserializer<PersonWithVariousCollections> deserializer =
            new Deserializer<>(PersonWithVariousCollections.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithVariousCollections deserialized = deserializer.deserialize(inputStream);

        // Verify
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertNotNull(deserialized.getFriends(), "Friends should not be null");
        assertEquals(3, deserialized.getFriends().size(), "Friends should have 3 elements");
        assertEquals("friend1", deserialized.getFriends().get(0));
        assertEquals("friend2", deserialized.getFriends().get(1));
        assertEquals("friend3", deserialized.getFriends().get(2));
        
        // Verify the type is correct (List interface should deserialize to ArrayList)
        assertTrue(deserialized.getFriends() instanceof java.util.ArrayList, "Friends should be ArrayList");
    }

    public void testSetDeserialization() throws Exception {
        PersonWithVariousCollections original = new PersonWithVariousCollections();
        original.setName("TestPerson");
        original.getTags().add("tag1");
        original.getTags().add("tag2");
        original.getTags().add("tag3");

        // Serialize
        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);
        String json = outputStream.toString();

        // Deserialize
        Deserializer<PersonWithVariousCollections> deserializer =
            new Deserializer<>(PersonWithVariousCollections.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithVariousCollections deserialized = deserializer.deserialize(inputStream);

        // Verify
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertNotNull(deserialized.getTags(), "Tags should not be null");
        assertEquals(3, deserialized.getTags().size(), "Tags should have 3 elements");
        assertTrue(deserialized.getTags().contains("tag1"), "Tags should contain tag1");
        assertTrue(deserialized.getTags().contains("tag2"), "Tags should contain tag2");
        assertTrue(deserialized.getTags().contains("tag3"), "Tags should contain tag3");
        
        // Verify the type is correct (Set interface should deserialize to LinkedHashSet)
        assertTrue(deserialized.getTags() instanceof java.util.LinkedHashSet, "Tags should be LinkedHashSet");
    }

    public void testSortedSetDeserialization() throws Exception {
        PersonWithVariousCollections original = new PersonWithVariousCollections();
        original.setName("TestPerson");
        original.getSortedItems().add("zebra");
        original.getSortedItems().add("apple");
        original.getSortedItems().add("banana");

        // Serialize
        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);
        String json = outputStream.toString();

        // Deserialize
        Deserializer<PersonWithVariousCollections> deserializer =
            new Deserializer<>(PersonWithVariousCollections.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithVariousCollections deserialized = deserializer.deserialize(inputStream);

        // Verify
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertNotNull(deserialized.getSortedItems(), "SortedItems should not be null");
        assertEquals(3, deserialized.getSortedItems().size(), "SortedItems should have 3 elements");
        
        // Verify the elements are sorted
        Object[] items = deserialized.getSortedItems().toArray();
        assertEquals("apple", items[0]);
        assertEquals("banana", items[1]);
        assertEquals("zebra", items[2]);
        
        // Verify the type is correct (SortedSet interface should deserialize to TreeSet)
        assertTrue(deserialized.getSortedItems() instanceof java.util.TreeSet, "SortedItems should be TreeSet");
    }

    public void testConcreteArrayListDeserialization() throws Exception {
        PersonWithVariousCollections original = new PersonWithVariousCollections();
        original.setName("TestPerson");
        original.getConcreteList().add("item1");
        original.getConcreteList().add("item2");

        // Serialize
        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);
        String json = outputStream.toString();

        // Deserialize
        Deserializer<PersonWithVariousCollections> deserializer =
            new Deserializer<>(PersonWithVariousCollections.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithVariousCollections deserialized = deserializer.deserialize(inputStream);

        // Verify
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertNotNull(deserialized.getConcreteList(), "ConcreteList should not be null");
        assertEquals(2, deserialized.getConcreteList().size(), "ConcreteList should have 2 elements");
        assertTrue(deserialized.getConcreteList() instanceof java.util.ArrayList, "ConcreteList should be ArrayList");
    }

    public void testConcreteLinkedListDeserialization() throws Exception {
        PersonWithVariousCollections original = new PersonWithVariousCollections();
        original.setName("TestPerson");
        original.getConcreteLinkedList().add("item1");
        original.getConcreteLinkedList().add("item2");

        // Serialize
        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);
        String json = outputStream.toString();

        // Deserialize
        Deserializer<PersonWithVariousCollections> deserializer =
            new Deserializer<>(PersonWithVariousCollections.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithVariousCollections deserialized = deserializer.deserialize(inputStream);

        // Verify
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertNotNull(deserialized.getConcreteLinkedList(), "ConcreteLinkedList should not be null");
        assertEquals(2, deserialized.getConcreteLinkedList().size(), "ConcreteLinkedList should have 2 elements");
        assertTrue(deserialized.getConcreteLinkedList() instanceof java.util.LinkedList, "ConcreteLinkedList should be LinkedList");
    }

    public void testConcreteHashSetDeserialization() throws Exception {
        PersonWithVariousCollections original = new PersonWithVariousCollections();
        original.setName("TestPerson");
        original.getConcreteHashSet().add("item1");
        original.getConcreteHashSet().add("item2");

        // Serialize
        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);
        String json = outputStream.toString();

        // Deserialize
        Deserializer<PersonWithVariousCollections> deserializer =
            new Deserializer<>(PersonWithVariousCollections.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithVariousCollections deserialized = deserializer.deserialize(inputStream);

        // Verify
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertNotNull(deserialized.getConcreteHashSet(), "ConcreteHashSet should not be null");
        assertEquals(2, deserialized.getConcreteHashSet().size(), "ConcreteHashSet should have 2 elements");
        assertTrue(deserialized.getConcreteHashSet() instanceof java.util.HashSet, "ConcreteHashSet should be HashSet");
    }

    public void testConcreteLinkedHashSetDeserialization() throws Exception {
        PersonWithVariousCollections original = new PersonWithVariousCollections();
        original.setName("TestPerson");
        original.getConcreteLinkedHashSet().add("item1");
        original.getConcreteLinkedHashSet().add("item2");

        // Serialize
        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);
        String json = outputStream.toString();

        // Deserialize
        Deserializer<PersonWithVariousCollections> deserializer =
            new Deserializer<>(PersonWithVariousCollections.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithVariousCollections deserialized = deserializer.deserialize(inputStream);

        // Verify
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertNotNull(deserialized.getConcreteLinkedHashSet(), "ConcreteLinkedHashSet should not be null");
        assertEquals(2, deserialized.getConcreteLinkedHashSet().size(), "ConcreteLinkedHashSet should have 2 elements");
        assertTrue(deserialized.getConcreteLinkedHashSet() instanceof java.util.LinkedHashSet, "ConcreteLinkedHashSet should be LinkedHashSet");
    }

    public void testConcreteTreeSetDeserialization() throws Exception {
        PersonWithVariousCollections original = new PersonWithVariousCollections();
        original.setName("TestPerson");
        original.getConcreteTreeSet().add("zebra");
        original.getConcreteTreeSet().add("apple");

        // Serialize
        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);
        String json = outputStream.toString();

        // Deserialize
        Deserializer<PersonWithVariousCollections> deserializer =
            new Deserializer<>(PersonWithVariousCollections.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithVariousCollections deserialized = deserializer.deserialize(inputStream);

        // Verify
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertNotNull(deserialized.getConcreteTreeSet(), "ConcreteTreeSet should not be null");
        assertEquals(2, deserialized.getConcreteTreeSet().size(), "ConcreteTreeSet should have 2 elements");
        assertTrue(deserialized.getConcreteTreeSet() instanceof java.util.TreeSet, "ConcreteTreeSet should be TreeSet");
        
        // Verify elements are sorted
        Object[] items = deserialized.getConcreteTreeSet().toArray();
        assertEquals("apple", items[0]);
        assertEquals("zebra", items[1]);
    }

    public void testConcretePriorityQueueDeserialization() throws Exception {
        PersonWithVariousCollections original = new PersonWithVariousCollections();
        original.setName("TestPerson");
        original.getConcretePriorityQueue().add("zebra");
        original.getConcretePriorityQueue().add("apple");

        // Serialize
        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);
        String json = outputStream.toString();

        // Deserialize
        Deserializer<PersonWithVariousCollections> deserializer =
            new Deserializer<>(PersonWithVariousCollections.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithVariousCollections deserialized = deserializer.deserialize(inputStream);

        // Verify
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertNotNull(deserialized.getConcretePriorityQueue(), "ConcretePriorityQueue should not be null");
        assertEquals(2, deserialized.getConcretePriorityQueue().size(), "ConcretePriorityQueue should have 2 elements");
        assertTrue(deserialized.getConcretePriorityQueue() instanceof java.util.PriorityQueue, "ConcretePriorityQueue should be PriorityQueue");
    }

    public void testConcreteArrayDequeDeserialization() throws Exception {
        PersonWithVariousCollections original = new PersonWithVariousCollections();
        original.setName("TestPerson");
        original.getConcreteArrayDeque().addFirst("first");
        original.getConcreteArrayDeque().addLast("last");

        // Serialize
        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);
        String json = outputStream.toString();

        // Deserialize
        Deserializer<PersonWithVariousCollections> deserializer =
            new Deserializer<>(PersonWithVariousCollections.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithVariousCollections deserialized = deserializer.deserialize(inputStream);

        // Verify
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertNotNull(deserialized.getConcreteArrayDeque(), "ConcreteArrayDeque should not be null");
        assertEquals(2, deserialized.getConcreteArrayDeque().size(), "ConcreteArrayDeque should have 2 elements");
        assertTrue(deserialized.getConcreteArrayDeque() instanceof java.util.ArrayDeque, "ConcreteArrayDeque should be ArrayDeque");
    }

    public void testEmptyCollections() throws Exception {
        PersonWithVariousCollections original = new PersonWithVariousCollections();
        original.setName("TestPerson");

        // Serialize
        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);
        String json = outputStream.toString();

        // Deserialize
        Deserializer<PersonWithVariousCollections> deserializer =
            new Deserializer<>(PersonWithVariousCollections.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithVariousCollections deserialized = deserializer.deserialize(inputStream);

        // Verify all collections are empty but not null
        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals("TestPerson", deserialized.getName());
        assertNotNull(deserialized.getActiveEffects(), "ActiveEffects should not be null");
        assertTrue(deserialized.getActiveEffects().isEmpty(), "ActiveEffects should be empty");
        assertNotNull(deserialized.getHistory(), "History should not be null");
        assertTrue(deserialized.getHistory().isEmpty(), "History should be empty");
        assertNotNull(deserialized.getFriends(), "Friends should not be null");
        assertTrue(deserialized.getFriends().isEmpty(), "Friends should be empty");
        assertNotNull(deserialized.getTags(), "Tags should not be null");
        assertTrue(deserialized.getTags().isEmpty(), "Tags should be empty");
        assertNotNull(deserialized.getSortedItems(), "SortedItems should not be null");
        assertTrue(deserialized.getSortedItems().isEmpty(), "SortedItems should be empty");
    }

    public static void main(String[] args) {
        CollectionDeserializationTest test = new CollectionDeserializationTest();
        test.run();
    }
}
