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
  - Collections (List, Set, Deque, Queue, etc.)
  - Maps (HashMap, LinkedHashMap, TreeMap, etc.)
  - Arrays
  - Enums
  - JDK types (UUID, Date, Random, etc.)
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
./build.sh
```

### Run Tests

```bash
./test.sh
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

// Serialize to JSON
Serializer serializer = new Serializer("app", 1);
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);
String json = outputStream.toString();

// Deserialize from JSON
Deserializer<SimplePerson> deserializer = new Deserializer<>(SimplePerson.class);
ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
SimplePerson deserialized = deserializer.deserialize(inputStream);
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
    "age": 30
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
```

### Working with Maps

```java
PersonWithMap person = new PersonWithMap();
person.setName("Bob");

Map<String, String> properties = new HashMap<>();
properties.put("department", "Engineering");
properties.put("location", "New York");
person.setProperties(properties);

// Maps are serialized as plain JSON objects:
// {"department": "Engineering", "location": "New York"}
```

**Note**: All Map instances are serialized as plain JSON objects (without `$id`/`$class` metadata). The Map implementation type is not preserved and will generally deserialize as `LinkedHashMap`.

### Typed Map Keys

Map keys are automatically converted to their appropriate types during deserialization:

```java
// Map<UUID, String> - UUID keys are properly converted from JSON strings
// Map<Long, String> - Long keys are properly converted from JSON strings
// Map<MyEnum, String> - Enum keys are properly converted from JSON strings
```

**Limitation**: Typed map key conversion is applied for Map-typed fields and constructor parameters. Maps inside collections (e.g., `List<Map<Long, String>>`) currently deserialize with String keys.

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

### Inspector Classes

#### [`FieldInspector`](src/com/pjr22/serialization/inspector/FieldInspector.java)

Inspects Java classes to extract fields for serialization.

- `static Field[] getAllFields(Class<?> clazz)` - Gets all non-static, non-transient fields

#### [`FieldClassifier`](src/com/pjr22/serialization/inspector/FieldClassifier.java)

Classifies fields into categories for appropriate serialization.

#### [`ConstructorAnalyzer`](src/com/pjr22/serialization/inspector/ConstructorAnalyzer.java)

Analyzes constructors to find the best one for deserialization.

## Design Decisions

### JSON Format

The JSON format was chosen for:
- Human readability and debugging
- Wide interoperability
- No external parsing libraries required

### Object ID Format

Object IDs are generated in the format: `{serializationKey}_{counter}_{className}`

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
4. Falls back to default constructor if needed

## Project Structure

```
src/com/pjr22/serialization/
├── core/          # Serializer, Deserializer, SerializationException
├── format/        # JSON serialization and parsing
├── inspector/     # Field inspection and classification
├── registry/      # Object ID generation and registry
├── util/          # Utility classes (ValueSerializer, CollectionFactory)
└── test/          # Test framework and test data
    └── data/      # Test data classes
```

## Documentation

- [`docs/requirements-and-design.md`](docs/requirements-and-design.md) - Original requirements and design document
- [`docs/api.md`](docs/api.md) - Detailed API documentation
- [`docs/examples.md`](docs/examples.md) - Usage examples
- [`CHANGELOG.md`](CHANGELOG.md) - Version history and changes

## License

This project is provided as-is for educational and commercial use.

## Contributing

Contributions are welcome! Please ensure all tests pass before submitting changes.

```bash
./test.sh
```
