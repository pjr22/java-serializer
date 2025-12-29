package com.pjr22.serialization.test;

import com.pjr22.serialization.core.Deserializer;
import com.pjr22.serialization.core.Serializer;
import com.pjr22.serialization.core.SerializationException;
import com.pjr22.serialization.test.data.PersonWithEnumMap;
import com.pjr22.serialization.test.data.PersonWithEnumMap.AttributeState;
import com.pjr22.serialization.test.data.PersonWithEnumMap.PrimaryAttribute;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Test case for Map<Enum, Object> serialization issue.
 * This test reproduces the issue where enum keys are double-quoted.
 */
public class EnumMapTest extends TestCase {

    public EnumMapTest() {
    }

    /**
     * Test that enum keys are not double-quoted in JSON output.
     */
    public void testEnumMapSerialization() {
        PersonWithEnumMap person = new PersonWithEnumMap();
        person.setName("Test Character");

        person.getAttributes().put(PrimaryAttribute.Charisma, new AttributeState(12, 1, 0));
        person.getAttributes().put(PrimaryAttribute.Constitution, new AttributeState(15, 0, 0));
        person.getAttributes().put(PrimaryAttribute.Strength, new AttributeState(10, 2, 5));

        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            serializer.serialize(person, outputStream);
            String json = outputStream.toString();

            System.out.println("Serialized JSON:");
            System.out.println(json);

            // Check that enum keys are NOT double-quoted
            // They should be "Charisma" NOT ""Charisma""
            assertFalse(json.contains("\"\"Charisma\"\""), "Enum keys are double-quoted! Found double-quoted keys in JSON output.");
            assertFalse(json.contains("\"\"Constitution\"\""), "Enum keys are double-quoted! Found double-quoted keys in JSON output.");
            assertFalse(json.contains("\"\"Strength\"\""), "Enum keys are double-quoted! Found double-quoted keys in JSON output.");

            // Check that enum keys are properly quoted (single quotes)
            assertTrue(json.contains("\"Charisma\""), "Enum keys are not properly quoted in JSON output.");
            assertTrue(json.contains("\"Constitution\""), "Enum keys are not properly quoted in JSON output.");
            assertTrue(json.contains("\"Strength\""), "Enum keys are not properly quoted in JSON output.");

        } catch (SerializationException e) {
            fail("Serialization failed: " + e.getMessage());
        }
    }

    /**
     * Test that enum map can be deserialized correctly.
     */
    public void testEnumMapDeserialization() {
        PersonWithEnumMap person = new PersonWithEnumMap();
        person.setName("Test Character");

        person.getAttributes().put(PrimaryAttribute.Charisma, new AttributeState(12, 1, 0));
        person.getAttributes().put(PrimaryAttribute.Constitution, new AttributeState(15, 0, 0));
        person.getAttributes().put(PrimaryAttribute.Strength, new AttributeState(10, 2, 5));

        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            serializer.serialize(person, outputStream);
            String json = outputStream.toString();

            // Fix the JSON if it has double-quoted keys (for testing purposes)
            String fixedJson = json.replace("\"\"Charisma\"\"", "\"Charisma\"")
                                   .replace("\"\"Constitution\"\"", "\"Constitution\"")
                                   .replace("\"\"Strength\"\"", "\"Strength\"")
                                   .replace("\"\"Dexterity\"\"", "\"Dexterity\"")
                                   .replace("\"\"Faith\"\"", "\"Faith\"")
                                   .replace("\"\"Intelligence\"\"", "\"Intelligence\"")
                                   .replace("\"\"Luck\"\"", "\"Luck\"");

            Deserializer<PersonWithEnumMap> deserializer = new Deserializer<>(PersonWithEnumMap.class);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(fixedJson.getBytes());
            PersonWithEnumMap result = deserializer.deserialize(inputStream);

            assertNotNull(result, "Deserialization returned null.");
            assertEquals("Test Character", result.getName(), "Name not deserialized correctly.");
            assertEquals(3, result.getAttributes().size(), "Attributes map size incorrect.");

            AttributeState charisma = result.getAttributes().get(PrimaryAttribute.Charisma);
            assertNotNull(charisma, "Charisma attribute not found in deserialized map.");
            assertEquals(12, charisma.getBaseValue(), "Charisma base value incorrect.");

        } catch (SerializationException e) {
            fail("Deserialization failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        EnumMapTest test = new EnumMapTest();
        test.run();
    }
}
