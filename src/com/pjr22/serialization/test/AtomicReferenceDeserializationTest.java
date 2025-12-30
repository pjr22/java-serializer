package com.pjr22.serialization.test;

import com.pjr22.serialization.core.Deserializer;
import com.pjr22.serialization.core.Serializer;
import com.pjr22.serialization.core.SerializationException;
import com.pjr22.serialization.test.data.CombatStance;
import com.pjr22.serialization.test.data.PersonWithAtomicEnum;
import com.pjr22.serialization.test.data.Status;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

/**
 * Test class for AtomicReference deserialization with parameterized types.
 * Specifically tests that AtomicReference<Enum> is properly deserialized.
 */
public class AtomicReferenceDeserializationTest extends TestCase {

    public void testAtomicReferenceWithEnumRoundTrip() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        
        // Create a person with AtomicReference to enum types
        PersonWithAtomicEnum original = new PersonWithAtomicEnum(
            "Test Player", 
            CombatStance.AGGRESSIVE, 
            Status.ACTIVE
        );

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);

        String json = outputStream.toString();

        // Verify that JSON contains the enum values as strings
        assertTrue(json.contains("AGGRESSIVE"), "JSON should contain AGGRESSIVE enum value");
        assertTrue(json.contains("ACTIVE"), "JSON should contain ACTIVE enum value");

        Deserializer<PersonWithAtomicEnum> deserializer = new Deserializer<>(PersonWithAtomicEnum.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithAtomicEnum deserialized = deserializer.deserialize(inputStream);

        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals(original.getName(), deserialized.getName(), "Name should match");
        
        // Verify AtomicReference<CombatStance> was properly deserialized
        assertNotNull(deserialized.getCombatStance(), "CombatStance AtomicReference should not be null");
        assertNotNull(deserialized.getCombatStance().get(), "CombatStance value should not be null");
        assertEquals(
            CombatStance.class, 
            deserialized.getCombatStance().get().getClass(),
            "CombatStance should be of correct enum type"
        );
        assertEquals(
            original.getCombatStance().get(), 
            deserialized.getCombatStance().get(),
            "CombatStance enum value should match"
        );
        
        // Verify AtomicReference<Status> was properly deserialized
        assertNotNull(deserialized.getStatus(), "Status AtomicReference should not be null");
        assertNotNull(deserialized.getStatus().get(), "Status value should not be null");
        assertEquals(
            Status.class, 
            deserialized.getStatus().get().getClass(),
            "Status should be of correct enum type"
        );
        assertEquals(
            original.getStatus().get(), 
            deserialized.getStatus().get(),
            "Status enum value should match"
        );
    }

    public void testAtomicReferenceWithEnumDeserializationFromString() throws SerializationException, IOException {
        // Test that a simple string value in JSON is converted to the correct enum type
        String json = "{\n" +
            "  \"$id\": \"test_1\",\n" +
            "  \"$class\": \"com.pjr22.serialization.test.data.PersonWithAtomicEnum\",\n" +
            "  \"fields\": {\n" +
            "    \"name\": \"Test Player\",\n" +
            "    \"combatStance\": \"BALANCED\",\n" +
            "    \"status\": \"INACTIVE\"\n" +
            "  }\n" +
            "}";

        Deserializer<PersonWithAtomicEnum> deserializer = new Deserializer<>(PersonWithAtomicEnum.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithAtomicEnum deserialized = deserializer.deserialize(inputStream);

        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals("Test Player", deserialized.getName(), "Name should match");
        
        // Verify that string "BALANCED" was converted to CombatStance enum
        assertNotNull(deserialized.getCombatStance(), "CombatStance AtomicReference should not be null");
        assertNotNull(deserialized.getCombatStance().get(), "CombatStance value should not be null");
        assertEquals(
            CombatStance.class, 
            deserialized.getCombatStance().get().getClass(),
            "CombatStance should be of correct enum type, not String"
        );
        assertEquals(
            CombatStance.BALANCED, 
            deserialized.getCombatStance().get(),
            "CombatStance should be BALANCED enum value"
        );
        
        // Verify that string "INACTIVE" was converted to Status enum
        assertNotNull(deserialized.getStatus(), "Status AtomicReference should not be null");
        assertNotNull(deserialized.getStatus().get(), "Status value should not be null");
        assertEquals(
            Status.class, 
            deserialized.getStatus().get().getClass(),
            "Status should be of correct enum type, not String"
        );
        assertEquals(
            Status.INACTIVE, 
            deserialized.getStatus().get(),
            "Status should be INACTIVE enum value"
        );
    }

    public void testAtomicReferenceWithNullEnum() throws SerializationException, IOException {
        String json = "{\n" +
            "  \"$id\": \"test_1\",\n" +
            "  \"$class\": \"com.pjr22.serialization.test.data.PersonWithAtomicEnum\",\n" +
            "  \"fields\": {\n" +
            "    \"name\": \"Test Player\",\n" +
            "    \"combatStance\": null,\n" +
            "    \"status\": null\n" +
            "  }\n" +
            "}";

        Deserializer<PersonWithAtomicEnum> deserializer = new Deserializer<>(PersonWithAtomicEnum.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithAtomicEnum deserialized = deserializer.deserialize(inputStream);

        assertNotNull(deserialized, "Deserialized object should not be null");
        assertNotNull(deserialized.getCombatStance(), "CombatStance AtomicReference should not be null");
        assertNull(deserialized.getCombatStance().get(), "CombatStance value should be null");
        assertNotNull(deserialized.getStatus(), "Status AtomicReference should not be null");
        assertNull(deserialized.getStatus().get(), "Status value should be null");
    }

    public static void main(String[] args) {
        AtomicReferenceDeserializationTest test = new AtomicReferenceDeserializationTest();
        test.run();
    }
}
