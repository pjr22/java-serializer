package com.pjr22.serialization.test.data;

/**
 * Complex object class representing an effect in a game.
 * This class is used to test serialization of complex objects as map keys.
 */
public class Effect {
    private String description;
    private Attribute attribute;
    private double potency;
    private int durationSec;
    private long elapsed;
    private Type type;
    private double originalValue;
    private double modifier;

    public enum Attribute {
        Constitution,
        Strength,
        Dexterity,
        Intelligence,
        Wisdom,
        Charisma
    }

    public enum Type {
        FORTIFY_ATTRIBUTE,
        WEAKEN_ATTRIBUTE,
        TEMPORARY_BOOST,
        TEMPORARY_DEBUFF
    }

    public Effect() {
    }

    public Effect(String description, Attribute attribute, double potency, int durationSec,
                  long elapsed, Type type, double originalValue, double modifier) {
        this.description = description;
        this.attribute = attribute;
        this.potency = potency;
        this.durationSec = durationSec;
        this.elapsed = elapsed;
        this.type = type;
        this.originalValue = originalValue;
        this.modifier = modifier;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Attribute getAttribute() {
        return attribute;
    }

    public void setAttribute(Attribute attribute) {
        this.attribute = attribute;
    }

    public double getPotency() {
        return potency;
    }

    public void setPotency(double potency) {
        this.potency = potency;
    }

    public int getDurationSec() {
        return durationSec;
    }

    public void setDurationSec(int durationSec) {
        this.durationSec = durationSec;
    }

    public long getElapsed() {
        return elapsed;
    }

    public void setElapsed(long elapsed) {
        this.elapsed = elapsed;
    }

    public Type getType() {
        return type;
    }

    public void setType(Type type) {
        this.type = type;
    }

    public double getOriginalValue() {
        return originalValue;
    }

    public void setOriginalValue(double originalValue) {
        this.originalValue = originalValue;
    }

    public double getModifier() {
        return modifier;
    }

    public void setModifier(double modifier) {
        this.modifier = modifier;
    }

    @Override
    public String toString() {
        return "Effect [description=" + description + ", attribute=" + attribute +
               ", potency=" + potency + ", durationSec=" + durationSec +
               ", elapsed=" + elapsed + ", type=" + type +
               ", originalValue=" + originalValue + ", modifier=" + modifier + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Effect effect = (Effect) obj;
        return description != null ? description.equals(effect.description) : effect.description == null;
    }

    @Override
    public int hashCode() {
        return description != null ? description.hashCode() : 0;
    }
}
