# Java Serializer/Deserializer

A lightweight, dependency-free Java library for serializing and deserializing Java objects to/from JSON format. Built with test-driven development (TDD) and designed to handle complex object graphs including circular references, collections, arrays, and inheritance hierarchies.

## Features

- **Zero Dependencies**: Pure Java implementation with no external libraries
- **JSON Format**: Human-readable JSON output for easy debugging and interoperability
- **Complex Object Graphs**: Handles nested objects, collections, arrays, and maps
- **Circular References**: Properly handles object references and circular dependencies
- **Type Support**:
  - Primitive types (byte, short, int, long, float, double, char, boolean)
  - String and Character
  - Number types (including BigDecimal)
  - Atomic types (AtomicBoolean, AtomicInteger, AtomicLong, AtomicReference)
  - Collections (List, Set, etc.)
  - Maps
  - Arrays
  - Enums
  - JDK types (UUID, Date, Random, etc.) - Serialized as string representation or numeric value
- **Inheritance**: Supports serialization/deserialization of class hierarchies
- **Immutable Objects**: Works with immutable classes using constructor injection
- **Final Fields**: Handles final fields via constructor or reflection
- **SerialVersionUID**: Includes and validates serialVersionUID for version compatibility
- **Field Filtering**: Automatically excludes static and transient fields

## Requirements

- Java 17 or higher
- No external dependencies

## Installation

Since this is a plain Java project with no external dependencies, simply copy the source files from the `src/com/pjr22/serialization` directory to your project.

### Compile

```bash
# Compile all source files
javac -d bin -sourcepath src src/com/pjr22/serialization/**/*.java
```

### Run Tests

```bash
# Compile test classes
javac -d bin -sourcepath src src/com/pjr22/serialization/test/*.java src/com/pjr22/serialization/test/data/*.java

# Run all tests
java -cp bin com.pjr22.serialization.test.TestRunner

# Run individual test class
java -cp bin com.pjr22.serialization.test.SerializerTest
```

## Quick Start

### Basic Serialization and Deserialization

```java
import com.pjr22.serialization.core.Serializer;
import com.pjr22.serialization.core.Deserializer;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

// Create a simple object
SimplePerson person = new SimplePerson();
person.setName("John Doe");
person.setAge(30);
person.setSalary(75000.50);
person.setActive(true);

// Serialize to JSON
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);
String json = outputStream.toString();

System.out.println(json);

// Deserialize from JSON
Deserializer<SimplePerson> deserializer = new Deserializer<>(SimplePerson.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
SimplePerson deserialized = deserializer.deserialize(inputStream);

System.out.println(deserialized.getName()); // "John Doe"
```

### Serialization Output Format

The serializer produces JSON in the following format:

```json
{
  "$id": "app_1_com.example.SimplePerson",
  "$class": "com.example.SimplePerson",
  "serialVersionUID": 1,
  "fields": {
    "name": "John Doe",
    "age": 30,
    "salary": 75000.50,
    "active": true
  }
}
```

### Handling Circular References

The library automatically handles circular references using object IDs:

```java
PersonWithCircularReference parent = new PersonWithCircularReference();
parent.setName("Parent");

PersonWithCircularReference child = new PersonWithCircularReference();
child.setName("Child");
child.setParent(parent);

parent.addChild(child);

// Serialize - circular reference is handled automatically
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(parent, outputStream);

// Deserialize - circular reference is restored
Deserializer<PersonWithCircularReference> deserializer = 
    new Deserializer<>(PersonWithCircularReference.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithCircularReference result = deserializer.deserialize(inputStream);

// child.getParent() == result (same object)
```

### Working with Collections

```java
PersonWithCollections person = new PersonWithCollections();
person.setName("Alice");

List<String> tags = new ArrayList<>();
tags.add("developer");
tags.add("java");
person.setTags(tags);

List<Integer> scores = new ArrayList<>();
scores.add(95);
scores.add(87);
scores.add(92);
person.setScores(scores);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithCollections> deserializer = 
    new Deserializer<>(PersonWithCollections.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithCollections result = deserializer.deserialize(inputStream);
```

### Working with Maps

```java
PersonWithMap person = new PersonWithMap();
person.setName("Bob");

Map<String, String> properties = new HashMap<>();
properties.put("department", "Engineering");
properties.put("location", "New York");
person.setProperties(properties);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithMap> deserializer = new Deserializer<>(PersonWithMap.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithMap result = deserializer.deserialize(inputStream);
```

### Working with Enums

```java
PersonWithEnum person = new PersonWithEnum();
person.setName("Charlie");
person.setStatus(Status.ACTIVE);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<PersonWithEnum> deserializer = new Deserializer<>(PersonWithEnum.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
PersonWithEnum result = deserializer.deserialize(inputStream);

System.out.println(result.getStatus()); // Status.ACTIVE
```

### Working with Immutable Objects

The library supports immutable objects using constructor injection:

```java
ImmutablePerson person = new ImmutablePerson("David", 35, 90000.00);

// Serialize and deserialize
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);

Deserializer<ImmutablePerson> deserializer = new Deserializer<>(ImmutablePerson.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
ImmutablePerson result = deserializer.deserialize(inputStream);
```

## API Reference

### Core Classes

#### [`Serializer`](src/com/pjr22/serialization/core/Serializer.java)
Main class for serializing Java objects to JSON format.

- `Serializer(String serializationKey, int startingId)` - Creates a new serializer
- `void serialize(Object object, OutputStream outputStream)` - Serializes an object to the output stream
- `int getCounter()` - Returns the current object ID counter value
- `String getSerializationKey()` - Returns the serialization key

#### [`Deserializer<T>`](src/com/pjr22/serialization/core/Deserializer.java)
Main class for deserializing Java objects from JSON format.

- `Deserializer(Class<T> targetType)` - Creates a new deserializer for the specified type
- `T deserialize(InputStream inputStream)` - Deserializes an object from the input stream
- `List<String> getWarnings()` - Returns any warnings generated during deserialization

#### [`SerializationException`](src/com/pjr22/serialization/core/SerializationException.java)
Exception thrown when a serialization or deserialization error occurs.

- `SerializationException(String message)` - Creates exception with message
- `SerializationException(String message, Throwable cause)` - Creates exception with message and cause

### Inspector Classes

#### [`FieldInspector`](src/com/pjr22/serialization/inspector/FieldInspector.java)
Inspects Java classes to extract fields for serialization.

- `static Field[] getAllFields(Class<?> clazz)` - Gets all non-static, non-transient fields

#### [`FieldClassifier`](src/com/pjr22/serialization/inspector/FieldClassifier.java)
Classifies Java fields into categories for serialization.

- `static FieldCategory classify(Field field)` - Classifies a field into a category

#### [`ConstructorAnalyzer`](src/com/pjr22/serialization/inspector/ConstructorAnalyzer.java)
Analyzes and selects the best constructor for deserialization.

- `static Constructor<?> selectBestConstructor(Class<?> clazz, Set<String> fieldNames)` - Selects the best constructor
- `static int countMatchingParameterNames(Constructor<?> constructor, Set<String> fieldNames)` - Counts matching parameter names
- `static boolean isTypeCompatible(Class<?> paramType, Class<?> fieldType)` - Checks type compatibility

### Registry Classes

#### [`ObjectIdGenerator`](src/com/pjr22/serialization/registry/ObjectIdGenerator.java)
Generates unique object IDs for serialization.

- `ObjectIdGenerator(String serializationKey, int startingId)` - Creates a new ID generator
- `String generateId(String className)` - Generates a unique object ID
- `int getCounter()` - Returns the current counter value
- `String getSerializationKey()` - Returns the serialization key

#### [`ObjectRegistry`](src/com/pjr22/serialization/registry/ObjectRegistry.java)
Registry for storing objects during deserialization.

- `ObjectRegistry()` - Creates a new empty registry
- `void register(String objectId, Object object)` - Registers an object
- `Object get(String objectId)` - Retrieves an object by ID
- `boolean contains(String objectId)` - Checks if an object is registered
- `void clear()` - Clears all registered objects
- `Set<String> getAllObjectIds()` - Returns all registered object IDs
- `int size()` - Returns the number of registered objects

### Utility Classes

#### [`CollectionFactory`](src/com/pjr22/serialization/util/CollectionFactory.java)
Factory for creating appropriate collection instances based on target field type.

- `static <T> Collection<T> createCollection(Class<?> fieldType)` - Creates a collection instance appropriate for the specified field type

**Supported Collection Types:**
| Field Type | Implementation |
|-------------|----------------|
| `java.util.List` | `ArrayList` |
| `java.util.Set` | `LinkedHashSet` |
| `java.util.SortedSet` / `java.util.NavigableSet` | `TreeSet` |
| `java.util.Queue` | `LinkedList` |
| `java.util.Deque` | `ArrayDeque` |
| `java.util.concurrent.BlockingQueue` | `LinkedBlockingQueue` |
| `java.util.concurrent.BlockingDeque` | `LinkedBlockingDeque` |
| `java.util.concurrent.TransferQueue` | `LinkedBlockingQueue` |
| `ArrayList` | `ArrayList` (via no-arg constructor) |
| `LinkedList` | `LinkedList` (via no-arg constructor) |
| `HashSet` | `HashSet` (via no-arg constructor) |
| `LinkedHashSet` | `LinkedHashSet` (via no-arg constructor) |
| `TreeSet` | `TreeSet` (via no-arg constructor) |
| `PriorityQueue` | `PriorityQueue` (via no-arg constructor) |
| `ArrayDeque` | `ArrayDeque` (via no-arg constructor) |

#### [`ValueSerializer`](src/com/pjr22/serialization/util/ValueSerializer.java)
Utility class for serializing and deserializing JDK classes that can be constructed with a single value (String or Number).

- `static boolean canSerializeAsValue(Class<?> clazz)` - Checks if a class can be serialized as a simple value
- `static Object serializeAsValue(Object obj)` - Serializes an object to a simple value (String or Number)
- `static <T> T deserializeFromValue(Object value, Class<T> targetClass)` - Deserializes a simple value to an object

**Supported JDK Types:**
- `UUID` - Serialized as string representation, deserialized via `UUID.fromString()`
- `Date` - Serialized as ISO 8601 string format (`yyyy-MM-dd'T'HH:mm:ss.SSSZ`), deserialized via `SimpleDateFormat`
- `Random` - Serialized as `nextLong()` value, deserialized via `Random(long)` constructor

### Format Classes

#### [`JsonSerializer`](src/com/pjr22/serialization/format/JsonSerializer.java)
Serializes Java objects to JSON format.

- `static String serialize(Object value)` - Serializes an object to JSON string

#### [`JsonParser`](src/com/pjr22/serialization/format/JsonParser.java)
Parses JSON strings into Java objects.

- `static Object parse(String json)` - Parses a JSON string

## Design Decisions

### Why JSON Format?

JSON was chosen as the serialization format because:
- Human-readable and easy to debug
- Widely supported across languages and platforms
- No external parsing libraries required
- Easy to inspect and modify manually if needed

### Object ID Format

Object IDs are generated in the format: `{serializationKey}_{counter}_{className}`

This format ensures:
- Uniqueness across different serialization sessions
- Easy identification of the object's class
- Deterministic and reproducible IDs

### Circular Reference Handling

Circular references are handled using:
- `$id` field: Unique identifier for each object
- `$ref` field: Reference to an already-serialized object
- Object registry: Tracks all objects during serialization/deserialization

### Field Filtering

The following fields are automatically excluded:
- **Static fields**: Not part of object state
- **Transient fields**: Marked as non-serializable by convention

### Constructor Selection

The deserializer uses [`ConstructorAnalyzer`](src/com/pjr22/serialization/inspector/ConstructorAnalyzer.java) to select the best constructor:
1. Supports all constructor visibility levels (public, protected, package-private, private)
2. Prefers constructors with more parameters
3. Matches parameter names to field names
4. Handles type conversions (e.g., int to long)
5. Falls back to default constructor and setters if needed

**Note:** Non-public constructors are made accessible via reflection during deserialization, allowing the library to work with classes that use protected or private constructors (e.g., builder pattern classes).

## Testing

The library includes comprehensive tests covering:
- Unit tests for individual components
- Integration tests for end-to-end serialization/deserialization
- Edge cases (circular references, inheritance, null fields, etc.)
- Type conversion tests

Run all tests:
```bash
java -cp bin com.pjr22.serialization.test.TestRunner
```

## Project Structure

```
src/com/pjr22/serialization/
├── core/          # Serializer, Deserializer, SerializationException
├── format/        # JSON serialization and parsing
├── inspector/     # Field inspection and classification
├── registry/      # Object ID generation and registry
├── util/          # Utility classes
└── test/          # Test framework and test data
    └── data/      # Test data classes
```

## Documentation

- [`docs/requirements-and-design.md`](docs/requirements-and-design.md) - Original requirements and design document
- [`docs/api.md`](docs/api.md) - Detailed API documentation
- [`docs/examples.md`](docs/examples.md) - Usage examples

## License

This project is provided as-is for educational and commercial use.

## Contributing

Contributions are welcome! Please ensure all tests pass before submitting changes.

```bash
# Run tests
java -cp bin com.pjr22.serialization.test.TestRunner
```

## Recent Fixes

### Map Key Type Deserialization (2025-12-30)

**Issue**: When deserializing objects with Map fields where the key type is not String (e.g., `Map<UUID, Quest>`), the deserializer was not converting JSON string keys to the appropriate Java type. Since JSON map keys are always strings, map keys like UUID were being deserialized as String objects instead of UUID, causing type mismatches and lookup failures.

**Root Cause**: The `convertMapKey()` method in [`Deserializer.java`](src/com/pjr22/serialization/core/Deserializer.java) only handled primitive types, wrapper types, and enums. It did not handle complex JDK types like UUID that can be constructed from a string via `fromString()` method or single-parameter constructor.

**Fix**:
1. Modified `convertMapKey()` method to use [`ValueSerializer.deserializeFromValue()`](src/com/pjr22/serialization/util/ValueSerializer.java) for any type that can be deserialized from a string
2. This provides a generalized solution that works for UUID, Date, and any other JDK type with `fromString(String)` method or single-parameter constructor
3. Added test case `testUUIDKeyMapDeserialization()` in [`MapKeyTypeDeserializationTest.java`](src/com/pjr22/serialization/test/MapKeyTypeDeserializationTest.java) to verify UUID keys are properly deserialized

**Impact**: Map keys of complex types (UUID, Date, etc.) are now correctly deserialized as their proper types, enabling correct type checking and map lookups.

### Constructor Parameter Value Deserialization (2025-12-29)

**Issue**: When deserializing objects with parameterized constructors that create new Map/Collection instances from parameters (e.g., `new TreeMap<>(map)`), the deserializer was passing `null` values to the constructor, causing `NullPointerException`.

**Root Cause**: The `createWithConstructor` method was using default values (null for objects) for all constructor parameters instead of deserializing the actual field values from the JSON data.

**Fix**:
1. Modified `createInstance` to accept the full `fields` map instead of just field names
2. Added `deserializeValueForConstructor` method that properly deserializes field values to expected parameter types (including Map, Collection, Array, and primitive types)
3. Updated `build.sh` to include `-parameters` javac flag to preserve parameter names in compiled bytecode

**Impact**: Classes with constructors that expect non-null Map/Collection parameters (like the `Config` class in the issue report) can now be deserialized correctly.

### Map Values Containing Nested Objects (2025-12-30)

**Issue**: When deserializing objects with Map fields where values contain nested objects (e.g., `Map<String, NaturalInventoryContainer>`), the `ValueSerializer.canSerializeAsValue()` was incorrectly treating `LinkedHashMap` as a simple value type. This caused Map values to be serialized as string representations (via `toString()`) instead of proper nested object structures, which could not be deserialized back to the original objects.

**Root Cause**: The `ValueSerializer.canSerializeAsValue()` method was checking for JDK classes with `fromString()` methods or single-parameter constructors but did not exclude Map types. Since `LinkedHashMap` is a JDK class with a Map constructor, it was being serialized as a simple value using `toString()`, producing output like `{$id=test_10, $class=org.pjr22.adv.items.NaturalInventoryContainer, ...}`. During deserialization, this string could not be converted back to a proper `LinkedHashMap`, causing the map values to be lost.

**Fix**:
1. Added import for `java.util.Map` in [`ValueSerializer.java`](src/com/pjr22/serialization/util/ValueSerializer.java)
2. Added exclusion check for Map types in `canSerializeAsValue()` method, similar to the existing Collection exclusion
3. Added test case `testDeserializeMapWithNestedObjectValues()` in [`DeserializerTest.java`](src/com/pjr22/serialization/test/DeserializerTest.java) to verify Map values containing nested objects are properly serialized and deserialized
4. Created test data class [`PersonWithMapOfPeople.java`](src/com/pjr22/serialization/test/data/PersonWithMapOfPeople.java) for testing

**Impact**: Map values containing nested objects are now correctly serialized as JSON objects (not strings), ensuring proper round-trip serialization and deserialization.

### Circular Reference Constructor Handling (2026-01-01)

**Issue**: When deserializing objects with circular references through constructor parameters (e.g., immutable classes with final fields), the deserializer would throw a "Referenced object not found" exception. This occurred because when constructing a child object that references a parent object still being constructed, the parent's object ID was not yet registered in the ObjectRegistry.

**Root Cause**: The deserializer registered objects in the ObjectRegistry only after they were fully constructed. When a nested object's constructor parameter referenced back to the parent object, the reference could not be resolved because the parent was not yet registered.

**Fix**:
1. Added placeholder-based circular reference handling in [`Deserializer.java`](src/com/pjr22/serialization/core/Deserializer.java)
2. Register a placeholder object in the ObjectRegistry BEFORE creating the instance
3. Track unresolved references using `UnresolvedReferenceMarker` and `UnresolvedReference` classes
4. After an object is fully constructed, resolve all pending references to it via `resolveUnresolvedReferences()` method
5. For constructor parameters, unresolved references are set to null (since final fields cannot be modified after construction)
6. For non-final fields, unresolved references are properly set after the target object is available

**Impact**: Objects with circular references through constructor parameters can now be deserialized successfully. Non-final fields with circular references are properly resolved. Final fields in constructor parameters receive null values (a limitation of Java's final field semantics).

**Example**:
```java
// Parent has a Child, and Child has a reference back to Parent
public class Parent {
    private final String name;
    private final Child child;
    
    public Parent(String name, Child child) {
        this.name = name;
        this.child = child;
    }
}

public class Child {
    private final String name;
    private final Parent parent;
    
    public Child(String name, Parent parent) {
        this.name = name;
        this.parent = parent;
    }
}
```

During deserialization:
1. A placeholder is registered for `parent_1`
2. The `Child` is constructed with `parent` set to null (unresolved)
3. The `Parent` is constructed with the `Child`
4. The placeholder is replaced with the actual `Parent` instance
5. If `parent` in `Child` were non-final, it would be resolved now
