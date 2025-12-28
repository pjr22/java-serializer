package com.pjr22.serialization.test.data;

import java.util.Arrays;

/**
 * Test class with array fields.
 */
public class PersonWithArray {
    private String name;
    private int[] values;

    public PersonWithArray() {
    }

    public PersonWithArray(String name, int[] values) {
        this.name = name;
        this.values = values;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int[] getValues() {
        return values;
    }

    public void setValues(int[] values) {
        this.values = values;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonWithArray that = (PersonWithArray) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return Arrays.equals(values, that.values);
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + Arrays.hashCode(values);
        return result;
    }

    @Override
    public String toString() {
        return "PersonWithArray{" +
                "name='" + name + '\'' +
                ", values=" + Arrays.toString(values) +
                '}';
    }
}
