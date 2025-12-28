package com.pjr22.serialization.test.data;

import java.util.ArrayList;
import java.util.List;

/**
 * Test class with collections.
 */
public class PersonWithCollections {
    private String name;
    private List<String> tags;
    private List<Integer> scores;

    public PersonWithCollections() {
        this.tags = new ArrayList<>();
        this.scores = new ArrayList<>();
    }

    public PersonWithCollections(String name, List<String> tags, List<Integer> scores) {
        this.name = name;
        this.tags = tags;
        this.scores = scores;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }

    public List<Integer> getScores() {
        return scores;
    }

    public void setScores(List<Integer> scores) {
        this.scores = scores;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonWithCollections that = (PersonWithCollections) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (tags != null ? !tags.equals(that.tags) : that.tags != null) return false;
        return scores != null ? scores.equals(that.scores) : that.scores == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (tags != null ? tags.hashCode() : 0);
        result = 31 * result + (scores != null ? scores.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PersonWithCollections{" +
                "name='" + name + '\'' +
                ", tags=" + tags +
                ", scores=" + scores +
                '}';
    }
}
