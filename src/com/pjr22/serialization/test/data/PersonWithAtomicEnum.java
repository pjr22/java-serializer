package com.pjr22.serialization.test.data;

import java.util.concurrent.atomic.AtomicReference;

/**
 * Test class with AtomicReference to an enum type.
 * This is used to verify that parameterized type deserialization
 * works correctly for enum types.
 */
public class PersonWithAtomicEnum {
    private String name;
    private AtomicReference<CombatStance> combatStance;
    private AtomicReference<Status> status;

    public PersonWithAtomicEnum() {
        this.combatStance = new AtomicReference<>(CombatStance.BALANCED);
        this.status = new AtomicReference<>(Status.INACTIVE);
    }

    public PersonWithAtomicEnum(String name, CombatStance combatStance, Status status) {
        this.name = name;
        this.combatStance = new AtomicReference<>(combatStance);
        this.status = new AtomicReference<>(status);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AtomicReference<CombatStance> getCombatStance() {
        return combatStance;
    }

    public void setCombatStance(AtomicReference<CombatStance> combatStance) {
        this.combatStance = combatStance;
    }

    public AtomicReference<Status> getStatus() {
        return status;
    }

    public void setStatus(AtomicReference<Status> status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonWithAtomicEnum that = (PersonWithAtomicEnum) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (combatStance != null ? !combatStance.equals(that.combatStance) : that.combatStance != null) return false;
        return status != null ? status.equals(that.status) : that.status == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (combatStance != null ? combatStance.hashCode() : 0);
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PersonWithAtomicEnum{" +
                "name='" + name + '\'' +
                ", combatStance=" + combatStance +
                ", status=" + status +
                '}';
    }
}
