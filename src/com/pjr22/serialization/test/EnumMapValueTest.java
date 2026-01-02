package com.pjr22.serialization.test;

import com.pjr22.serialization.core.Deserializer;
import com.pjr22.serialization.core.Serializer;
import com.pjr22.serialization.core.SerializationException;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Test for enum values in Map deserialization.
 * This test verifies the fix for the issue where enum map values
 * were being deserialized as String instead of the enum type.
 */
public class EnumMapValueTest extends TestCase {

    // Test enum
    public enum WeaponType {
        BLUNT, PIERCING, SLASHING, MAGIC
    }

    // Test class with a Map containing enum values
    public static class Race {
        private String name;
        private Map<String, WeaponType> raceResistanceToWeapon;

        public Race() {
            this.raceResistanceToWeapon = new HashMap<>();
        }

        public Race(String name, Map<String, WeaponType> raceResistanceToWeapon) {
            this.name = name;
            this.raceResistanceToWeapon = raceResistanceToWeapon;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public Map<String, WeaponType> getRaceResistanceToWeapon() {
            return raceResistanceToWeapon;
        }

        public void setRaceResistanceToWeapon(Map<String, WeaponType> raceResistanceToWeapon) {
            this.raceResistanceToWeapon = raceResistanceToWeapon;
        }
    }

    public void testEnumMapValueSerializationAndDeserialization() throws SerializationException, IOException {
        // Create a test object with enum values in a map
        Map<String, WeaponType> resistanceMap = new HashMap<>();
        resistanceMap.put("Ent", WeaponType.BLUNT);
        resistanceMap.put("Skeleton", WeaponType.SLASHING);
        resistanceMap.put("Ghost", WeaponType.MAGIC);

        Race race = new Race("TestRace", resistanceMap);

        // Serialize
        Serializer serializer = new Serializer("REV-A", 1001);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(race, outputStream);

        String json = outputStream.toString();

        // Verify the JSON contains enum values as strings
        assertTrue(json.contains("BLUNT"), "JSON should contain BLUNT");
        assertTrue(json.contains("SLASHING"), "JSON should contain SLASHING");
        assertTrue(json.contains("MAGIC"), "JSON should contain MAGIC");

        // Deserialize
        Deserializer<Race> deserializer = new Deserializer<>(Race.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        Race deserialized = deserializer.deserialize(inputStream);

        // Verify the deserialized object
        assertNotNull(deserialized, "Deserialized race should not be null");
        assertEquals("TestRace", deserialized.getName(), "Race name should match");

        // Verify the map was correctly deserialized with enum values
        Map<String, WeaponType> deserializedMap = deserialized.getRaceResistanceToWeapon();
        assertNotNull(deserializedMap, "Deserialized map should not be null");
        assertEquals(3, deserializedMap.size(), "Map should have 3 entries");

        // Verify each entry has the correct enum type (not String)
        WeaponType entResistance = deserializedMap.get("Ent");
        assertNotNull(entResistance, "Ent resistance should not be null");
        assertEquals(WeaponType.BLUNT, entResistance, "Ent resistance should be BLUNT");
        assertTrue(entResistance instanceof WeaponType, "Ent resistance should be WeaponType enum");

        WeaponType skeletonResistance = deserializedMap.get("Skeleton");
        assertNotNull(skeletonResistance, "Skeleton resistance should not be null");
        assertEquals(WeaponType.SLASHING, skeletonResistance, "Skeleton resistance should be SLASHING");
        assertTrue(skeletonResistance instanceof WeaponType, "Skeleton resistance should be WeaponType enum");

        WeaponType ghostResistance = deserializedMap.get("Ghost");
        assertNotNull(ghostResistance, "Ghost resistance should not be null");
        assertEquals(WeaponType.MAGIC, ghostResistance, "Ghost resistance should be MAGIC");
        assertTrue(ghostResistance instanceof WeaponType, "Ghost resistance should be WeaponType enum");
    }

    public void testEnumMapValueWithConstructor() throws SerializationException, IOException {
        // Test with a class that uses constructor with map parameter
        Map<String, WeaponType> resistanceMap = new HashMap<>();
        resistanceMap.put("Dragon", WeaponType.PIERCING);

        Race race = new Race("DragonRace", resistanceMap);

        // Serialize
        Serializer serializer = new Serializer("REV-A", 1001);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        serializer.serialize(race, outputStream);

        String json = outputStream.toString();

        // Deserialize
        Deserializer<Race> deserializer = new Deserializer<>(Race.class);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
        Race deserialized = deserializer.deserialize(inputStream);

        // Verify
        assertNotNull(deserialized, "Deserialized race should not be null");
        assertEquals("DragonRace", deserialized.getName(), "Race name should match");

        Map<String, WeaponType> deserializedMap = deserialized.getRaceResistanceToWeapon();
        assertNotNull(deserializedMap, "Deserialized map should not be null");
        assertEquals(1, deserializedMap.size(), "Map should have 1 entry");

        WeaponType dragonResistance = deserializedMap.get("Dragon");
        assertNotNull(dragonResistance, "Dragon resistance should not be null");
        assertEquals(WeaponType.PIERCING, dragonResistance, "Dragon resistance should be PIERCING");
        assertTrue(dragonResistance instanceof WeaponType, "Dragon resistance should be WeaponType enum");
    }

    public static void main(String[] args) {
        EnumMapValueTest test = new EnumMapValueTest();
        test.run();
    }
}
