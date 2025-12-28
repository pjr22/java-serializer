package com.pjr22.serialization.test;

/**
 * Simple test case base class for running tests without external dependencies.
 */
public abstract class TestCase {
    private int passed = 0;
    private int failed = 0;

    /**
     * Run all test methods in this test case.
     */
    public void run() {
        System.out.println("Running " + this.getClass().getSimpleName() + "...");
        java.lang.reflect.Method[] methods = this.getClass().getDeclaredMethods();
        for (java.lang.reflect.Method method : methods) {
            if (method.getName().startsWith("test") && method.getParameterCount() == 0) {
                try {
                    System.out.print("  " + method.getName() + "... ");
                    method.invoke(this);
                    System.out.println("PASSED");
                    passed++;
                } catch (Exception e) {
                    System.out.println("FAILED: " + e.getCause().getMessage());
                    e.getCause().printStackTrace();
                    failed++;
                }
            }
        }
        System.out.println("  Results: " + passed + " passed, " + failed + " failed");
    }

    /**
     * Assert that a condition is true.
     */
    protected void assertTrue(boolean condition, String message) {
        if (!condition) {
            throw new AssertionError(message);
        }
    }

    /**
     * Assert that two objects are equal.
     */
    protected void assertEquals(Object expected, Object actual, String message) {
        if (expected == null && actual == null) {
            return;
        }
        if (expected != null && expected.equals(actual)) {
            return;
        }
        throw new AssertionError(message + " - expected: " + expected + ", actual: " + actual);
    }

    /**
     * Assert that two objects are equal.
     */
    protected void assertEquals(Object expected, Object actual) {
        assertEquals(expected, actual, "Objects are not equal");
    }

    /**
     * Assert that two doubles are equal within a delta.
     */
    protected void assertEquals(double expected, double actual, double delta, String message) {
        if (Double.compare(expected, actual) > delta) {
            throw new AssertionError(message + " - expected: " + expected + ", actual: " + actual + ", delta: " + delta);
        }
    }

    /**
     * Assert that a condition is false.
     */
    protected void assertFalse(boolean condition, String message) {
        if (condition) {
            throw new AssertionError(message);
        }
    }

    /**
     * Assert that an object is null.
     */
    protected void assertNull(Object obj, String message) {
        if (obj != null) {
            throw new AssertionError(message);
        }
    }

    /**
     * Assert that an object is not null.
     */
    protected void assertNotNull(Object obj, String message) {
        if (obj == null) {
            throw new AssertionError(message);
        }
    }

    /**
     * Fail the test with a message.
     */
    protected void fail(String message) {
        throw new AssertionError(message);
    }
}
