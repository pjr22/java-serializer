package com.pjr22.serialization.test.data;

import java.math.BigDecimal;

/**
 * Test class with a protected constructor, similar to the BodyArmor class
 * that triggered the original issue.
 */
public class ItemWithProtectedConstructor {
    private final String name;
    private final BigDecimal weight;
    private final int value;
    private final int totalDamageCapacity;
    private final int totalDamageAbsorbed;

    protected ItemWithProtectedConstructor(String name,
                                          BigDecimal weight,
                                          int value,
                                          int totalDamageCapacity,
                                          int totalDamageAbsorbed) {
        this.name = name;
        this.weight = weight;
        this.value = value;
        this.totalDamageCapacity = totalDamageCapacity;
        this.totalDamageAbsorbed = totalDamageAbsorbed;
    }

    public static ItemWithProtectedConstructor build(String name,
                                                     BigDecimal weight,
                                                     int value,
                                                     int totalDamageCapacity,
                                                     int totalDamageAbsorbed) {
        return new ItemWithProtectedConstructor(name, weight, value, totalDamageCapacity, totalDamageAbsorbed);
    }

    public String getName() {
        return name;
    }

    public BigDecimal getWeight() {
        return weight;
    }

    public int getValue() {
        return value;
    }

    public int getTotalDamageCapacity() {
        return totalDamageCapacity;
    }

    public int getTotalDamageAbsorbed() {
        return totalDamageAbsorbed;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        ItemWithProtectedConstructor other = (ItemWithProtectedConstructor) obj;
        return value == other.value &&
               totalDamageCapacity == other.totalDamageCapacity &&
               totalDamageAbsorbed == other.totalDamageAbsorbed &&
               name.equals(other.name) &&
               weight.equals(other.weight);
    }

    @Override
    public int hashCode() {
        int result = name.hashCode();
        result = 31 * result + weight.hashCode();
        result = 31 * result + value;
        result = 31 * result + totalDamageCapacity;
        result = 31 * result + totalDamageAbsorbed;
        return result;
    }
}
