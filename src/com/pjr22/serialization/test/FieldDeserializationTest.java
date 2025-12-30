package com.pjr22.serialization.test;

import com.pjr22.serialization.core.Deserializer;
import com.pjr22.serialization.core.Serializer;
import com.pjr22.serialization.core.SerializationException;
import com.pjr22.serialization.test.data.PersonWithExtraField;
import com.pjr22.serialization.test.data.PersonWithFinalExtraField;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Test class to verify that all fields from JSON are set during deserialization,
 * including fields that are not covered by constructor parameters.
 */
public class FieldDeserializationTest extends TestCase {

    public void testDeserializeFieldNotInConstructor() throws SerializationException {
        // Create an object with all fields set
        PersonWithExtraField original = new PersonWithExtraField("John Doe", 30);
        original.setExtraField("This is an extra field");

        // Serialize it
        Serializer serializer = new Serializer("TEST", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);
        String json = outputStream.toString();

        System.out.println("Serialized JSON:");
        System.out.println(json);

        // Deserialize it
        Deserializer<PersonWithExtraField> deserializer = new Deserializer<>(PersonWithExtraField.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithExtraField result = deserializer.deserialize(inputStream);

        System.out.println("\nDeserialized object:");
        System.out.println(result);

        // Verify all fields are set correctly
        assertNotNull(result, "Deserialized object should not be null");
        assertEquals("John Doe", result.getName(), "Name should match");
        assertEquals(30, result.getAge(), "Age should match");
        assertEquals("This is an extra field", result.getExtraField(), 
            "Extra field should be set even though it's not in constructor");
    }

    public void testDeserializeImmutablePersonAllFields() throws SerializationException {
        // Test that ImmutablePerson (which has only a parameterized constructor)
        // gets all fields set correctly
        String json = """
            {
              "objects": [
                {
                  "id": "TEST_1_com.pjr22.serialization.test.data.ImmutablePerson",
                  "className": "com.pjr22.serialization.test.data.ImmutablePerson",
                  "fields": {
                    "name": "Alice",
                    "age": 25,
                    "active": true
                  },
                  "references": {}
                }
              ]
            }
            """;

        Deserializer<com.pjr22.serialization.test.data.ImmutablePerson> deserializer = 
            new Deserializer<>(com.pjr22.serialization.test.data.ImmutablePerson.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        com.pjr22.serialization.test.data.ImmutablePerson result = deserializer.deserialize(inputStream);

        assertNotNull(result, "Deserialized object should not be null");
        assertEquals("Alice", result.getName(), "Name should match");
        assertEquals(25, result.getAge(), "Age should match");
        assertTrue(result.isActive(), "Active should be true");
    }

    public void testDeserializeFinalFieldNotInConstructor() throws SerializationException {
        // Test that a final field NOT in constructor fails to be set
        String json = """
            {
              "objects": [
                {
                  "id": "TEST_1_com.pjr22.serialization.test.data.PersonWithFinalExtraField",
                  "className": "com.pjr22.serialization.test.data.PersonWithFinalExtraField",
                  "fields": {
                    "name": "Bob",
                    "age": 40,
                    "extraField": "This should be set but won't be"
                  },
                  "references": {}
                }
              ]
            }
            """;

        Deserializer<PersonWithFinalExtraField> deserializer =
            new Deserializer<>(PersonWithFinalExtraField.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithFinalExtraField result = deserializer.deserialize(inputStream);

        assertNotNull(result, "Deserialized object should not be null");
        assertEquals("Bob", result.getName(), "Name should match");
        assertEquals(40, result.getAge(), "Age should match");
        // This will FAIL because extraField is final and not in constructor
        // The deserializer will try to set it via reflection but will fail silently
        assertEquals("This should be set but won't be", result.getExtraField(),
            "Final field not in constructor SHOULD be set but currently fails");
    }

    public static void main(String[] args) {
        FieldDeserializationTest test = new FieldDeserializationTest();
        test.run();
    }
}
