package com.pjr22.serialization.test.data;

/**
 * Test class that extends BasePerson for testing inheritance.
 */
public class PersonWithInheritance extends BasePerson {
    private int age;
    private String email;

    public PersonWithInheritance() {
    }

    public PersonWithInheritance(String firstName, String lastName, int age, String email) {
        super(firstName, lastName);
        this.age = age;
        this.email = email;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;

        PersonWithInheritance that = (PersonWithInheritance) o;

        if (age != that.age) return false;
        return email != null ? email.equals(that.email) : that.email == null;
    }

    @Override
    public int hashCode() {
        int result = super.hashCode();
        result = 31 * result + age;
        result = 31 * result + (email != null ? email.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PersonWithInheritance{" +
                "age=" + age +
                ", email='" + email + '\'' +
                "} " + super.toString();
    }
}
