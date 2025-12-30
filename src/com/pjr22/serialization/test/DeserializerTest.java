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

    public void testDeserializeObjectWithProtectedConstructor() throws SerializationException, IOException {
        String json = """
            {
              "objects": [
                {
                  "id": "REV-A_1001_com.pjr22.serialization.test.data.ItemWithProtectedConstructor",
                  "className": "com.pjr22.serialization.test.data.ItemWithProtectedConstructor",
                  "fields": {
                    "name": "Steel Plate",
                    "weight": 15.5,
                    "value": 500,
                    "totalDamageCapacity": 1000,
                    "totalDamageAbsorbed": 0
                  },
                  "references": {}
                }
              ]
            }
            """;

        Deserializer<ItemWithProtectedConstructor> deserializer = new Deserializer<>(ItemWithProtectedConstructor.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        ItemWithProtectedConstructor item = deserializer.deserialize(inputStream);

        assertNotNull(item, "Deserialized object should not be null");
        assertEquals("Steel Plate", item.getName(), "Name should match");
        assertEquals(15.5, item.getWeight().doubleValue(), 0.001, "Weight should match");
        assertEquals(500, item.getValue(), "Value should match");
        assertEquals(1000, item.getTotalDamageCapacity(), "Total damage capacity should match");
        assertEquals(0, item.getTotalDamageAbsorbed(), "Total damage absorbed should match");
    }

    public void testDeserializeConfigLikeWithMapConstructorParameters() throws SerializationException, IOException {
        // Test case for the reported issue where a class has a constructor
        // that creates new Map instances from parameters (which would fail with null)
        String json = """
            {
              "objects": [
                {
                  "id": "REV-A_1001_com.pjr22.serialization.test.data.ConfigLike",
                  "className": "com.pjr22.serialization.test.data.ConfigLike",
                  "serialVersionUID": 1,
                  "fields": {
                    "client": {
                      "client.keystore": "security/client.jks",
                      "client.truststore": "security/trusted_servers.jks"
                    },
                    "combat": {
                      "combat.ticks_per_round": 120
                    },
                    "features": {
                      "feature.ai_room_descriptions": true,
                      "feature.demo_mode_enabled": false,
                      "feature.log_window": false,
                      "feature.sound": true
                    },
                    "game": {
                      "game.character_points": 10,
                      "game.clock_frequency": 30,
                      "game.log_window_height": 400,
                      "game.log_window_scale": 1,
                      "game.log_window_width": 1024,
                      "game.max_arcane_material_placements": 3,
                      "game.ogre_teeth_pct_chance": 10,
                      "game.random_seed": 1268161756,
                      "game.ticks_to_heal": 1200,
                      "game.ticks_to_recharge": 1200,
                      "game.ticks_to_recover": 120
                    },
                    "map": {
                      "oubliette.exit_max_distance": 20,
                      "oubliette.exit_min_distance": 4,
                      "oubliette.random_seed": 1377016630,
                      "oubliette.rooms_height_max": 20,
                      "oubliette.rooms_height_min": 8,
                      "oubliette.rooms_max": 40,
                      "oubliette.rooms_min": 10,
                      "oubliette.rooms_percent_chance_of_container": 25,
                      "oubliette.rooms_percent_chance_of_enemy": 50,
                      "oubliette.rooms_width_max": 20,
                      "oubliette.rooms_width_min": 8
                    },
                    "server": {
                      "server.hostname": "localhost",
                      "server.port_ssl": "8822",
                      "server.skip_verification": "true"
                    },
                    "sounds": {
                      "sounds.primary": ["depths_extended.mid", "oubliette.mid"],
                      "sounds.combat": ["spellsword.mid", "combat_216.mid"]
                    }
                  },
                  "references": {}
                }
              ]
            }
            """;

        Deserializer<ConfigLike> deserializer = new Deserializer<>(ConfigLike.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        ConfigLike config = deserializer.deserialize(inputStream);

        assertNotNull(config, "Deserialized ConfigLike should not be null");
        
        // Verify client map
        assertNotNull(config.getClient(), "Client map should not be null");
        assertEquals(2, config.getClient().size(), "Client map should have 2 entries");
        assertEquals("security/client.jks", config.getClient().get("client.keystore"), "Client keystore should match");
        
        // Verify combat map
        assertNotNull(config.getCombat(), "Combat map should not be null");
        assertEquals(1, config.getCombat().size(), "Combat map should have 1 entry");
        assertEquals(120, config.getCombat().get("combat.ticks_per_round"), "Combat ticks should match");
        
        // Verify features map
        assertNotNull(config.getFeatures(), "Features map should not be null");
        assertEquals(4, config.getFeatures().size(), "Features map should have 4 entries");
        assertTrue(config.getFeatures().get("feature.sound"), "Feature sound should be true");
        
        // Verify game map
        assertNotNull(config.getGame(), "Game map should not be null");
        assertEquals(11, config.getGame().size(), "Game map should have 11 entries");
        assertEquals(10, config.getGame().get("game.character_points"), "Character points should match");
        
        // Verify map (the one that was causing the error)
        assertNotNull(config.getMap(), "Map should not be null");
        assertEquals(11, config.getMap().size(), "Map should have 11 entries");
        assertEquals(20, config.getMap().get("oubliette.exit_max_distance"), "Exit max distance should match");
        assertEquals(4, config.getMap().get("oubliette.exit_min_distance"), "Exit min distance should match");
        
        // Verify server map
        assertNotNull(config.getServer(), "Server map should not be null");
        assertEquals(3, config.getServer().size(), "Server map should have 3 entries");
        assertEquals("localhost", config.getServer().get("server.hostname"), "Server hostname should match");
        
        // Verify sounds map
        assertNotNull(config.getSounds(), "Sounds map should not be null");
        assertEquals(2, config.getSounds().size(), "Sounds map should have 2 entries");
        assertEquals(2, config.getSounds().get("sounds.primary").size(), "Primary sounds should have 2 entries");
    }

    public void testDeserializeConfigLikeRoundTrip() throws SerializationException, IOException {
        // Test full round-trip serialization and deserialization
        Serializer serializer = new Serializer("REV-A", 1001);
        
        // Create a ConfigLike instance with data
        Map<String, String> client = new java.util.TreeMap<>();
        client.put("client.keystore", "security/client.jks");
        
        Map<String, Integer> combat = new java.util.TreeMap<>();
        combat.put("combat.ticks_per_round", 120);
        
        Map<String, Boolean> features = new java.util.TreeMap<>();
        features.put("feature.sound", true);
        
        Map<String, Integer> game = new java.util.TreeMap<>();
        game.put("game.character_points", 10);
        
        Map<String, Integer> map = new java.util.TreeMap<>();
        map.put("oubliette.exit_max_distance", 20);
        
        Map<String, String> server = new java.util.TreeMap<>();
        server.put("server.hostname", "localhost");
        
        Map<String, List<String>> sounds = new java.util.TreeMap<>();
        sounds.put("sounds.primary", List.of("depths_extended.mid", "oubliette.mid"));
        
        ConfigLike original = new ConfigLike(client, combat, features, game, map, server, sounds);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(original, outputStream);

        String json = outputStream.toString();

        Deserializer<ConfigLike> deserializer = new Deserializer<>(ConfigLike.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        ConfigLike result = deserializer.deserialize(inputStream);

        assertNotNull(result, "Deserialization returned null.");
        assertEquals(original, result, "Round-trip deserialization should produce equal objects.");
        assertEquals(1, result.getClient().size(), "Client map size incorrect.");
        assertEquals(1, result.getCombat().size(), "Combat map size incorrect.");
        assertEquals(1, result.getFeatures().size(), "Features map size incorrect.");
        assertEquals(1, result.getGame().size(), "Game map size incorrect.");
        assertEquals(1, result.getMap().size(), "Map size incorrect.");
        assertEquals(1, result.getServer().size(), "Server map size incorrect.");
        assertEquals(1, result.getSounds().size(), "Sounds map size incorrect.");
    }

    public void testDeserializeMapWithNestedObjectValues() throws SerializationException, IOException {
        // Test for issue where Map values containing nested objects were incorrectly
        // serialized as strings instead of proper nested objects
        Serializer serializer = new Serializer("REV-A", 1001);
        
        // Create a map with nested object values
        Map<String, SimplePerson> peopleByRole = new java.util.LinkedHashMap<>();
        peopleByRole.put("developer", new SimplePerson("Alice", 28, 85000.0, true));
        peopleByRole.put("manager", new SimplePerson("Bob", 35, 120000.0, true));
        peopleByRole.put("intern", new SimplePerson("Charlie", 22, 45000.0, false));
        
        PersonWithMapOfPeople container = new PersonWithMapOfPeople("Team", peopleByRole);
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(container, outputStream);
        
        String json = outputStream.toString();
        
        // Verify that the JSON does NOT contain "$value" for map entries
        // (which would indicate incorrect serialization as simple value)
        assertFalse(json.contains("\"$value\""), "Map values should not be serialized as simple values");
        
        Deserializer<PersonWithMapOfPeople> deserializer = new Deserializer<>(PersonWithMapOfPeople.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        PersonWithMapOfPeople result = deserializer.deserialize(inputStream);
        
        assertNotNull(result, "Deserialization returned null.");
        assertEquals("Team", result.getName(), "Name not deserialized correctly.");
        assertEquals(3, result.getPeopleByRole().size(), "Map size incorrect.");
        
        SimplePerson developer = result.getPeopleByRole().get("developer");
        assertNotNull(developer, "Developer not found.");
        assertEquals("Alice", developer.getName(), "Developer name incorrect.");
        assertEquals(28, developer.getAge(), "Developer age incorrect.");
        assertEquals(85000.0, developer.getSalary(), 0.001, "Developer salary incorrect.");
        assertTrue(developer.isActive(), "Developer active incorrect.");
        
        SimplePerson manager = result.getPeopleByRole().get("manager");
        assertNotNull(manager, "Manager not found.");
        assertEquals("Bob", manager.getName(), "Manager name incorrect.");
        
        SimplePerson intern = result.getPeopleByRole().get("intern");
        assertNotNull(intern, "Intern not found.");
        assertEquals("Charlie", intern.getName(), "Intern name incorrect.");
    }

    public static void main(String[] args) {
        DeserializerTest test = new DeserializerTest();
        test.run();
    }
}
