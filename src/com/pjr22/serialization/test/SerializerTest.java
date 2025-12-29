package com.pjr22.serialization.test;

import com.pjr22.serialization.core.Serializer;
import com.pjr22.serialization.core.SerializationException;
import com.pjr22.serialization.test.data.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Test class for Serializer.
 */
public class SerializerTest extends TestCase {

    public void testSerializeSimpleObject() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        SimplePerson person = new SimplePerson("John Doe", 30, 75000.50, true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(person, outputStream);

        String output = outputStream.toString();
        assertNotNull(output, "Serialized output should not be null");
        assertTrue(output.contains("John Doe"), "Output should contain name");
        assertTrue(output.contains("30"), "Output should contain age");
        assertTrue(output.contains("75000.5"), "Output should contain salary");
        assertTrue(output.contains("true"), "Output should contain active");
    }

    public void testSerializeObjectWithReferences() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        Address address = new Address("123 Main St", "Springfield", "12345");
        Department department = new Department("Engineering", new BigDecimal("500000.00"), null);
        PersonWithReferences person = new PersonWithReferences("John Doe", address, department);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(person, outputStream);

        String output = outputStream.toString();
        assertNotNull(output, "Serialized output should not be null");
        assertTrue(output.contains("John Doe"), "Output should contain name");
        assertTrue(output.contains("123 Main St"), "Output should contain address");
        assertTrue(output.contains("Engineering"), "Output should contain department");
    }

    public void testSerializeObjectWithCollections() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        List<String> tags = List.of("developer", "senior", "java");
        List<Integer> scores = List.of(95, 87, 92);
        PersonWithCollections person = new PersonWithCollections("John Doe", tags, scores);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(person, outputStream);

        String output = outputStream.toString();
        assertNotNull(output, "Serialized output should not be null");
        assertTrue(output.contains("developer"), "Output should contain tag");
        assertTrue(output.contains("95"), "Output should contain score");
    }

    public void testSerializeObjectWithMap() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        Map<String, String> properties = Map.of("key1", "value1", "key2", "value2");
        PersonWithMap person = new PersonWithMap("John Doe", properties);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(person, outputStream);

        String output = outputStream.toString();
        assertNotNull(output, "Serialized output should not be null");
        assertTrue(output.contains("key1"), "Output should contain map key");
        assertTrue(output.contains("value1"), "Output should contain map value");
    }

    public void testSerializeObjectWithArray() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        int[] values = new int[]{1, 2, 3, 4, 5};
        PersonWithArray person = new PersonWithArray("John Doe", values);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(person, outputStream);

        String output = outputStream.toString();
        assertNotNull(output, "Serialized output should not be null");
        assertTrue(output.contains("1"), "Output should contain array value");
    }

    public void testSerializeObjectWithEnum() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        PersonWithEnum person = new PersonWithEnum("John Doe", Status.ACTIVE);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(person, outputStream);

        String output = outputStream.toString();
        assertNotNull(output, "Serialized output should not be null");
        assertTrue(output.contains("ACTIVE"), "Output should contain enum value");
    }

    public void testSerializeObjectWithInheritance() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        PersonWithInheritance person = new PersonWithInheritance("John", "Doe", 30, "john@example.com");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(person, outputStream);

        String output = outputStream.toString();
        assertNotNull(output, "Serialized output should not be null");
        assertTrue(output.contains("John"), "Output should contain firstName");
        assertTrue(output.contains("Doe"), "Output should contain lastName");
        assertTrue(output.contains("30"), "Output should contain age");
        assertTrue(output.contains("john@example.com"), "Output should contain email");
    }

    public void testSerializeObjectWithCircularReference() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        PersonWithCircularReference parent = new PersonWithCircularReference("Parent");
        PersonWithCircularReference child = new PersonWithCircularReference("Child", parent);
        parent.addChild(child);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(parent, outputStream);

        String output = outputStream.toString();
        assertNotNull(output, "Serialized output should not be null");
        assertTrue(output.contains("Parent"), "Output should contain parent name");
        assertTrue(output.contains("Child"), "Output should contain child name");
    }

    public void testSerializeObjectWithAtomicTypes() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        PersonWithAtomic person = new PersonWithAtomic("John Doe", 42, true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(person, outputStream);

        String output = outputStream.toString();
        assertNotNull(output, "Serialized output should not be null");
        assertTrue(output.contains("42"), "Output should contain atomic integer");
        assertTrue(output.contains("true"), "Output should contain atomic boolean");
    }

    public void testSerializeObjectWithNullFields() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        PersonWithNullFields person = new PersonWithNullFields("John Doe", null, null);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(person, outputStream);

        String output = outputStream.toString();
        assertNotNull(output, "Serialized output should not be null");
        assertTrue(output.contains("null"), "Output should contain null values");
    }

    public void testExcludeStaticFields() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        PersonWithStaticTransient person = new PersonWithStaticTransient("John Doe", 30, "transient value");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(person, outputStream);

        String output = outputStream.toString();
        assertNotNull(output, "Serialized output should not be null");
        assertFalse(output.contains("STATIC_VALUE"), "Output should not contain static field value");
    }

    public void testExcludeTransientFields() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        PersonWithStaticTransient person = new PersonWithStaticTransient("John Doe", 30, "transient value");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(person, outputStream);

        String output = outputStream.toString();
        assertNotNull(output, "Serialized output should not be null");
        assertFalse(output.contains("transient value"), "Output should not contain transient field value");
    }

    public void testIncludeSerialVersionUID() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        PersonWithSerialVersionUID person = new PersonWithSerialVersionUID("John Doe", 30);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(person, outputStream);

        String output = outputStream.toString();
        assertNotNull(output, "Serialized output should not be null");
        assertTrue(output.contains("123456789"), "Output should contain serialVersionUID");
    }

    public void testGenerateObjectIds() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        SimplePerson person1 = new SimplePerson("John", 30, 50000.0, true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(person1, outputStream);

        String output = outputStream.toString();
        assertTrue(output.contains("REV-A_1001"), "Output should contain first object ID");
    }

    public void testSerializeEmptyObject() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        SimplePerson person = new SimplePerson();

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(person, outputStream);

        String output = outputStream.toString();
        assertNotNull(output, "Serialized output should not be null");
    }

    public void testSerializeMapWithCollectionValues() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        
        Map<String, List<String>> tagsByCategory = new HashMap<>();
        tagsByCategory.put("skills", List.of("java", "python", "javascript"));
        tagsByCategory.put("languages", List.of("english", "spanish"));
        tagsByCategory.put("empty", new ArrayList<>());
        
        PersonWithMapOfCollections person = new PersonWithMapOfCollections("John Doe", tagsByCategory);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(person, outputStream);

        String output = outputStream.toString();
        assertNotNull(output, "Serialized output should not be null");
        assertTrue(output.contains("skills"), "Output should contain map key");
        assertTrue(output.contains("java"), "Output should contain list element");
        
        // Verify that collections are serialized as arrays, not objects
        // Should NOT contain "$class" or "$id" for list values
        assertFalse(output.contains("\"skills\":{\"$id\":"), "List values should be serialized as arrays, not objects");
        assertFalse(output.contains("\"skills\":{\"$class\":"), "List values should be serialized as arrays, not objects");
        
        // Verify empty list is serialized as []
        assertTrue(output.contains("\"empty\":[]"), "Empty list should be serialized as []");
    }

    public static void main(String[] args) {
        SerializerTest test = new SerializerTest();
        test.run();
    }
}
