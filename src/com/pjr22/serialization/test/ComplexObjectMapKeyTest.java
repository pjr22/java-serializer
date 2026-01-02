package com.pjr22.serialization.test;

import com.pjr22.serialization.core.Deserializer;
import com.pjr22.serialization.core.Serializer;
import com.pjr22.serialization.core.SerializationException;
import com.pjr22.serialization.test.data.Effect;
import com.pjr22.serialization.test.data.PersonWithEffectMap;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * Test case for Map<ComplexObject, Object> serialization.
 * This test reproduces the issue where complex object keys are serialized
 * using toString() instead of being properly serialized as objects.
 */
public class ComplexObjectMapKeyTest extends TestCase {

    public ComplexObjectMapKeyTest() {
    }

    /**
     * Test that complex object keys are properly serialized.
     * Currently, this test will fail because complex objects are serialized
     * using toString() instead of being properly serialized as objects.
     */
    public void testComplexObjectMapKeySerialization() {
        PersonWithEffectMap person = new PersonWithEffectMap();
        person.setName("Test Character");

        Effect effect1 = new Effect(
            "increased constitution",
            Effect.Attribute.Constitution,
            0.01,
            1800,
            486127,
            Effect.Type.FORTIFY_ATTRIBUTE,
            0,
            10
        );

        Effect effect2 = new Effect(
            "increased strength",
            Effect.Attribute.Strength,
            0.02,
            1200,
            123456,
            Effect.Type.FORTIFY_ATTRIBUTE,
            0,
            5
        );

        person.getActiveEffects().put(effect1, 10);
        person.getActiveEffects().put(effect2, 5);

        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            serializer.serialize(person, outputStream);
            String json = outputStream.toString();

            System.out.println("Serialized JSON:");
            System.out.println(json);

            // The current implementation uses toString() for complex object keys,
            // which produces output like:
            // "Effect [description=increased constitution, ...]"
            // This is NOT valid JSON and will cause deserialization to fail.

            // Check that we DON'T have the toString() output in the JSON
            assertFalse(json.contains("Effect [description=increased constitution"),
                "Complex object key is using toString() instead of proper serialization!");

            // After the fix, complex object keys should be serialized as references
            // like "$ref:obj1" or similar

        } catch (SerializationException e) {
            fail("Serialization failed: " + e.getMessage());
        }
    }

    /**
     * Test that complex object map can be deserialized correctly.
     * Currently, this test will fail because complex objects are serialized
     * using toString() which cannot be deserialized back to the original object.
     */
    public void testComplexObjectMapKeyDeserialization() {
        PersonWithEffectMap person = new PersonWithEffectMap();
        person.setName("Test Character");

        Effect effect1 = new Effect(
            "increased constitution",
            Effect.Attribute.Constitution,
            0.01,
            1800,
            486127,
            Effect.Type.FORTIFY_ATTRIBUTE,
            0,
            10
        );

        Effect effect2 = new Effect(
            "increased strength",
            Effect.Attribute.Strength,
            0.02,
            1200,
            123456,
            Effect.Type.FORTIFY_ATTRIBUTE,
            0,
            5
        );

        person.getActiveEffects().put(effect1, 10);
        person.getActiveEffects().put(effect2, 5);

        Serializer serializer = new Serializer("test", 1);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

        try {
            serializer.serialize(person, outputStream);
            String json = outputStream.toString();

            System.out.println("Serialized JSON:");
            System.out.println(json);

            Deserializer<PersonWithEffectMap> deserializer = new Deserializer<>(PersonWithEffectMap.class);
            ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
            PersonWithEffectMap result = deserializer.deserialize(inputStream);

            assertNotNull(result, "Deserialization returned null.");
            assertEquals("Test Character", result.getName(), "Name not deserialized correctly.");
            assertEquals(2, result.getActiveEffects().size(), "Active effects map size incorrect.");

            // Verify that the deserialized map has the correct keys and values
            // Note: This requires that the complex objects are properly reconstructed
            boolean foundEffect1 = false;
            boolean foundEffect2 = false;

            for (Effect effect : result.getActiveEffects().keySet()) {
                if (effect.getDescription().equals("increased constitution") &&
                    effect.getAttribute() == Effect.Attribute.Constitution) {
                    foundEffect1 = true;
                    assertEquals(10, result.getActiveEffects().get(effect), "Effect1 value incorrect.");
                }
                if (effect.getDescription().equals("increased strength") &&
                    effect.getAttribute() == Effect.Attribute.Strength) {
                    foundEffect2 = true;
                    assertEquals(5, result.getActiveEffects().get(effect), "Effect2 value incorrect.");
                }
            }

            assertTrue(foundEffect1, "Effect1 not found in deserialized map.");
            assertTrue(foundEffect2, "Effect2 not found in deserialized map.");

        } catch (SerializationException e) {
            fail("Deserialization failed: " + e.getMessage());
        }
    }

    public static void main(String[] args) {
        ComplexObjectMapKeyTest test = new ComplexObjectMapKeyTest();
        test.run();
    }
}
