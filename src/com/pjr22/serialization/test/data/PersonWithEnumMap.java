package com.pjr22.serialization.test.data;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

/**
 * Test class with a {@code Map<Enum, Object>} field to reproduce the double-quote issue.
 */
public class PersonWithEnumMap {
    private String name;
    protected final Map<PrimaryAttribute, AttributeState> attributes = Collections.synchronizedMap(new TreeMap<>());

    public PersonWithEnumMap() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<PrimaryAttribute, AttributeState> getAttributes() {
        return attributes;
    }

    /**
     * Primary attribute enum.
     */
    public enum PrimaryAttribute {
        Charisma("CHR"),
        Constitution("CON"),
        Dexterity("DEX"),
        Faith("FTH"),
        Intelligence("INT"),
        Luck("LCK"),
        Strength("STR");

        private final String abbreviation;

        private PrimaryAttribute(String abbreviation) {
            this.abbreviation = abbreviation;
        }

        public String getAbbreviation() {
            return abbreviation;
        }
    }

    /**
     * Attribute state class.
     */
    public static class AttributeState {
        private int baseValue;
        private int racialBonus;
        private Map<String, Integer> activeEffects;
        private int advancementPoints;

        public AttributeState() {
            this.activeEffects = new java.util.HashMap<>();
        }

        public AttributeState(int baseValue, int racialBonus, int advancementPoints) {
            this.baseValue = baseValue;
            this.racialBonus = racialBonus;
            this.activeEffects = new java.util.HashMap<>();
            this.advancementPoints = advancementPoints;
        }

        public int getBaseValue() {
            return baseValue;
        }

        public void setBaseValue(int baseValue) {
            this.baseValue = baseValue;
        }

        public int getRacialBonus() {
            return racialBonus;
        }

        public void setRacialBonus(int racialBonus) {
            this.racialBonus = racialBonus;
        }

        public Map<String, Integer> getActiveEffects() {
            return activeEffects;
        }

        public void setActiveEffects(Map<String, Integer> activeEffects) {
            this.activeEffects = activeEffects;
        }

        public int getAdvancementPoints() {
            return advancementPoints;
        }

        public void setAdvancementPoints(int advancementPoints) {
            this.advancementPoints = advancementPoints;
        }
    }
}
