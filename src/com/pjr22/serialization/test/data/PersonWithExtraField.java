package com.pjr22.serialization.test.data;

/**
 * Test class to verify that fields not covered by constructor parameters
 * are still set during deserialization.
 */
public class PersonWithExtraField {
    private final String name;
    private final int age;
    private String extraField; // This field is NOT in the constructor

    public PersonWithExtraField(String name, int age) {
        this.name = name;
        this.age = age;
        this.extraField = null; // Initialize to null
    }

    public String getName() {
        return name;
    }

    public int getAge() {
        return age;
    }

    public String getExtraField() {
        return extraField;
    }

    public void setExtraField(String extraField) {
        this.extraField = extraField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonWithExtraField that = (PersonWithExtraField) o;

        if (age != that.age) return false;
        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return extraField != null ? extraField.equals(that.extraField) : that.extraField == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + age;
        result = 31 * result + (extraField != null ? extraField.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PersonWithExtraField{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", extraField='" + extraField + '\'' +
                '}';
    }
}
