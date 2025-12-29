package com.pjr22.serialization.test.data;

import java.util.UUID;

/**
 * Test data class with UUID field.
 * Used to test serialization/deserialization of JDK types like UUID.
 */
public class PersonWithUUID {
    private String name;
    private UUID id;
    private UUID secondaryId;

    public PersonWithUUID() {
    }

    public PersonWithUUID(String name, UUID id, UUID secondaryId) {
        this.name = name;
        this.id = id;
        this.secondaryId = secondaryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getSecondaryId() {
        return secondaryId;
    }

    public void setSecondaryId(UUID secondaryId) {
        this.secondaryId = secondaryId;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PersonWithUUID that = (PersonWithUUID) obj;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (id != null ? !id.equals(that.id) : that.id != null) return false;
        return secondaryId != null ? secondaryId.equals(that.secondaryId) : that.secondaryId == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (id != null ? id.hashCode() : 0);
        result = 31 * result + (secondaryId != null ? secondaryId.hashCode() : 0);
        return result;
    }
}
