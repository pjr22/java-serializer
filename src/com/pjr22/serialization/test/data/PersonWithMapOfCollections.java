package com.pjr22.serialization.test.data;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Test class with a Map<String, List<String>> field.
 */
public class PersonWithMapOfCollections {
    private String name;
    private Map<String, List<String>> tagsByCategory;

    public PersonWithMapOfCollections() {
        this.tagsByCategory = new HashMap<>();
    }

    public PersonWithMapOfCollections(String name, Map<String, List<String>> tagsByCategory) {
        this.name = name;
        this.tagsByCategory = tagsByCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, List<String>> getTagsByCategory() {
        return tagsByCategory;
    }

    public void setTagsByCategory(Map<String, List<String>> tagsByCategory) {
        this.tagsByCategory = tagsByCategory;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonWithMapOfCollections that = (PersonWithMapOfCollections) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        return tagsByCategory != null ? tagsByCategory.equals(that.tagsByCategory) : that.tagsByCategory == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (tagsByCategory != null ? tagsByCategory.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PersonWithMapOfCollections{" +
                "name='" + name + '\'' +
                ", tagsByCategory=" + tagsByCategory +
                '}';
    }
}
