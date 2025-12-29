package com.pjr22.serialization.registry;

/**
 * Generates unique object IDs for serialization.
 * IDs are generated in the format: {serializationKey}_{counter}
 * Each generator instance maintains its own independent counter.
 */
public class ObjectIdGenerator {

    private final String serializationKey;
    private int counter;

    /**
     * Creates a new ObjectIdGenerator with specified serialization key and starting ID.
     *
     * @param serializationKey prefix to use for all generated IDs
     * @param startingId starting value for the counter
     */
    public ObjectIdGenerator(String serializationKey, int startingId) {
        this.serializationKey = serializationKey;
        this.counter = startingId;
    }

    /**
     * Generates a unique object ID.
     * The ID format is: {serializationKey}_{counter}
     * The counter is incremented after each call.
     *
     * @return generated object ID
     */
    public String generateId() {
        String id = serializationKey + "_" + counter;
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
