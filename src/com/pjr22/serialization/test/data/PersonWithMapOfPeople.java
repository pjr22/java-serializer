package com.pjr22.serialization.test.data;

import java.util.Map;

/**
 * Test class for Map with nested object values.
 * Used to test that Map values containing objects are properly serialized/deserialized.
 */
public class PersonWithMapOfPeople {
    private String name;
    private Map<String, SimplePerson> peopleByRole;

    public PersonWithMapOfPeople() {
    }

    public PersonWithMapOfPeople(String name, Map<String, SimplePerson> peopleByRole) {
        this.name = name;
        this.peopleByRole = peopleByRole;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Map<String, SimplePerson> getPeopleByRole() {
        return peopleByRole;
    }

    public void setPeopleByRole(Map<String, SimplePerson> peopleByRole) {
        this.peopleByRole = peopleByRole;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        PersonWithMapOfPeople other = (PersonWithMapOfPeople) obj;
        return name != null ? name.equals(other.name) : other.name == null;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }
}
