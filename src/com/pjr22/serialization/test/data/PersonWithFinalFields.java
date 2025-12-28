package com.pjr22.serialization.test.data;

/**
 * Test class with final fields.
 */
public class PersonWithFinalFields {
    private final String name;
    private final int age;
    private final boolean active;

    public PersonWithFinalFields() {
        this.name = null;
        this.age = 0;
        this.active = false;
    }

    public PersonWithFinalFields(String name, int age, boolean active) {
        this.name = name;
        this.age = age;
        this.active = active;
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public boolean isActive() {
        return active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonWithFinalFields that = (PersonWithFinalFields) o;

        if (age != that.age) return false;
        if (active != that.active) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + age;
        result = 31 * result + (active ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PersonWithFinalFields{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", active=" + active +
                '}';
    }
}
