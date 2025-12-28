package com.pjr22.serialization.test.data;

import java.math.BigDecimal;

/**
 * Test class representing a department with a reference to a manager.
 */
public class Department {
    private String name;
    private BigDecimal budget;
    private SimplePerson manager;

    public Department() {
    }

    public Department(String name, BigDecimal budget, SimplePerson manager) {
        this.name = name;
        this.budget = budget;
        this.manager = manager;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getBudget() {
        return budget;
    }

    public void setBudget(BigDecimal budget) {
        this.budget = budget;
    }

    public SimplePerson getManager() {
        return manager;
    }

    public void setManager(SimplePerson manager) {
        this.manager = manager;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Department that = (Department) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (budget != null ? budget.compareTo(that.budget) != 0 : that.budget != null) return false;
        return manager != null ? manager.equals(that.manager) : that.manager == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (budget != null ? budget.hashCode() : 0);
        result = 31 * result + (manager != null ? manager.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "Department{" +
                "name='" + name + '\'' +
                ", budget=" + budget +
                ", manager=" + manager +
                '}';
    }
}
