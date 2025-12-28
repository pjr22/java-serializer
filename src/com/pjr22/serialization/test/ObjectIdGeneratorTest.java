package com.pjr22.serialization.test;

import com.pjr22.serialization.registry.ObjectIdGenerator;

/**
 * Test class for ObjectIdGenerator.
 */
public class ObjectIdGeneratorTest extends TestCase {

    public void testBasicIdGeneration() {
        ObjectIdGenerator generator = new ObjectIdGenerator("REV-A", 1001);
        String id = generator.generateId("com.example.Person");
        assertEquals("REV-A_1001_com.example.Person", id, "Generated ID should match expected format");
    }

    public void testSequentialCounterIncrement() {
        ObjectIdGenerator generator = new ObjectIdGenerator("REV-A", 1001);
        String id1 = generator.generateId("com.example.Person");
        String id2 = generator.generateId("com.example.Address");
        String id3 = generator.generateId("com.example.Department");

        assertEquals("REV-A_1001_com.example.Person", id1);
        assertEquals("REV-A_1002_com.example.Address", id2);
        assertEquals("REV-A_1003_com.example.Department", id3);
    }

    public void testDifferentSerializationKeys() {
        ObjectIdGenerator generator1 = new ObjectIdGenerator("REV-A", 1001);
        ObjectIdGenerator generator2 = new ObjectIdGenerator("REV-B", 2000);

        String id1 = generator1.generateId("com.example.Person");
        String id2 = generator2.generateId("com.example.Person");

        assertEquals("REV-A_1001_com.example.Person", id1);
        assertEquals("REV-B_2000_com.example.Person", id2);
    }

    public void testDifferentStartingIds() {
        ObjectIdGenerator generator1 = new ObjectIdGenerator("REV-A", 1001);
        ObjectIdGenerator generator2 = new ObjectIdGenerator("REV-A", 5000);

        String id1 = generator1.generateId("com.example.Person");
        String id2 = generator2.generateId("com.example.Person");

        assertEquals("REV-A_1001_com.example.Person", id1);
        assertEquals("REV-A_5000_com.example.Person", id2);
    }

    public void testIdFormatComponents() {
        ObjectIdGenerator generator = new ObjectIdGenerator("TEST-KEY", 42);
        String id = generator.generateId("org.myapp.MyClass");

        assertTrue(id.startsWith("TEST-KEY_42_"), "ID should start with key and counter");
        assertTrue(id.endsWith("org.myapp.MyClass"), "ID should end with class name");
    }

    public void testCounterIsIndependentPerGenerator() {
        ObjectIdGenerator generator1 = new ObjectIdGenerator("REV-A", 1001);
        ObjectIdGenerator generator2 = new ObjectIdGenerator("REV-A", 1001);

        String id1 = generator1.generateId("com.example.Person");
        String id2 = generator2.generateId("com.example.Person");

        assertEquals("REV-A_1001_com.example.Person", id1);
        assertEquals("REV-A_1001_com.example.Person", id2, "Different generators should have independent counters");
    }

    public void testEmptySerializationKey() {
        ObjectIdGenerator generator = new ObjectIdGenerator("", 1001);
        String id = generator.generateId("com.example.Person");
        assertEquals("_1001_com.example.Person", id, "Should handle empty serialization key");
    }

    public void testNegativeStartingId() {
        ObjectIdGenerator generator = new ObjectIdGenerator("REV-A", -5);
        String id = generator.generateId("com.example.Person");
        assertEquals("REV-A_-5_com.example.Person", id, "Should handle negative starting ID");
    }

    public void testZeroStartingId() {
        ObjectIdGenerator generator = new ObjectIdGenerator("REV-A", 0);
        String id = generator.generateId("com.example.Person");
        assertEquals("REV-A_0_com.example.Person", id, "Should handle zero starting ID");
    }

    public void testComplexClassName() {
        ObjectIdGenerator generator = new ObjectIdGenerator("REV-A", 1001);
        String id = generator.generateId("com.example.deep.nested.package.VeryComplexClassName");
        assertEquals("REV-A_1001_com.example.deep.nested.package.VeryComplexClassName", id);
    }

    public static void main(String[] args) {
        ObjectIdGeneratorTest test = new ObjectIdGeneratorTest();
        test.run();
    }
}
