package com.pjr22.serialization.test;

/**
 * Main test runner that executes all test classes.
 */
public class TestRunner {

    private static int totalPassed = 0;
    private static int totalFailed = 0;

    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("Java Serializer/Deserializer Test Suite");
        System.out.println("========================================");
        System.out.println();

        // Note: The following tests will fail until the implementation is complete.
        // This is expected in TDD - tests are written first, then implementation follows.

        runTest("ObjectIdGeneratorTest", ObjectIdGeneratorTest::new);
        runTest("FieldInspectorTest", FieldInspectorTest::new);
        runTest("ObjectRegistryTest", ObjectRegistryTest::new);
        runTest("ValueSerializerTest", ValueSerializerTest::new);
        runTest("SerializerTest", SerializerTest::new);
        runTest("DeserializerTest", DeserializerTest::new);
        runTest("IntegrationTest", IntegrationTest::new);
        runTest("CollectionDeserializationTest", CollectionDeserializationTest::new);
        runTest("MapKeyTypeDeserializationTest", MapKeyTypeDeserializationTest::new);
        runTest("AtomicReferenceDeserializationTest", AtomicReferenceDeserializationTest::new);
        runTest("CircularReferenceConstructorTest", CircularReferenceConstructorTest::new);
        runTest("JdkMapSerializationTest", JdkMapSerializationTest::new);

        System.out.println();
        System.out.println("========================================");
        System.out.println("Test Summary");
        System.out.println("========================================");
        System.out.println("Total Passed: " + totalPassed);
        System.out.println("Total Failed: " + totalFailed);
        System.out.println("========================================");

        if (totalFailed > 0) {
            System.out.println();
            System.out.println("NOTE: Tests are expected to fail until implementation is complete.");
            System.out.println("This is part of the Test-Driven Development (TDD) approach.");
        }
    }

    private static void runTest(String testName, TestFactory factory) {
        try {
            System.out.println();
            System.out.println("--- Running " + testName + " ---");
            TestCase test = factory.create();
            test.run();
            totalPassed++;
            System.out.println("--- " + testName + " completed ---");
        } catch (Exception e) {
            totalFailed++;
            System.out.println("--- " + testName + " FAILED ---");
            e.printStackTrace();
        }
    }

    @FunctionalInterface
    private interface TestFactory {
        TestCase create();
    }
}
