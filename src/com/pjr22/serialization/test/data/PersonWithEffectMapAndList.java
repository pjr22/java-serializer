package com.pjr22.serialization.test.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class with both a {@code Map<ComplexObject, Integer>} field and a {@code List<ComplexObject>} field.
 * This reproduces the issue where $mapKeys appears in nested objects that don't
 * have maps with complex keys.
 */
public class PersonWithEffectMapAndList {
    private String name;
    private final Map<Effect, Integer> activeEffects = new HashMap<>();
    private final List<Effect> effectHistory = new ArrayList<>();

    public PersonWithEffectMapAndList() {
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

    public List<Effect> getEffectHistory() {
        return effectHistory;
    }
}
