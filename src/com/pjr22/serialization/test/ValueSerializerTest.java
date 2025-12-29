package com.pjr22.serialization.test;

import com.pjr22.serialization.util.ValueSerializer;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Test class for ValueSerializer utility.
 */
public class ValueSerializerTest extends TestCase {

    public void testCanSerializeAsValue() {
        // UUID - should be serializable as value
        assertTrue(ValueSerializer.canSerializeAsValue(UUID.class), "UUID should be serializable as value");
        
        // Date - should be serializable as value
        assertTrue(ValueSerializer.canSerializeAsValue(Date.class), "Date should be serializable as value");
        
        // Random - should be serializable as value
        assertTrue(ValueSerializer.canSerializeAsValue(Random.class), "Random should be serializable as value");
        
        // Non-JDK class - should NOT be serializable as value
        assertFalse(ValueSerializer.canSerializeAsValue(String.class), "String should NOT be serializable as value");
    }

    public void testSerializeAsValue() {
        // UUID - serialize as string
        UUID uuid = UUID.randomUUID();
        Object value = ValueSerializer.serializeAsValue(uuid);
        assertEquals(uuid.toString(), value, "UUID should be serialized as string");
        
        // Date - serialize as ISO 8601 string
        Date date = new Date();
        Object dateValue = ValueSerializer.serializeAsValue(date);
        assertTrue(dateValue instanceof String, "Date should be serialized as string");
        assertTrue(((String) dateValue).contains("T"), "Date string should contain 'T' separator");
        
        // Random - serialize as nextLong value (the value returned by nextLong)
        Random random = new Random(12345L);
        Object randomValue = ValueSerializer.serializeAsValue(random);
        assertTrue(randomValue instanceof Long, "Random should be serialized as Long");
        assertNotNull(randomValue, "Random serialized value should not be null");
    }

    public void testDeserializeFromValue() {
        // UUID - deserialize from string
        UUID uuid = UUID.randomUUID();
        String uuidString = uuid.toString();
        Object deserialized = ValueSerializer.deserializeFromValue(uuidString, UUID.class);
        assertEquals(uuid, deserialized, "UUID should be deserialized from string");
        
        // Date - deserialize from ISO 8601 string
        Date date = new Date();
        Object dateValue = ValueSerializer.serializeAsValue(date);
        Object dateDeserialized = ValueSerializer.deserializeFromValue(dateValue, Date.class);
        assertNotNull(dateDeserialized, "Date should be deserialized from string");
        assertEquals(date.getTime(), ((Date) dateDeserialized).getTime(), "Date timestamps should match");
        
        // Random - deserialize using nextLong approach
        // According to the pseudo code:
        // 1. Create original Random
        // 2. Serialize the Random (which calls nextLong() internally)
        // 3. Deserialize to create a new Random using the serialized value as seed
        // 4. Compare nextLong values from new Random constructed with serialized value
        Random originalRandom = new Random();
        
        // Serialize the Random instance (this calls nextLong() internally)
        Object serializedValue = ValueSerializer.serializeAsValue(originalRandom);
        assertNotNull(serializedValue, "Serialized value should not be null");
        assertTrue(serializedValue instanceof Long, "Serialized value should be a Long");
        
        // Deserialize to create a new Random
        Random deserializedRandom = ValueSerializer.deserializeFromValue(serializedValue, Random.class);
        assertNotNull(deserializedRandom, "Deserialized Random should not be null");
        
        // Compare nextLong values
        // Create a new Random with the serialized value as seed and get nextLong
        long originalNext = new Random((Long) serializedValue).nextLong();
        // Get nextLong from the deserialized Random
        long deserializedNext = deserializedRandom.nextLong();
        assertEquals(originalNext, deserializedNext, "nextLong values should match");
    }

    public static void main(String[] args) {
        ValueSerializerTest test = new ValueSerializerTest();
        test.run();
    }
}
