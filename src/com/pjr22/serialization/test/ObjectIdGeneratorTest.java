package com.pjr22.serialization.test;

import com.pjr22.serialization.registry.ObjectIdGenerator;

/**
 * Test class for ObjectIdGenerator.
 */
public class ObjectIdGeneratorTest extends TestCase {

    public void testBasicIdGeneration() {
        ObjectIdGenerator generator = new ObjectIdGenerator("REV-A", 1001);
        String id = generator.generateId();
        assertEquals("REV-A_1001", id, "Generated ID should match expected format");
    }

    public void testSequentialCounterIncrement() {
        ObjectIdGenerator generator = new ObjectIdGenerator("REV-A", 1001);
        String id1 = generator.generateId();
        String id2 = generator.generateId();
        String id3 = generator.generateId();

        assertEquals("REV-A_1001", id1);
        assertEquals("REV-A_1002", id2);
        assertEquals("REV-A_1003", id3);
    }

    public void testDifferentSerializationKeys() {
        ObjectIdGenerator generator1 = new ObjectIdGenerator("REV-A", 1001);
        ObjectIdGenerator generator2 = new ObjectIdGenerator("REV-B", 2000);

        String id1 = generator1.generateId();
        String id2 = generator2.generateId();

        assertEquals("REV-A_1001", id1);
        assertEquals("REV-B_2000", id2);
    }

    public void testDifferentStartingIds() {
        ObjectIdGenerator generator1 = new ObjectIdGenerator("REV-A", 1001);
        ObjectIdGenerator generator2 = new ObjectIdGenerator("REV-A", 5000);

        String id1 = generator1.generateId();
        String id2 = generator2.generateId();

        assertEquals("REV-A_1001", id1);
        assertEquals("REV-A_5000", id2);
    }

    public void testIdFormatComponents() {
        ObjectIdGenerator generator = new ObjectIdGenerator("TEST-KEY", 42);
        String id = generator.generateId();

        assertTrue(id.startsWith("TEST-KEY_42"), "ID should start with key and counter");
    }

    public void testCounterIsIndependentPerGenerator() {
        ObjectIdGenerator generator1 = new ObjectIdGenerator("REV-A", 1001);
        ObjectIdGenerator generator2 = new ObjectIdGenerator("REV-A", 1001);

        String id1 = generator1.generateId();
        String id2 = generator2.generateId();

        assertEquals("REV-A_1001", id1);
        assertEquals("REV-A_1001", id2, "Different generators should have independent counters");
    }

    public void testEmptySerializationKey() {
        ObjectIdGenerator generator = new ObjectIdGenerator("", 1001);
        String id = generator.generateId();
        assertEquals("_1001", id, "Should handle empty serialization key");
    }

    public void testNegativeStartingId() {
        ObjectIdGenerator generator = new ObjectIdGenerator("REV-A", -5);
        String id = generator.generateId();
        assertEquals("REV-A_-5", id, "Should handle negative starting ID");
    }

    public void testZeroStartingId() {
        ObjectIdGenerator generator = new ObjectIdGenerator("REV-A", 0);
        String id = generator.generateId();
        assertEquals("REV-A_0", id, "Should handle zero starting ID");
    }

    public void testComplexClassName() {
        ObjectIdGenerator generator = new ObjectIdGenerator("REV-A", 1001);
        String id = generator.generateId();
        assertEquals("REV-A_1001", id);
    }

    public static void main(String[] args) {
        ObjectIdGeneratorTest test = new ObjectIdGeneratorTest();
        test.run();
    }
}
