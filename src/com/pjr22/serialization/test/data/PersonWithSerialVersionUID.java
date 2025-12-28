package com.pjr22.serialization.test.data;

/**
 * Test class with serialVersionUID for testing versioning.
 */
public class PersonWithSerialVersionUID {
    private static final long serialVersionUID = 123456789L;

    private String name;
    private int age;

    public PersonWithSerialVersionUID() {
    }

    public PersonWithSerialVersionUID(String name, int age) {
        this.name = name;
        this.age = age;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonWithSerialVersionUID that = (PersonWithSerialVersionUID) o;

        if (age != that.age) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + age;
        return result;
    }

    @Override
    public String toString() {
        return "PersonWithSerialVersionUID{" +
                "name='" + name + '\'' +
                ", age=" + age +
                '}';
    }
}
