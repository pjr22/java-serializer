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
1. Prefers constructors with more parameters
2. Matches parameter names to field names
3. Handles type conversions (e.g., int to long)
4. Falls back to default constructor and setters if needed

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
