package com.pjr22.serialization.test.data;

/**
 * Simple test class with primitive and String fields.
 */
public class SimplePerson {
    private String name;
    private int age;
    private double salary;
    private boolean active;

    public SimplePerson() {
    }

    public SimplePerson(String name, int age, double salary, boolean active) {
        this.name = name;
        this.age = age;
        this.salary = salary;
        this.active = active;
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

    public double getSalary() {
        return salary;
    }

    public void setSalary(double salary) {
        this.salary = salary;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        SimplePerson that = (SimplePerson) o;

        if (age != that.age) return false;
        if (Double.compare(that.salary, salary) != 0) return false;
        if (active != that.active) return false;
        return name != null ? name.equals(that.name) : that.name == null;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        result = name != null ? name.hashCode() : 0;
        result = 31 * result + age;
        temp = Double.doubleToLongBits(salary);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        result = 31 * result + (active ? 1 : 0);
        return result;
    }

    @Override
    public String toString() {
        return "SimplePerson{" +
                "name='" + name + '\'' +
                ", age=" + age +
                ", salary=" + salary +
                ", active=" + active +
                '}';
    }
}
