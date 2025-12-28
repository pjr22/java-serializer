package com.pjr22.serialization.registry;

/**
 * Generates unique object IDs for serialization.
 * IDs are generated in the format: {serializationKey}_{counter}_{className}
 * Each generator instance maintains its own independent counter.
 */
public class ObjectIdGenerator {

    private final String serializationKey;
    private int counter;

    /**
     * Creates a new ObjectIdGenerator with the specified serialization key and starting ID.
     *
     * @param serializationKey the prefix to use for all generated IDs
     * @param startingId the starting value for the counter
     */
    public ObjectIdGenerator(String serializationKey, int startingId) {
        this.serializationKey = serializationKey;
        this.counter = startingId;
    }

    /**
     * Generates a unique object ID for the given class name.
     * The ID format is: {serializationKey}_{counter}_{className}
     * The counter is incremented after each call.
     *
     * @param className the fully qualified class name
     * @return the generated object ID
     */
    public String generateId(String className) {
        String id = serializationKey + "_" + counter + "_" + className;
        counter++;
        return id;
    }

    /**
     * Returns the current counter value.
     *
     * @return the current counter value
     */
    public int getCounter() {
        return counter;
    }

    /**
     * Returns the serialization key used by this generator.
     *
     * @return the serialization key
     */
    public String getSerializationKey() {
        return serializationKey;
    }
}
