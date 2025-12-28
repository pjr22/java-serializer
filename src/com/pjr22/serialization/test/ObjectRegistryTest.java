package com.pjr22.serialization.test;

import com.pjr22.serialization.registry.ObjectRegistry;
import com.pjr22.serialization.test.data.SimplePerson;

/**
 * Test class for ObjectRegistry.
 */
public class ObjectRegistryTest extends TestCase {

    public void testRegisterObject() {
        ObjectRegistry registry = new ObjectRegistry();
        SimplePerson person = new SimplePerson("John", 30, 50000.0, true);
        String objectId = "REV-A_1001_com.example.Person";

        registry.register(objectId, person);
        assertTrue(registry.contains(objectId), "Registry should contain the registered object");
    }

    public void testRetrieveObject() {
        ObjectRegistry registry = new ObjectRegistry();
        SimplePerson person = new SimplePerson("Jane", 25, 60000.0, false);
        String objectId = "REV-A_1002_com.example.Person";

        registry.register(objectId, person);
        Object retrieved = registry.get(objectId);

        assertNotNull(retrieved, "Retrieved object should not be null");
        assertTrue(retrieved instanceof SimplePerson, "Retrieved object should be SimplePerson");
        assertEquals(person, retrieved, "Retrieved object should be the same as registered");
    }

    public void testContainsReturnsFalseForUnregistered() {
        ObjectRegistry registry = new ObjectRegistry();
        assertFalse(registry.contains("NON_EXISTENT_ID"), "Registry should not contain unregistered ID");
    }

    public void testGetReturnsNullForUnregistered() {
        ObjectRegistry registry = new ObjectRegistry();
        assertNull(registry.get("NON_EXISTENT_ID"), "Registry should return null for unregistered ID");
    }

    public void testRegisterMultipleObjects() {
        ObjectRegistry registry = new ObjectRegistry();
        SimplePerson person1 = new SimplePerson("John", 30, 50000.0, true);
        SimplePerson person2 = new SimplePerson("Jane", 25, 60000.0, false);
        SimplePerson person3 = new SimplePerson("Bob", 35, 70000.0, true);

        registry.register("ID_1", person1);
        registry.register("ID_2", person2);
        registry.register("ID_3", person3);

        assertTrue(registry.contains("ID_1"), "Registry should contain ID_1");
        assertTrue(registry.contains("ID_2"), "Registry should contain ID_2");
        assertTrue(registry.contains("ID_3"), "Registry should contain ID_3");
    }

    public void testOverwriteExistingObject() {
        ObjectRegistry registry = new ObjectRegistry();
        SimplePerson person1 = new SimplePerson("John", 30, 50000.0, true);
        SimplePerson person2 = new SimplePerson("Jane", 25, 60000.0, false);
        String objectId = "REV-A_1001_com.example.Person";

        registry.register(objectId, person1);
        registry.register(objectId, person2);

        Object retrieved = registry.get(objectId);
        assertEquals(person2, retrieved, "Retrieved object should be the overwritten value");
    }

    public void testClearRegistry() {
        ObjectRegistry registry = new ObjectRegistry();
        SimplePerson person = new SimplePerson("John", 30, 50000.0, true);

        registry.register("ID_1", person);
        assertTrue(registry.contains("ID_1"), "Registry should contain object before clear");

        registry.clear();
        assertFalse(registry.contains("ID_1"), "Registry should not contain object after clear");
    }

    public void testGetAllObjectIds() {
        ObjectRegistry registry = new ObjectRegistry();
        SimplePerson person1 = new SimplePerson("John", 30, 50000.0, true);
        SimplePerson person2 = new SimplePerson("Jane", 25, 60000.0, false);

        registry.register("ID_1", person1);
        registry.register("ID_2", person2);

        var ids = registry.getAllObjectIds();
        assertTrue(ids.contains("ID_1"), "IDs should contain ID_1");
        assertTrue(ids.contains("ID_2"), "IDs should contain ID_2");
        assertEquals(2, ids.size(), "Should have 2 IDs");
    }

    public void testGetAllObjectIdsReturnsEmptyForEmptyRegistry() {
        ObjectRegistry registry = new ObjectRegistry();
        var ids = registry.getAllObjectIds();
        assertTrue(ids.isEmpty(), "IDs should be empty for empty registry");
    }

    public void testRegisterNullObject() {
        ObjectRegistry registry = new ObjectRegistry();
        String objectId = "REV-A_1001_com.example.Person";

        registry.register(objectId, null);
        assertTrue(registry.contains(objectId), "Registry should allow null object registration");
        assertNull(registry.get(objectId), "Retrieved null object should be null");
    }

    public void testSize() {
        ObjectRegistry registry = new ObjectRegistry();

        assertEquals(0, registry.size(), "Empty registry should have size 0");

        registry.register("ID_1", new SimplePerson("John", 30, 50000.0, true));
        assertEquals(1, registry.size(), "Registry should have size 1");

        registry.register("ID_2", new SimplePerson("Jane", 25, 60000.0, false));
        assertEquals(2, registry.size(), "Registry should have size 2");

        registry.clear();
        assertEquals(0, registry.size(), "Cleared registry should have size 0");
    }

    public static void main(String[] args) {
        ObjectRegistryTest test = new ObjectRegistryTest();
        test.run();
    }
}
