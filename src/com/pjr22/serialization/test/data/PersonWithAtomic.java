package com.pjr22.serialization.test.data;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

/**
 * Test class with atomic types.
 */
public class PersonWithAtomic {
    private String name;
    private AtomicInteger counter;
    private AtomicBoolean flag;
    private AtomicReference<Address> address;

    public PersonWithAtomic() {
        this.counter = new AtomicInteger(0);
        this.flag = new AtomicBoolean(false);
        this.address = new AtomicReference<>();
    }

    public PersonWithAtomic(String name, int counter, boolean flag) {
        this.name = name;
        this.counter = new AtomicInteger(counter);
        this.flag = new AtomicBoolean(flag);
        this.address = new AtomicReference<>();
    }

    public PersonWithAtomic(String name, int counter, boolean flag, Address address) {
        this.name = name;
        this.counter = new AtomicInteger(counter);
        this.flag = new AtomicBoolean(flag);
        this.address = new AtomicReference<>(address);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AtomicInteger getCounter() {
        return counter;
    }

    public void setCounter(AtomicInteger counter) {
        this.counter = counter;
    }

    public AtomicBoolean getFlag() {
        return flag;
    }

    public void setFlag(AtomicBoolean flag) {
        this.flag = flag;
    }

    public AtomicReference<Address> getAddress() {
        return address;
    }

    public void setAddress(AtomicReference<Address> address) {
        this.address = address;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        PersonWithAtomic that = (PersonWithAtomic) o;

        if (name != null ? !name.equals(that.name) : that.name != null) return false;
        if (counter != null ? !counter.equals(that.counter) : that.counter != null) return false;
        if (flag != null ? !flag.equals(that.flag) : that.flag != null) return false;
        return address != null ? address.equals(that.address) : that.address == null;
    }

    @Override
    public int hashCode() {
        int result = name != null ? name.hashCode() : 0;
        result = 31 * result + (counter != null ? counter.hashCode() : 0);
        result = 31 * result + (flag != null ? flag.hashCode() : 0);
        result = 31 * result + (address != null ? address.hashCode() : 0);
        return result;
    }

    @Override
    public String toString() {
        return "PersonWithAtomic{" +
                "name='" + name + '\'' +
                ", counter=" + counter +
                ", flag=" + flag +
                ", address=" + address +
                '}';
    }
}
