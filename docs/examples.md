# Usage Examples

This document provides comprehensive usage examples for the Java Serializer/Deserializer library.

## Table of Contents

- [Basic Serialization and Deserialization](#basic-serialization-and-deserialization)
- [Working with Different Data Types](#working-with-different-data-types)
  - [Primitives and Strings](#primitives-and-strings)
  - [Numbers and BigDecimal](#numbers-and-bigdecimal)
  - [Atomic Types](#atomic-types)
  - [Enums](#enums)
  - [Working with Date](#working-with-date)
  - [Working with Random](#working-with-random)
  - [Working with UUID](#working-with-uuid)
  - [Working with Collections](#working-with-collections)
  - [Collection Interface Types](#collection-interface-types)
  - [Working with Maps](#working-with-maps)
  - [Working with Arrays](#working-with-arrays)
  - [Object References](#object-references)
  - [Circular References](#circular-references)
    - [Circular References with Constructor-Based Deserialization](#circular-references-with-constructor-based-deserialization)
  - [Inheritance](#inheritance)
  - [Immutable Objects](#immutable-objects)
  - [Non-Public Constructors](#non-public-constructors)
  - [Final Fields](#final-fields)
  - [Null Fields](#null-fields)
  - [Static and Transient Fields](#static-and-transient-fields)
  - [SerialVersionUID](#serialversionuid)
  - [Error Handling](#error-handling)
  - [Working with Files](#working-with-files)
  - [Advanced Usage](#advanced-usage)
  - [Using ObjectIdGenerator Directly](#using-objectidgenerator-directly)
  - [Using ObjectRegistry Directly](#using-objectregistry-directly)
  - [Using FieldInspector and FieldClassifier](#using-fieldinspector-and-fieldclassifier)
  - [Using ConstructorAnalyzer](#using-constructoranalyzer)
  - [Using JsonSerializer and JsonParser](#using-jsonserializer-and-jsonparser)

---

## Basic Serialization and Deserialization

### Simple Object Example

```java
import com.pjr22.serialization.core.Serializer;
import com.pjr22.serialization.core.Deserializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

// Create a simple person object
SimplePerson person = new SimplePerson();
person.setName("John Doe");
person.setAge(30);
person.setSalary(75000.50);
person.setActive(true);

// Serialize to JSON
Serializer serializer = new Serializer("myapp", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);
String json = outputStream.toString();

System.out.println("Serialized JSON:");
System.out.println(json);

// Deserialize from JSON
Deserializer<SimplePerson> deserializer = new Deserializer<>(SimplePerson.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
SimplePerson deserialized = deserializer.deserialize(inputStream);

System.out.println("\nDeserialized object:");
System.out.println("Name: " + deserialized.getName());
System.out.println("Age: " + deserialized.getAge());
System.out.println("Salary: " + deserialized.getSalary());
System.out.println("Active: " + deserialized.isActive());
```

**Output:**
```
Serialized JSON:
{"$id":"myapp_1_com.example.SimplePerson","$class":"com.example.SimplePerson","fields":{"name":"John Doe","age":30,"salary":75000.5,"active":true}}

Deserialized object:
Name: John Doe
Age: 30
Salary: 75000.5
Active: true
```

---

## Working with Different Data Types

### Primitives and Strings

```java
// Create an object with primitive fields
SimplePerson person = new SimplePerson();
person.setName("Alice Smith");  // String
person.setAge(28);              // int
person.setActive(true);          // boolean

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<SimplePerson> deserializer = new Deserializer<>(SimplePerson.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
SimplePerson result = deserializer.deserialize(inputStream);

// Verify values
assert result.getName().equals("Alice Smith");
assert result.getAge() == 28;
assert result.isActive() == true;
```

### Numbers and BigDecimal

```java
// Department class has a BigDecimal field
Department dept = new Department();
dept.setName("Engineering");
dept.setBudget(new BigDecimal("1500000.00"));

SimplePerson manager = new SimplePerson();
manager.setName("Bob Johnson");
manager.setAge(45);
manager.setSalary(120000.00);
dept.setManager(manager);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(dept, outputStream);

Deserializer<Department> deserializer = new Deserializer<>(Department.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
Department result = deserializer.deserialize(inputStream);

// Verify values
assert result.getName().equals("Engineering");
assert result.getBudget().compareTo(new BigDecimal("1500000.00")) == 0;
assert result.getManager().getName().equals("Bob Johnson");
```

### Atomic Types

```java
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

// Create an object with atomic fields
PersonWithAtomic person = new PersonWithAtomic();
person.setName("Charlie Brown");
person.setCounter(new AtomicInteger(100));
person.setFlag(new AtomicBoolean(true));

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithAtomic> deserializer = new Deserializer<>(PersonWithAtomic.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithAtomic result = deserializer.deserialize(inputStream);

// Verify values
assert result.getName().equals("Charlie Brown");
assert result.getCounter().get() == 100;
assert result.getFlag().get() == true;
```

### AtomicReference with Complex Type

```java
import java.util.concurrent.atomic.AtomicReference;

// Create a complex address object
Address address = new Address();
address.setStreet("789 Pine Road");
address.setCity("Capital City");
address.setZipCode("54321");

// Create an object with AtomicReference to a complex type
PersonWithAtomic person = new PersonWithAtomic();
person.setName("Jane Smith");
person.setCounter(new AtomicInteger(100));
person.setFlag(new AtomicBoolean(true));
person.setAddress(new AtomicReference<>(address));

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithAtomic> deserializer = new Deserializer<>(PersonWithAtomic.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithAtomic result = deserializer.deserialize(inputStream);

// Verify values
assert result.getName().equals("Jane Smith");
assert result.getCounter().get() == 100;
assert result.getFlag().get() == true;

// Verify AtomicReference<Address> was properly serialized and deserialized
assert result.getAddress() != null;
assert result.getAddress().get() != null;
assert result.getAddress().get().getStreet().equals("789 Pine Road");
assert result.getAddress().get().getCity().equals("Capital City");
assert result.getAddress().get().getZipCode().equals("54321");
```

### Enums

```java
// Status enum
public enum Status {
    ACTIVE, INACTIVE, PENDING
}

// Create an object with enum field
PersonWithEnum person = new PersonWithEnum();
person.setName("David Wilson");
person.setStatus(Status.ACTIVE);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithEnum> deserializer = new Deserializer<>(PersonWithEnum.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithEnum result = deserializer.deserialize(inputStream);

// Verify values
assert result.getName().equals("David Wilson");
assert result.getStatus() == Status.ACTIVE;
```

---

## Working with Date

```java
import java.util.Date;

// Create an object with Date field
PersonWithDate person = new PersonWithDate();
person.setName("Alice");
person.setBirthDate(new Date());

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithDate> deserializer = new Deserializer<>(PersonWithDate.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithDate result = deserializer.deserialize(inputStream);

// Verify values
assert result.getName().equals("Alice");
assert result.getBirthDate().getTime() == person.getBirthDate().getTime();
```

**Note:** Date is serialized as ISO 8601 string format (`yyyy-MM-dd'T'HH:mm:ss.SSSZ`) with millisecond precision and timezone information.

---

## Working with Random

```java
import java.util.Random;

// Create an object with Random field
PersonWithRandom person = new PersonWithRandom();
person.setName("Bob");
person.setRandomGenerator(new Random(12345L));

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithRandom> deserializer = new Deserializer<>(PersonWithRandom.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithRandom result = deserializer.deserialize(inputStream);

// Verify values
assert result.getName().equals("Bob");
assert result.getRandomGenerator() != null;

// Verify that the deserialized Random produces the same sequence
long originalNext = new Random(12345L).nextLong();
long deserializedNext = result.getRandomGenerator().nextLong();
assert originalNext == deserializedNext;
```

**Note:** Random is serialized as the value returned by `nextLong()`. On deserialization, a new `Random` is constructed using this value as the seed. This ensures that the deserialized Random produces the same sequence of random numbers as the original.

---

## Working with UUID

```java
import java.util.UUID;

// Create an object with UUID fields
PersonWithUUID person = new PersonWithUUID();
person.setName("Alice");
person.setId(UUID.randomUUID());
person.setSecondaryId(UUID.randomUUID());

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithUUID> deserializer = new Deserializer<>(PersonWithUUID.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithUUID result = deserializer.deserialize(inputStream);

// Verify values
assert result.getName().equals("Alice");
assert result.getId().equals(person.getId());
assert result.getSecondaryId().equals(person.getSecondaryId());
```

**Note:** UUID and other JDK types are serialized as their string representation and deserialized using their `fromString()` method.

---

## Working with Collections

### List of Strings

```java
import java.util.ArrayList;
import java.util.List;

// Create an object with List<String>
PersonWithCollections person = new PersonWithCollections();
person.setName("Eva Green");

List<String> tags = new ArrayList<>();
tags.add("developer");
tags.add("java");
tags.add("serialization");
person.setTags(tags);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithCollections> deserializer = new Deserializer<>(PersonWithCollections.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithCollections result = deserializer.deserialize(inputStream);

// Verify values
assert result.getName().equals("Eva Green");
assert result.getTags().size() == 3;
assert result.getTags().contains("java");
```

### List of Integers

```java
// Create an object with List<Integer>
PersonWithCollections person = new PersonWithCollections();
person.setName("Frank Miller");

List<Integer> scores = new ArrayList<>();
scores.add(95);
scores.add(87);
scores.add(92);
scores.add(88);
person.setScores(scores);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithCollections> deserializer = new Deserializer<>(PersonWithCollections.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithCollections result = deserializer.deserialize(inputStream);

// Verify values
assert result.getScores().size() == 4;
assert result.getScores().get(0) == 95;
```

### List of Objects

```java
// Create objects with nested object list
PersonWithCollections person = new PersonWithCollections();
person.setName("Grace Lee");

List<Address> addresses = new ArrayList<>();
Address addr1 = new Address();
addr1.setStreet("123 Main St");
addr1.setCity("New York");
addr1.setZipCode("10001");
addresses.add(addr1);

Address addr2 = new Address();
addr2.setStreet("456 Oak Ave");
addr2.setCity("Boston");
addr2.setZipCode("02108");
addresses.add(addr2);

person.setAddresses(addresses);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithCollections> deserializer = new Deserializer<>(PersonWithCollections.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithCollections result = deserializer.deserialize(inputStream);

// Verify values
assert result.getAddresses().size() == 2;
assert result.getAddresses().get(0).getCity().equals("New York");
```

### Queue Interface

The library automatically deserializes collection fields to the appropriate type based on the field type. For example, a `Queue<String>` field will be deserialized as a `LinkedList` (which implements `Queue`).

```java
import java.util.Queue;
import java.util.LinkedList;

// Game character with active effects queue
class GameCharacter {
    private String name;
    private Queue<String> activeEffects = new LinkedList<>();
    
    // getters and setters...
}

// Create character
GameCharacter hero = new GameCharacter();
hero.setName("Warrior");
hero.getActiveEffects().add("Strength Boost");
hero.getActiveEffects().add("Shield Buff");
hero.getActiveEffects().add("Speed Boost");

// Serialize and deserialize
Serializer serializer = new Serializer("game", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(hero, outputStream);

Deserializer<GameCharacter> deserializer = new Deserializer<>(GameCharacter.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
GameCharacter result = deserializer.deserialize(inputStream);

// Verify queue is deserialized correctly
assert result.getActiveEffects() instanceof LinkedList;
assert result.getActiveEffects().size() == 3;
assert result.getActiveEffects().poll().equals("Strength Boost"); // Queue method
```

### Set Interface

A `Set<String>` field will be deserialized as a `LinkedHashSet` (which preserves insertion order).

```java
import java.util.Set;
import java.util.LinkedHashSet;

class TaggedItem {
    private String name;
    private Set<String> tags = new LinkedHashSet<>();
    
    // getters and setters...
}

// Create item
TaggedItem item = new TaggedItem();
item.setName("Laptop");
item.getTags().add("electronics");
item.getTags().add("portable");
item.getTags().add("work");

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(item, outputStream);

Deserializer<TaggedItem> deserializer = new Deserializer<>(TaggedItem.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
TaggedItem result = deserializer.deserialize(inputStream);

// Verify set is deserialized correctly
assert result.getTags() instanceof LinkedHashSet;
assert result.getTags().size() == 3;
```

### SortedSet Interface

A `SortedSet<String>` field will be deserialized as a `TreeSet` (which maintains sorted order).

```java
import java.util.SortedSet;
import java.util.TreeSet;

class SortedWords {
    private String description;
    private SortedSet<String> words = new TreeSet<>();
    
    // getters and setters...
}

// Create sorted words
SortedWords sw = new SortedWords();
sw.setDescription("Alphabetical word list");
sw.getWords().add("zebra");
sw.getWords().add("apple");
sw.getWords().add("banana");

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(sw, outputStream);

Deserializer<SortedWords> deserializer = new Deserializer<>(SortedWords.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
SortedWords result = deserializer.deserialize(inputStream);

// Verify sorted set maintains order
Object[] words = result.getWords().toArray();
assert words[0].equals("apple"); // First alphabetically
assert words[1].equals("banana");
assert words[2].equals("zebra"); // Last alphabetically
```

### Deque Interface

A `Deque<String>` field will be deserialized as an `ArrayDeque` (efficient for stack/queue operations).

```java
import java.util.Deque;
import java.util.ArrayDeque;

class BrowserHistory {
    private Deque<String> backStack = new ArrayDeque<>();
    
    // getters and setters...
}

// Create history
BrowserHistory history = new BrowserHistory();
history.getBackStack().pushFirst("page1");
history.getBackStack().pushFirst("page2");
history.getBackStack().pushFirst("page3");

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(history, outputStream);

Deserializer<BrowserHistory> deserializer = new Deserializer<>(BrowserHistory.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
BrowserHistory result = deserializer.deserialize(inputStream);

// Verify deque is deserialized correctly
assert result.getBackStack() instanceof ArrayDeque;
assert result.getBackStack().size() == 3;
assert result.getBackStack().pollFirst().equals("page1"); // Deque method
```

---

## Working with Maps

### Map with String Keys

```java
import java.util.HashMap;
import java.util.Map;

// Create an object with Map<String, String>
PersonWithMap person = new PersonWithMap();
person.setName("Henry Ford");

Map<String, String> properties = new HashMap<>();
properties.put("department", "Engineering");
properties.put("location", "Detroit");
properties.put("level", "Senior");
person.setProperties(properties);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithMap> deserializer = new Deserializer<>(PersonWithMap.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithMap result = deserializer.deserialize(inputStream);

// Verify values
assert result.getName().equals("Henry Ford");
assert result.getProperties().get("department").equals("Engineering");
assert result.getProperties().get("location").equals("Detroit");
```

### Map with UUID Keys

```java
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

// Create an object with Map<UUID, String>
Map<UUID, String> quests = new LinkedHashMap<>();
UUID quest1 = UUID.fromString("00000000-0000-0000-0000-000000000001");
UUID quest2 = UUID.fromString("00000000-0000-0000-0000-000000000002");
quests.put(quest1, "The Lost Prayer Book");
quests.put(quest2, "Rare Arcane Materials");

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(quests, outputStream);

Deserializer<Map<UUID, String>> deserializer = new Deserializer<>(Map.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
Map<UUID, String> result = deserializer.deserialize(inputStream);

// Verify keys are UUID type, not String
for (Map.Entry<UUID, String> entry : result.entrySet()) {
    assert entry.getKey() instanceof UUID; // true
    assert entry.getValue() instanceof String; // true
}

// Can lookup by UUID (this would fail if keys were Strings)
assert result.get(quest1).equals("The Lost Prayer Book");
assert result.get(quest2).equals("Rare Arcane Materials");
```

**Note:** The deserializer uses reflection to extract generic type information from the map's `ParameterizedType`. For UUID and other JDK complex types, it uses `ValueSerializer.deserializeFromValue()` to convert string keys to the correct type.

---

## Map with Collection Values

### Map<String, List<String>>

```java
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Create an object with Map<String, List<String>>
Map<String, List<String>> tagsByCategory = new HashMap<>();
tagsByCategory.put("skills", List.of("java", "python", "javascript"));
tagsByCategory.put("languages", List.of("english", "spanish"));
tagsByCategory.put("empty", new ArrayList<>());

PersonWithMapOfCollections person = new PersonWithMapOfCollections("John Doe", tagsByCategory);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithMapOfCollections> deserializer = new Deserializer<>(PersonWithMapOfCollections.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithMapOfCollections result = deserializer.deserialize(inputStream);

// Verify values
assert result.getName().equals("John Doe");
assert result.getTagsByCategory().size() == 3;
assert result.getTagsByCategory().get("skills").size() == 3;
```

**Output:**
```
Serialized JSON:
{"$id":"app_1_com.example.PersonWithMapOfCollections","$class":"com.example.PersonWithMapOfCollections","fields":{"name":"John Doe","tagsByCategory":{"skills":["java","python","javascript"],"languages":["english","spanish"],"empty":[]}}

Deserialized object:
Name: John Doe
Map size: 3
Skills: [java, python, javascript]
```

**Note:** Collections in maps are serialized as JSON arrays (`[]`) without metadata. This produces idiomatic JSON and reduces output size.

---

## Working with Arrays

### Primitive Array

```java
// Create an object with int array
PersonWithArray person = new PersonWithArray();
person.setName("Iris West");

int[] values = {10, 20, 30, 40, 50};
person.setValues(values);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithArray> deserializer = new Deserializer<>(PersonWithArray.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithArray result = deserializer.deserialize(inputStream);

// Verify values
assert result.getValues().length == 5;
assert result.getValues()[0] == 10;
assert result.getValues()[4] == 50;
```

### Object Array

```java
// Create an object with object array
PersonWithArray person = new PersonWithArray();
person.setName("Jack Ryan");

Address[] addresses = new Address[2];
addresses[0] = new Address();
addresses[0].setStreet("100 Pine St");
addresses[0].setCity("Seattle");

addresses[1] = new Address();
addresses[1].setStreet("200 Elm St");
addresses[1].setCity("Portland");

person.setObjectAddresses(addresses);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithArray> deserializer = new Deserializer<>(PersonWithArray.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithArray result = deserializer.deserialize(inputStream);

// Verify values
assert result.getObjectAddresses().length == 2;
assert result.getObjectAddresses()[0].getCity().equals("Seattle");
```

---

## Object References

```java
// Create objects with references
Address address = new Address();
address.setStreet("789 Broadway");
address.setCity("Los Angeles");
address.setZipCode("90001");

SimplePerson person = new SimplePerson();
person.setName("Kate Bishop");
person.setAge(28);
person.setSalary(85000.00);

PersonWithReferences personWithRef = new PersonWithReferences();
personWithRef.setName("Kate Bishop");
personWithRef.setAddress(address);
personWithRef.setDepartment(null);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(personWithRef, outputStream);

Deserializer<PersonWithReferences> deserializer = new Deserializer<>(PersonWithReferences.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithReferences result = deserializer.deserialize(inputStream);

// Verify values
assert result.getName().equals("Kate Bishop");
assert result.getAddress().getCity().equals("Los Angeles");
assert result.getAddress().getZipCode().equals("90001");
```

---

## Circular References

```java
import java.util.ArrayList;
import java.util.List;

// Create circular reference structure
PersonWithCircularReference parent = new PersonWithCircularReference();
parent.setName("Parent Node");

PersonWithCircularReference child1 = new PersonWithCircularReference();
child1.setName("Child 1");
child1.setParent(parent);

PersonWithCircularReference child2 = new PersonWithCircularReference();
child2.setName("Child 2");
child2.setParent(parent);

List<PersonWithCircularReference> children = new ArrayList<>();
children.add(child1);
children.add(child2);
parent.setChildren(children);

// Serialize - circular reference is handled automatically
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(parent, outputStream);
String json = outputStream.toString();

System.out.println("Serialized JSON (with circular references):");
System.out.println(json);

// Deserialize - circular reference is restored
Deserializer<PersonWithCircularReference> deserializer = 
    new Deserializer<>(PersonWithCircularReference.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
PersonWithCircularReference result = deserializer.deserialize(inputStream);

// Verify circular reference is preserved
assert result.getName().equals("Parent Node");
assert result.getChildren().size() == 2;
assert result.getChildren().get(0).getParent() == result; // Same object reference
assert result.getChildren().get(1).getParent() == result; // Same object reference
```

### Circular References with Constructor-Based Deserialization

The library also handles circular references in immutable objects that use constructor injection with final fields. In this case, the deserializer uses a placeholder-based approach:

```java
// Parent has a Child, and Child has a reference back to Parent
// Both use final fields and constructor injection
public class Parent {
    private final String name;
    private final Child child;
    
    public Parent(String name, Child child) {
        this.name = name;
        this.child = child;
    }
    
    public String getName() { return name; }
    public Child getChild() { return child; }
}

public class Child {
    private final String name;
    private final Parent parent;
    
    public Child(String name, Parent parent) {
        this.name = name;
        this.parent = parent;
    }
    
    public String getName() { return name; }
    public Parent getParent() { return parent; }
}
```

**JSON with circular reference:**
```json
{
  "$id": "parent_1",
  "$class": "com.example.Parent",
  "fields": {
    "name": "parent",
    "child": {
      "$id": "child_1",
      "$class": "com.example.Child",
      "fields": {
        "name": "child",
        "parent": {
          "$ref": "parent_1"
        }
      }
    }
  }
}
```

**How it works:**
1. A placeholder is registered for `parent_1` before the Parent object is constructed
2. The Child is constructed with `parent` set to null (because the Parent doesn't exist yet)
3. The Parent is constructed with the Child
4. The placeholder is replaced with the actual Parent instance
5. If `parent` in Child were a non-final field, it would be resolved now

**Note:** For final fields in constructor parameters, circular references are set to null during construction and cannot be changed afterward (a limitation of Java's final field semantics). For non-final fields, circular references are properly resolved after the target object is available.

---

## Inheritance

```java
// BasePerson has base fields
// PersonWithInheritance extends BasePerson and adds additional fields

PersonWithInheritance person = new PersonWithInheritance();
person.setBaseField("Base Value");      // From BasePerson
person.setName("Leo Valdez");           // From PersonWithInheritance
person.setAge(25);                      // From PersonWithInheritance

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithInheritance> deserializer = new Deserializer<>(PersonWithInheritance.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithInheritance result = deserializer.deserialize(inputStream);

// Verify both base and derived fields
assert result.getBaseField().equals("Base Value");
assert result.getName().equals("Leo Valdez");
assert result.getAge() == 25;
```

---

## Immutable Objects

```java
// ImmutablePerson has no setters, only constructor
ImmutablePerson person = new ImmutablePerson("Maya Lopez", 32, 95000.00);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<ImmutablePerson> deserializer = new Deserializer<>(ImmutablePerson.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
ImmutablePerson result = deserializer.deserialize(inputStream);

// Verify values (immutable object is reconstructed via constructor)
assert result.getName().equals("Maya Lopez");
assert result.getAge() == 32;
assert result.getSalary() == 95000.00;
```

---

## Non-Public Constructors

The library supports deserialization of objects with protected, package-private, or private constructors. This is useful for classes that use the builder pattern or have restricted constructor access.

### Protected Constructor Example

```java
import java.math.BigDecimal;

// ItemWithProtectedConstructor has a protected constructor
// and a public static build method
ItemWithProtectedConstructor item = ItemWithProtectedConstructor.build(
    "Steel Plate",
    new BigDecimal("15.5"),
    500,
    1000,
    0
);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(item, outputStream);

Deserializer<ItemWithProtectedConstructor> deserializer =
    new Deserializer<>(ItemWithProtectedConstructor.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
ItemWithProtectedConstructor result = deserializer.deserialize(inputStream);

// Verify values
assert result.getName().equals("Steel Plate");
assert result.getWeight().compareTo(new BigDecimal("15.5")) == 0;
assert result.getValue() == 500;
assert result.getTotalDamageCapacity() == 1000;
assert result.getTotalDamageAbsorbed() == 0;
```

**Note:** The deserializer uses reflection to make non-public constructors accessible during deserialization. This allows the library to work with classes that use protected or private constructors without requiring modifications to the original class.

---

## Final Fields

```java
// PersonWithFinalFields has final fields
// These are set via constructor during deserialization

PersonWithFinalFields person = new PersonWithFinalFields();
person.setName("Nora Allen");
person.setAge(30);
person.setSalary(110000.00);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStr

eam);

Deserializer<PersonWithFinalFields> deserializer = new Deserializer<>(PersonWithFinalFields.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithFinalFields result = deserializer.deserialize(inputStream);

// Verify final fields are correctly set
assert result.getName().equals("Nora Allen");
assert result.getAge() == 30;
assert result.getSalary() == 110000.00;
```

---

## Null Fields

```java
// Create an object with null fields
PersonWithNullFields person = new PersonWithNullFields();
person.setName("Ollie Queen");
person.setAge(null);        // Integer (nullable)
person.setSalary(null);      // Double (nullable)
person.setActive(null);      // Boolean (nullable)

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithNullFields> deserializer = new Deserializer<>(PersonWithNullFields.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithNullFields result = deserializer.deserialize(inputStream);

// Verify null values are preserved
assert result.getName().equals("Ollie Queen");
assert result.getAge() == null;
assert result.getSalary() == null;
assert result.getActive() == null;
```

---

## Static and Transient Fields

```java
// PersonWithStaticTransient has static and transient fields
// These are automatically excluded from serialization

PersonWithStaticTransient person = new PersonWithStaticTransient();
person.setName("Peter Parker");
person.setAge(28);

// Static field - not serialized
PersonWithStaticTransient.setStaticField("Static Value");

// Transient field - not serialized
person.setTransientField("Transient Value");

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

// Clear static field to verify it's not from serialization
PersonWithStaticTransient.setStaticField("Different Value");

Deserializer<PersonWithStaticTransient> deserializer = new Deserializer<>(PersonWithStaticTransient.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithStaticTransient result = deserializer.deserialize(inputStream);

// Verify static and transient fields are NOT restored
assert result.getName().equals("Peter Parker");
assert result.getAge() == 28;
assert result.getTransientField() == null;  // Transient not restored
assert PersonWithStaticTransient.getStaticField().equals("Different Value");  // Static not restored
```

---

## SerialVersionUID

```java
// PersonWithSerialVersionUID has a serialVersionUID field
PersonWithSerialVersionUID person = new PersonWithSerialVersionUID();
person.setName("Quinn Parker");
person.setAge(35);
person.setSalary(125000.00);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);
String json = outputStream.toString();

// Check that serialVersionUID is included in JSON
assert json.contains("serialVersionUID");

// Deserialize
Deserializer<PersonWithSerialVersionUID> deserializer = new Deserializer<>(PersonWithSerialVersionUID.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
PersonWithSerialVersionUID result = deserializer.deserialize(inputStream);

// Verify values
assert result.getName().equals("Quinn Parker");
assert result.getAge() == 35;

// If serialVersionUID doesn't match, a warning is generated
List<String> warnings = deserializer.getWarnings();
for (String warning : warnings) {
    System.out.println("Warning: " + warning);
}
```

---

## Error Handling

```java
import com.pjr22.serialization.core.SerializationException;

try {
    // Attempt to deserialize invalid JSON
    String invalidJson = "{invalid json}";
    ByteArrayInputStream inputStream = new ByteArrayInputStream(invalidJson.getBytes());
    
    Deserializer<SimplePerson> deserializer = new Deserializer<>(SimplePerson.class);
    SimplePerson person = deserializer.deserialize(inputStream);
    
} catch (SerializationException e) {
    System.err.println("Serialization error: " + e.getMessage());
    e.printStackTrace();
}

// Handling warnings
Deserializer<SimplePerson> deserializer = new Deserializer<>(SimplePerson.class);
// ... deserialize ...
List<String> warnings = deserializer.getWarnings();
if (!warnings.isEmpty()) {
    System.out.println("Warnings during deserialization:");
    for (String warning : warnings) {
        System.out.println("  - " + warning);
    }
}
```

---

## Working with Files

### Serialize to File

```java
import java.io.FileOutputStream;
import java.io.FileInputStream;

// Create object
SimplePerson person = new SimplePerson();
person.setName("Rachel Green");
person.setAge(27);
person.setSalary(80000.00);
person.setActive(true);

// Serialize to file
Serializer serializer = new Serializer("myapp", 1);
try (FileOutputStream fileOut = new FileOutputStream("person.json")) {
    serializer.serialize(person, fileOut);
    System.out.println("Object serialized to person.json");
}
```

### Deserialize from File

```java
// Deserialize from file
Deserializer<SimplePerson> deserializer = new Deserializer<>(SimplePerson.class);
try (FileInputStream fileIn = new FileInputStream("person.json")) {
    SimplePerson person = deserializer.deserialize(fileIn);
    System.out.println("Name: " + person.getName());
    System.out.println("Age: " + person.getAge());
}
```

---

## Advanced Usage

### Using ObjectIdGenerator Directly

```java
import com.pjr22.serialization.registry.ObjectIdGenerator;

// Create an ID generator
ObjectIdGenerator generator = new ObjectIdGenerator("myapp", 1);

// Generate IDs for different classes
String id1 = generator.generateId("com.example.Person");
String id2 = generator.generateId("com.example.Address");
String id3 = generator.generateId("com.example.Person");

System.out.println(id1); // myapp_1_com.example.Person
System.out.println(id2); // myapp_2_com.example.Address
System.out.println(id3); // myapp_3_com.example.Person

// Get current counter value
System.out.println("Counter: " + generator.getCounter()); // 4

// Get serialization key
System.out.println("Key: " + generator.getSerializationKey()); // myapp
```

### Using ObjectRegistry Directly

```java
import com.pjr22.serialization.registry.ObjectRegistry;

// Create a registry
ObjectRegistry registry = new ObjectRegistry();

// Register objects
SimplePerson person1 = new SimplePerson();
person1.setName("Sam Wilson");
registry.register("obj1", person1);

SimplePerson person2 = new SimplePerson();
person2.setName("Tony Stark");
registry.register("obj2", person2);

// Register null
registry.register("obj3", null);

// Check if objects exist
System.out.println("Contains obj1: " + registry.contains("obj1")); // true
System.out.println("Contains obj4: " + registry.contains("obj4")); // false

// Retrieve objects
SimplePerson retrieved1 = (SimplePerson) registry.get("obj1");
SimplePerson retrieved2 = (SimplePerson) registry.get("obj2");
Object retrieved3 = registry.get("obj3"); // null

System.out.println("Registry size: " + registry.size()); // 3

// Get all object IDs
Set<String> ids = registry.getAllObjectIds();
System.out.println("Object IDs: " + ids);

// Clear registry
registry.clear();
System.out.println("Registry size after clear: " + registry.size()); // 0
```

### Using FieldInspector and FieldClassifier

```java
import com.pjr22.serialization.inspector.FieldInspector;
import com.pjr22.serialization.inspector.FieldClassifier;
import java.lang.reflect.Field;

// Get all fields from a class
Field[] fields = FieldInspector.getAllFields(SimplePerson.class);

System.out.println("Fields in SimplePerson:");
for (Field field : fields) {
    String fieldName = field.getName();
    Class<?> fieldType = field.getType();
    FieldClassifier.FieldCategory category = FieldClassifier.classify(field);
    
    System.out.println("  " + fieldName + ": " + fieldType.getSimpleName() + " -> " + category);
}

// Output example:
//   name: String -> STRING
//   age: int -> PRIMITIVE
//   salary: double -> NUMBER
//   active: boolean -> PRIMITIVE
```

### Using ConstructorAnalyzer

```java
import com.pjr22.serialization.inspector.ConstructorAnalyzer;
import java.lang.reflect.Constructor;
import java.util.Set;
import java.util.HashSet;

// Get field names
Set<String> fieldNames = new HashSet<>();
fieldNames.add("name");
fieldNames.add("age");
fieldNames.add("salary");

// Select best constructor
Constructor<?> constructor = ConstructorAnalyzer.selectBestConstructor(
    SimplePerson.class, 
    fieldNames
);

if (constructor != null) {
    System.out.println("Selected constructor: " + constructor);
    
    // Count matching parameter names
    int matchCount = ConstructorAnalyzer.countMatchingParameterNames(
        constructor, 
        fieldNames
    );
    System.out.println("Matching parameters: " + matchCount);
    
    // Check type compatibility
    boolean compatible = ConstructorAnalyzer.isTypeCompatible(
        long.class,   // parameter type
        int.class      // field type
    );
    System.out.println("long compatible with int: " + compatible); // true
}
```

### Using JsonSerializer and JsonParser

```java
import com.pjr22.serialization.format.JsonSerializer;
import com.pjr22.serialization.format.JsonParser;
import java.util.List;
import java.util.Map;

// Serialize various types to JSON
String nullJson = JsonSerializer.serialize(null);
System.out.println("null: " + nullJson); // null

String boolJson = JsonSerializer.serialize(true);
System.out.println("boolean: " + boolJson); // true

String intJson = JsonSerializer.serialize(42);
System.out.println("int: " + intJson); // 42

String doubleJson = JsonSerializer.serialize(3.14159);
System.out.println("double: " + doubleJson); // 3.14159

String stringJson = JsonSerializer.serialize("Hello, World!");
System.out.println("string: " + stringJson); // "Hello, World!"

String listJson = JsonSerializer.serialize(List.of(1, 2, 3));
System.out.println("list: " + listJson); // [1,2,3]

String mapJson = JsonSerializer.serialize(Map.of("key", "value"));
System.out.println("map: " + mapJson); // {"key":"value"}

// Parse JSON strings
Object parsedNull = JsonParser.parse("null");
System.out.println("parsed null: " + parsedNull); // null

Object parsedBool = JsonParser.parse("true");
System.out.println("parsed boolean: " + parsedBool); // true

Object parsedInt = JsonParser.parse("42");
System.out.println("parsed int: " + parsedInt); // 42

Object parsedDouble = JsonParser.parse("3.14159");
System.out.println("parsed double: " + parsedDouble); // 3.14159

Object parsedString = JsonParser.parse("\"Hello, World!\"");
System.out.println("parsed string: " + parsedString); // Hello, World!

Object parsedList = JsonParser.parse("[1,2,3]");
System.out.println("parsed list: " + parsedList); // [1, 2, 3]

Object parsedMap = JsonParser.parse("{\"key\":\"value\"}");
System.out.println("parsed map: " + parsedMap); // {key=value}
```

---

## Complete Example: Full Application

```java
import com.pjr22.serialization.core.Serializer;
import com.pjr22.serialization.core.Deserializer;
import com.pjr22.serialization.core.SerializationException;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class SerializationExample {
    
    public static void main(String[] args) {
        // Create a complex object graph
        Department engineering = new Department();
        engineering.setName("Engineering");
        engineering.setBudget(new BigDecimal("2500000.00"));
        
        SimplePerson manager = new SimplePerson();
        manager.setName("Sarah Connor");
        manager.setAge(40);
        manager.setSalary(150000.00);
        manager.setActive(true);
        engineering.setManager(manager);
        
        List<PersonWithReferences> employees = new ArrayList<>();
        
        for (int i = 1; i <= 3; i++) {
            PersonWithReferences employee = new PersonWithReferences();
            employee.setName("Employee " + i);
            employee.setAge(25 + i);
            employee.setSalary(75000.00 + (i * 5000));
            
            Address address = new Address();
            address.setStreet((100 * i) + " Main St");
            address.setCity("Tech City");
            address.setZipCode("9000" + i);
            employee.setAddress(address);
            
            employees.add(employee);
        }
        
        // Serialize to file
        Serializer serializer = new Serializer("company", 1);
        try (FileOutputStream fos = new FileOutputStream("department.json")) {
            serializer.serialize(engineering, fos);
            System.out.println("Department serialized to department.json");
        } catch (IOException | SerializationException e) {
            System.err.println("Error serializing: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
        // Deserialize from file
        Deserializer<Department> deserializer = new Deserializer<>(Department.class);
        Department deserializedDept = null;
        try (FileInputStream fis = new FileInputStream("department.json")) {
            deserializedDept = deserializer.deserialize(fis);
            System.out.println("Department deserialized from department.json");
        } catch (IOException | SerializationException e) {
            System.err.println("Error deserializing: " + e.getMessage());
            e.printStackTrace();
            return;
        }
        
        // Print deserialized data
        System.out.println("\n=== Deserialized Department ===");
        System.out.println("Name: " + deserializedDept.getName());
        System.out.println("Budget: $" + deserializedDept.getBudget());
        System.out.println("Manager: " + deserializedDept.getManager().getName());
        
        // Check for warnings
        List<String> warnings = deserializer.getWarnings();
        if (!warnings.isEmpty()) {
            System.out.println("\nWarnings:");
            for (String warning : warnings) {
                System.out.println("  - " + warning);
            }
        }
    }
}
```

---

## Best Practices

1. **Use meaningful serialization keys**: Choose keys that identify your application or context.
2. **Handle exceptions properly**: Always catch `SerializationException` and handle errors gracefully.
3. **Check warnings**: After deserialization, check for warnings that might indicate version mismatches.
4. **Use try-with-resources**: When working with streams, use try-with-resources for proper resource management.
5. **Test round-trip serialization**: Always verify that objects can be serialized and deserialized correctly.
6. **Consider thread safety**: Create new `Serializer` and `Deserializer` instances per thread or use external synchronization.
7. **Validate deserialized objects**: After deserialization, validate critical fields to ensure data integrity.
