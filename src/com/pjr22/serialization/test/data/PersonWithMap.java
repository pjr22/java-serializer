package com.pjr22.serialization.test.data;

import java.util.HashMap;
import java.util.Map;

/**
 * Test class with a Map field.
 */
public class PersonWithMap {
    private String name;
    private Map<String, String> properties;

    public PersonWithMap() {
        this.properties = new HashMap<>();
    }

    public PersonWithMap(String name, Map<String, String> properties) {
        this.name = name;
        this.properties = properties;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, String> getProperties() {
        return properties;
    }

    public void setProperties(Map<String, String> properties) {
        this.properties = properties;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonWithMap that = (PersonWithMap) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return properties != null ? properties.equals(that.properties) : that.properties == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (properties != null ? properties.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PersonWithMap{" +
                "name='" + name + '\'' +
                ", properties=" + properties +
                '}';
    }
}
