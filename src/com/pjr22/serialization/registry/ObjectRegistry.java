package com.pjr22.serialization.registry;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * Registry for storing objects during deserialization.
 * Objects are stored by their unique object ID and can be retrieved later.
 * This is necessary for handling object references and circular dependencies.
 */
public class ObjectRegistry {

    private final Map<String, Object> registry;

    /**
     * Creates a new empty ObjectRegistry.
     */
    public ObjectRegistry() {
        this.registry = new HashMap<>();
    }

    /**
     * Registers an object with the given object ID.
     * If an object with the same ID already exists, it will be overwritten.
     *
     * @param objectId the unique object ID
     * @param object   the object to register (can be null)
     */
    public void register(String objectId, Object object) {
        registry.put(objectId, object);
    }

    /**
     * Retrieves an object by its ID.
     *
     * @param objectId the unique object ID
     * @return the registered object, or null if not found or if the registered object is null
     */
    public Object get(String objectId) {
        return registry.get(objectId);
    }

    /**
     * Checks if an object with the given ID is registered.
     *
     * @param objectId the unique object ID
     * @return true if an object with the given ID is registered, false otherwise
     */
    public boolean contains(String objectId) {
        return registry.containsKey(objectId);
    }

    /**
     * Clears all registered objects from the registry.
     */
    public void clear() {
        registry.clear();
    }

    /**
     * Returns all registered object IDs.
     *
     * @return a set of all registered object IDs
     */
    public Set<String> getAllObjectIds() {
        return new HashSet<>(registry.keySet());
    }

    /**
     * Returns the number of registered objects.
     *
     * @return the size of the registry
     */
    public int size() {
        return registry.size();
    }
}
