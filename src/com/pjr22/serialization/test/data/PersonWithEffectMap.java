package com.pjr22.serialization.test.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class with a {@code Map<ComplexObject, Integer>} field to test complex object map keys.
 */
public class PersonWithEffectMap {
    private String name;
    private final Map<Effect, Integer> activeEffects = new HashMap<>();

    public PersonWithEffectMap() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<Effect, Integer> getActiveEffects() {
        return activeEffects;
    }
}
