package com.pjr22.serialization.test.data;

/**
 * Test class with static and transient fields.
 */
public class PersonWithStaticTransient {
    private String name;
    private int age;
    private static String staticField = "STATIC_VALUE";
    private transient String transientField = "TRANSIENT_VALUE";

    public PersonWithStaticTransient() {
    }

    public PersonWithStaticTransient(String name, int age, String transientField) {
        this.name = name;
        this.age = age;
        this.transientField = transientField;
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

    public static String getStaticField() {
        return staticField;
    }

    public static void setStaticField(String staticField) {
        PersonWithStaticTransient.staticField = staticField;
    }

    public String getTransientField() {
        return transientField;
    }

    public void setTransientField(String transientField) {
        this.transientField = transientField;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonWithStaticTransient that = (PersonWithStaticTransient) o;

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
        return "PersonWithStaticTransient{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", staticField='" + staticField + '\'' +
                ", transientField='" + transientField + '\'' +
                '}';
    }
}
