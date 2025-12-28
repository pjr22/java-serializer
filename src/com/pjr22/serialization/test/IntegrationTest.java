package com.pjr22.serialization.test;

import com.pjr22.serialization.core.Serializer;
import com.pjr22.serialization.core.Deserializer;
import com.pjr22.serialization.core.SerializationException;
import com.pjr22.serialization.test.data.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

/**
 * Integration tests for end-to-end serialization and deserialization.
 */
public class IntegrationTest extends TestCase {

    public void testSimpleObjectRoundTrip() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        SimplePerson original = new SimplePerson("John Doe", 30, 75000.50, true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);

        String json = outputStream.toString();

        Deserializer<SimplePerson> deserializer = new Deserializer<>(SimplePerson.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        SimplePerson deserialized = deserializer.deserialize(inputStream);

        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals(original.getName(), deserialized.getName(), "Name should match");
        assertEquals(original.getAge(), deserialized.getAge(), "Age should match");
        assertEquals(original.getSalary(), deserialized.getSalary(), 0.001, "Salary should match");
        assertEquals(original.isActive(), deserialized.isActive(), "Active should match");
    }

    public void testObjectWithReferencesRoundTrip() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        Address address = new Address("123 Main St", "Springfield", "12345");
        Department department = new Department("Engineering", new BigDecimal("500000.00"), null);
        PersonWithReferences original = new PersonWithReferences("John Doe", address, department);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);

        String json = outputStream.toString();

        Deserializer<PersonWithReferences> deserializer = new Deserializer<>(PersonWithReferences.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithReferences deserialized = deserializer.deserialize(inputStream);

        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals(original.getName(), deserialized.getName(), "Name should match");
        assertNotNull(deserialized.getAddress(), "Address should not be null");
        assertEquals(original.getAddress().getStreet(), deserialized.getAddress().getStreet(), "Address street should match");
        assertEquals(original.getAddress().getCity(), deserialized.getAddress().getCity(), "Address city should match");
        assertEquals(original.getAddress().getZipCode(), deserialized.getAddress().getZipCode(), "Address zipCode should match");
        assertNotNull(deserialized.getDepartment(), "Department should not be null");
        assertEquals(original.getDepartment().getName(), deserialized.getDepartment().getName(), "Department name should match");
    }

    public void testObjectWithCollectionsRoundTrip() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        java.util.List<String> tags = java.util.List.of("developer", "senior", "java");
        java.util.List<Integer> scores = java.util.List.of(95, 87, 92);
        PersonWithCollections original = new PersonWithCollections("John Doe", tags, scores);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);

        String json = outputStream.toString();

        Deserializer<PersonWithCollections> deserializer = new Deserializer<>(PersonWithCollections.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithCollections deserialized = deserializer.deserialize(inputStream);

        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals(original.getName(), deserialized.getName(), "Name should match");
        assertEquals(original.getTags().size(), deserialized.getTags().size(), "Tags count should match");
        assertEquals(original.getScores().size(), deserialized.getScores().size(), "Scores count should match");
    }

    public void testObjectWithInheritanceRoundTrip() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        PersonWithInheritance original = new PersonWithInheritance("John", "Doe", 30, "john@example.com");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);

        String json = outputStream.toString();

        Deserializer<PersonWithInheritance> deserializer = new Deserializer<>(PersonWithInheritance.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithInheritance deserialized = deserializer.deserialize(inputStream);

        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals(original.getFirstName(), deserialized.getFirstName(), "FirstName should match");
        assertEquals(original.getLastName(), deserialized.getLastName(), "LastName should match");
        assertEquals(original.getAge(), deserialized.getAge(), "Age should match");
        assertEquals(original.getEmail(), deserialized.getEmail(), "Email should match");
    }

    public void testObjectWithEnumRoundTrip() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        PersonWithEnum original = new PersonWithEnum("John Doe", Status.ACTIVE);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);

        String json = outputStream.toString();

        Deserializer<PersonWithEnum> deserializer = new Deserializer<>(PersonWithEnum.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithEnum deserialized = deserializer.deserialize(inputStream);

        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals(original.getName(), deserialized.getName(), "Name should match");
        assertEquals(original.getStatus(), deserialized.getStatus(), "Status should match");
    }

    public void testImmutableObjectRoundTrip() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        ImmutablePerson original = new ImmutablePerson("John Doe", 30, true);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);

        String json = outputStream.toString();

        Deserializer<ImmutablePerson> deserializer = new Deserializer<>(ImmutablePerson.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        ImmutablePerson deserialized = deserializer.deserialize(inputStream);

        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals(original.getName(), deserialized.getName(), "Name should match");
        assertEquals(original.getAge(), deserialized.getAge(), "Age should match");
        assertEquals(original.isActive(), deserialized.isActive(), "Active should match");
    }

    public void testObjectWithNullFieldsRoundTrip() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        PersonWithNullFields original = new PersonWithNullFields("John Doe", null, null);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);

        String json = outputStream.toString();

        Deserializer<PersonWithNullFields> deserializer = new Deserializer<>(PersonWithNullFields.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithNullFields deserialized = deserializer.deserialize(inputStream);

        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals(original.getName(), deserialized.getName(), "Name should match");
        assertNull(deserialized.getEmail(), "Email should be null");
        assertNull(deserialized.getPhone(), "Phone should be null");
    }

    public void testObjectWithCircularReferenceRoundTrip() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        PersonWithCircularReference parent = new PersonWithCircularReference("Parent");
        PersonWithCircularReference child = new PersonWithCircularReference("Child", parent);
        parent.addChild(child);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(parent, outputStream);

        String json = outputStream.toString();

        Deserializer<PersonWithCircularReference> deserializer = new Deserializer<>(PersonWithCircularReference.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithCircularReference deserialized = deserializer.deserialize(inputStream);

        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals("Parent", deserialized.getName(), "Name should match");
        assertNotNull(deserialized.getChildren(), "Children should not be null");
        assertEquals(1, deserialized.getChildren().size(), "Should have 1 child");
        assertEquals("Child", deserialized.getChildren().get(0).getName(), "Child name should match");
    }

    public void testComplexObjectGraphRoundTrip() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);

        // Create a complex object graph
        Address address1 = new Address("123 Main St", "Springfield", "12345");
        Address address2 = new Address("456 Oak Ave", "Shelbyville", "67890");

        SimplePerson manager = new SimplePerson("Alice Manager", 45, 120000.0, true);
        SimplePerson employee1 = new SimplePerson("Bob Developer", 30, 75000.0, true);
        SimplePerson employee2 = new SimplePerson("Carol Engineer", 35, 85000.0, true);

        Department dept1 = new Department("Engineering", new BigDecimal("1000000.00"), manager);
        Department dept2 = new Department("QA", new BigDecimal("500000.00"), manager);

        PersonWithReferences person1 = new PersonWithReferences("Bob", address1, dept1);
        PersonWithReferences person2 = new PersonWithReferences("Carol", address2, dept2);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(person1, outputStream);

        String json = outputStream.toString();

        Deserializer<PersonWithReferences> deserializer = new Deserializer<>(PersonWithReferences.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithReferences deserialized = deserializer.deserialize(inputStream);

        assertNotNull(deserialized, "Deserialized object should not be null");
        assertEquals("Bob", deserialized.getName(), "Name should match");
        assertNotNull(deserialized.getAddress(), "Address should not be null");
        assertEquals("123 Main St", deserialized.getAddress().getStreet(), "Address street should match");
        assertNotNull(deserialized.getDepartment(), "Department should not be null");
        assertEquals("Engineering", deserialized.getDepartment().getName(), "Department name should match");
    }

    public static void main(String[] args) {
        IntegrationTest test = new IntegrationTest();
        test.run();
    }
}
