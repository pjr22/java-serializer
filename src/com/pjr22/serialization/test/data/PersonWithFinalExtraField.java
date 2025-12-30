package com.pjr22.serialization.test.data;

/**
 * Test class with a final field that is NOT in the constructor.
 * This should fail to deserialize correctly because final fields
 * cannot be set via reflection after construction.
 */
public class PersonWithFinalExtraField {
    private final String name;
    private final int age;
    private final String extraField; // This final field is NOT in the constructor

    public PersonWithFinalExtraField(String name, int age) {
        this.name = name;
        this.age = age;
        this.extraField = "default"; // Default value
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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonWithFinalExtraField that = (PersonWithFinalExtraField) o;

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
        return "PersonWithFinalExtraField{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", extraField='" + extraField + '\'' +
                '}';
    }
}
