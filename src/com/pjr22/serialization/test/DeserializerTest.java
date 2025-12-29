package com.pjr22.serialization.test;

import com.pjr22.serialization.core.Deserializer;
import com.pjr22.serialization.core.Serializer;
import com.pjr22.serialization.core.SerializationException;
import com.pjr22.serialization.test.data.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class for Deserializer.
 */
public class DeserializerTest extends TestCase {

    public void testDeserializeSimpleObject() throws SerializationException, IOException {
        String json = """
            {
              "objects": [
                {
                  "id": "REV-A_1001_com.pjr22.serialization.test.data.SimplePerson",
                  "className": "com.pjr22.serialization.test.data.SimplePerson",
                  "fields": {
                    "name": "John Doe",
                    "age": 30,
                    "salary": 75000.5,
                    "active": true
                  },
                  "references": {}
                }
              ]
            }
            """;

        Deserializer<SimplePerson> deserializer = new Deserializer<>(SimplePerson.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        SimplePerson person = deserializer.deserialize(inputStream);

        assertNotNull(person, "Deserialized object should not be null");
        assertEquals("John Doe", person.getName(), "Name should match");
        assertEquals(30, person.getAge(), "Age should match");
        assertEquals(75000.5, person.getSalary(), 0.001, "Salary should match");
        assertTrue(person.isActive(), "Active should be true");
    }

    public void testDeserializeObjectWithReferences() throws SerializationException, IOException {
        String json = """
            {
              "objects": [
                {
                  "id": "REV-A_1001_com.pjr22.serialization.test.data.Address",
                  "className": "com.pjr22.serialization.test.data.Address",
                  "fields": {
                    "street": "123 Main St",
                    "city": "Springfield",
                    "zipCode": "12345"
                  },
                  "references": {}
                },
                {
                  "id": "REV-A_1002_com.pjr22.serialization.test.data.Department",
                  "className": "com.pjr22.serialization.test.data.Department",
                  "fields": {
                    "name": "Engineering",
                    "budget": 500000.0
                  },
                  "references": {}
                },
                {
                  "id": "REV-A_1003_com.pjr22.serialization.test.data.PersonWithReferences",
                  "className": "com.pjr22.serialization.test.data.PersonWithReferences",
                  "fields": {
                    "name": "John Doe"
                  },
                  "references": {
                    "address": "REV-A_1001_com.pjr22.serialization.test.data.Address",
                    "department": "REV-A_1002_com.pjr22.serialization.test.data.Department"
                  }
                }
              ]
            }
            """;

        Deserializer<PersonWithReferences> deserializer = new Deserializer<>(PersonWithReferences.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithReferences person = deserializer.deserialize(inputStream);

        assertNotNull(person, "Deserialized object should not be null");
        assertEquals("John Doe", person.getName(), "Name should match");
        assertNotNull(person.getAddress(), "Address should not be null");
        assertEquals("123 Main St", person.getAddress().getStreet(), "Address street should match");
        assertNotNull(person.getDepartment(), "Department should not be null");
        assertEquals("Engineering", person.getDepartment().getName(), "Department name should match");
    }

    public void testDeserializeObjectWithCollections() throws SerializationException, IOException {
        String json = """
            {
              "objects": [
                {
                  "id": "REV-A_1001_com.pjr22.serialization.test.data.PersonWithCollections",
                  "className": "com.pjr22.serialization.test.data.PersonWithCollections",
                  "fields": {
                    "name": "John Doe",
                    "tags": ["developer", "senior", "java"],
                    "scores": [95, 87, 92]
                  },
                  "references": {}
                }
              ]
            }
            """;

        Deserializer<PersonWithCollections> deserializer = new Deserializer<>(PersonWithCollections.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithCollections person = deserializer.deserialize(inputStream);

        assertNotNull(person, "Deserialized object should not be null");
        assertEquals("John Doe", person.getName(), "Name should match");
        assertNotNull(person.getTags(), "Tags should not be null");
        assertEquals(3, person.getTags().size(), "Should have 3 tags");
        assertEquals(3, person.getScores().size(), "Should have 3 scores");
    }

    public void testDeserializeObjectWithMap() throws SerializationException, IOException {
        String json = """
            {
              "objects": [
                {
                  "id": "REV-A_1001_com.pjr22.serialization.test.data.PersonWithMap",
                  "className": "com.pjr22.serialization.test.data.PersonWithMap",
                  "fields": {
                    "name": "John Doe",
                    "properties": {
                      "key1": "value1",
                      "key2": "value2"
                    }
                  },
                  "references": {}
                }
              ]
            }
            """;

        Deserializer<PersonWithMap> deserializer = new Deserializer<>(PersonWithMap.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithMap person = deserializer.deserialize(inputStream);

        assertNotNull(person, "Deserialized object should not be null");
        assertEquals("John Doe", person.getName(), "Name should match");
        assertNotNull(person.getProperties(), "Properties should not be null");
        assertEquals(2, person.getProperties().size(), "Should have 2 properties");
    }

    public void testDeserializeObjectWithArray() throws SerializationException, IOException {
        String json = """
            {
              "objects": [
                {
                  "id": "REV-A_1001_com.pjr22.serialization.test.data.PersonWithArray",
                  "className": "com.pjr22.serialization.test.data.PersonWithArray",
                  "fields": {
                    "name": "John Doe",
                    "values": [1, 2, 3, 4, 5]
                  },
                  "references": {}
                }
              ]
            }
            """;

        Deserializer<PersonWithArray> deserializer = new Deserializer<>(PersonWithArray.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithArray person = deserializer.deserialize(inputStream);

        assertNotNull(person, "Deserialized object should not be null");
        assertEquals("John Doe", person.getName(), "Name should match");
        assertNotNull(person.getValues(), "Values should not be null");
        assertEquals(5, person.getValues().length, "Should have 5 values");
    }

    public void testDeserializeObjectWithEnum() throws SerializationException, IOException {
        String json = """
            {
              "objects": [
                {
                  "id": "REV-A_1001_com.pjr22.serialization.test.data.PersonWithEnum",
                  "className": "com.pjr22.serialization.test.data.PersonWithEnum",
                  "fields": {
                    "name": "John Doe",
                    "status": "ACTIVE"
                  },
                  "references": {}
                }
              ]
            }
            """;

        Deserializer<PersonWithEnum> deserializer = new Deserializer<>(PersonWithEnum.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithEnum person = deserializer.deserialize(inputStream);

        assertNotNull(person, "Deserialized object should not be null");
        assertEquals("John Doe", person.getName(), "Name should match");
        assertEquals(Status.ACTIVE, person.getStatus(), "Status should be ACTIVE");
    }

    public void testDeserializeObjectWithInheritance() throws SerializationException, IOException {
        String json = """
            {
              "objects": [
                {
                  "id": "REV-A_1001_com.pjr22.serialization.test.data.PersonWithInheritance",
                  "className": "com.pjr22.serialization.test.data.PersonWithInheritance",
                  "fields": {
                    "firstName": "John",
                    "lastName": "Doe",
                    "age": 30,
                    "email": "john@example.com"
                  },
                  "references": {}
                }
              ]
            }
            """;

        Deserializer<PersonWithInheritance> deserializer = new Deserializer<>(PersonWithInheritance.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithInheritance person = deserializer.deserialize(inputStream);

        assertNotNull(person, "Deserialized object should not be null");
        assertEquals("John", person.getFirstName(), "FirstName should match");
        assertEquals("Doe", person.getLastName(), "LastName should match");
        assertEquals(30, person.getAge(), "Age should match");
        assertEquals("john@example.com", person.getEmail(), "Email should match");
    }

    public void testDeserializeObjectWithNullFields() throws SerializationException, IOException {
        String json = """
            {
              "objects": [
                {
                  "id": "REV-A_1001_com.pjr22.serialization.test.data.PersonWithNullFields",
                  "className": "com.pjr22.serialization.test.data.PersonWithNullFields",
                  "fields": {
                    "name": "John Doe",
                    "email": null,
                    "phone": null
                  },
                  "references": {}
                }
              ]
            }
            """;

        Deserializer<PersonWithNullFields> deserializer = new Deserializer<>(PersonWithNullFields.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithNullFields person = deserializer.deserialize(inputStream);

        assertNotNull(person, "Deserialized object should not be null");
        assertEquals("John Doe", person.getName(), "Name should match");
        assertNull(person.getEmail(), "Email should be null");
        assertNull(person.getPhone(), "Phone should be null");
    }

    public void testDeserializeImmutableObject() throws SerializationException, IOException {
        String json = """
            {
              "objects": [
                {
                  "id": "REV-A_1001_com.pjr22.serialization.test.data.ImmutablePerson",
                  "className": "com.pjr22.serialization.test.data.ImmutablePerson",
                  "fields": {
                    "name": "John Doe",
                    "age": 30,
                    "active": true
                  },
                  "references": {}
                }
              ]
            }
            """;

        Deserializer<ImmutablePerson> deserializer = new Deserializer<>(ImmutablePerson.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        ImmutablePerson person = deserializer.deserialize(inputStream);

        assertNotNull(person, "Deserialized object should not be null");
        assertEquals("John Doe", person.getName(), "Name should match");
        assertEquals(30, person.getAge(), "Age should match");
        assertTrue(person.isActive(), "Active should be true");
    }

    public void testHandleSerialVersionUIDMismatch() throws SerializationException, IOException {
        String json = """
            {
              "objects": [
                {
                  "id": "REV-A_1001_com.pjr22.serialization.test.data.PersonWithSerialVersionUID",
                  "className": "com.pjr22.serialization.test.data.PersonWithSerialVersionUID",
                  "serialVersionUID": "999999999L",
                  "fields": {
                    "name": "John Doe",
                    "age": 30
                  },
                  "references": {}
                }
              ]
            }
            """;

        Deserializer<PersonWithSerialVersionUID> deserializer = new Deserializer<>(PersonWithSerialVersionUID.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithSerialVersionUID person = deserializer.deserialize(inputStream);

        assertNotNull(person, "Deserialized object should not be null");
        assertEquals("John Doe", person.getName(), "Name should match");
        assertEquals(30, person.getAge(), "Age should match");
    }

    public void testDeserializeMapWithCollectionValues() throws SerializationException, IOException {
        Serializer serializer = new Serializer("REV-A", 1001);
        
        Map<String, List<String>> tagsByCategory = new HashMap<>();
        tagsByCategory.put("skills", List.of("java", "python", "javascript"));
        tagsByCategory.put("languages", List.of("english", "spanish"));
        tagsByCategory.put("empty", new ArrayList<>());
        
        PersonWithMapOfCollections original = new PersonWithMapOfCollections("John Doe", tagsByCategory);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);

        String json = outputStream.toString();

        Deserializer<PersonWithMapOfCollections> deserializer = new Deserializer<>(PersonWithMapOfCollections.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithMapOfCollections result = deserializer.deserialize(inputStream);

        assertNotNull(result, "Deserialization returned null.");
        assertEquals("John Doe", result.getName(), "Name not deserialized correctly.");
        assertEquals(3, result.getTagsByCategory().size(), "Map size incorrect.");
        
        List<String> skills = result.getTagsByCategory().get("skills");
        assertNotNull(skills, "Skills list not found.");
        assertEquals(3, skills.size(), "Skills list size incorrect.");
        assertEquals("java", skills.get(0), "First skill incorrect.");
        
        List<String> empty = result.getTagsByCategory().get("empty");
        assertNotNull(empty, "Empty list not found.");
        assertEquals(0, empty.size(), "Empty list should have size 0.");
    }

    public static void main(String[] args) {
        DeserializerTest test = new DeserializerTest();
        test.run();
    }
}
