package com.pjr22.serialization.test.data;

/**
 * Test class with enum field.
 */
public class PersonWithEnum {
    private String name;
    private Status status;

    public PersonWithEnum() {
    }

    public PersonWithEnum(String name, Status status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonWithEnum that = (PersonWithEnum) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return status == that.status;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (status != null ? status.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PersonWithEnum{" +
                "name='" + name + '\'' +
                ", status=" + status +
                '}';
    }
}
