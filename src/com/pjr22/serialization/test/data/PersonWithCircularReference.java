package com.pjr22.serialization.test.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class that can reference itself (circular reference).
 */
public class PersonWithCircularReference {
    private String name;
    private PersonWithCircularReference parent;
    private List<PersonWithCircularReference> children;

    public PersonWithCircularReference() {
        this.children = new ArrayList<>();
    }

    public PersonWithCircularReference(String name) {
        this.name = name;
        this.children = new ArrayList<>();
    }

    public PersonWithCircularReference(String name, PersonWithCircularReference parent) {
        this.name = name;
        this.parent = parent;
        this.children = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public PersonWithCircularReference getParent() {
        return parent;
    }

    public void setParent(PersonWithCircularReference parent) {
        this.parent = parent;
    }

    public List<PersonWithCircularReference> getChildren() {
        return children;
    }

    public void setChildren(List<PersonWithCircularReference> children) {
        this.children = children;
    }

    public void addChild(PersonWithCircularReference child) {
        children.add(child);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonWithCircularReference that = (PersonWithCircularReference) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        // Note: We don't compare parent or children to avoid infinite recursion
        return true;
    }

    @Override
    public int hashCode() {
        return name != null ? name.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "PersonWithCircularReference{" +
                "name='" + name + '\'' +
                ", parent=" + (parent != null ? parent.getName() : "null") +
                ", children=" + children.size() +
                '}';
    }
}
