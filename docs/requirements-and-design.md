# Java Serializer/Deserializer - Requirements and Design

## Project Overview

A Java library for serializing and deserializing arbitrary Java objects using reflection, without any external dependencies.

## Technology Stack

- **Language**: Java 17
- **Dependencies**: None (no external libraries)
- **Build Tool**: plain Java

## Core Requirements

### Serialization

The serializer will use reflection to inspect arbitrary Java objects and serialize them into a human-readable, structured format including:

1. **Fully qualified class name** - The complete package and class name
2. **serialVersionUID** - If specified in the class (via `private static final long serialVersionUID`)
3. **Unique object ID** - A unique identifier that can be used as a reference by other objects
4. **Field mappings**:
 - For primitive types, String, Number types (including BigDecimal), and Atomic wrapped boolean/number types:
   - Map of field name to field value (using JSON native representations)
 - For all other Object field types (excluding Strings, Numbers, BigDecimals, and Atomic wrapped booleans/numbers):
   - Map of field name to unique object ID (reference)

### Deserialization

The deserializer will use reflection to create new objects from serialized data, employing the following methods in order:

1. **Constructor with parameters** - Use a constructor with parameters for as many fields as possible
2. **Default constructor and setters** - Use a default constructor followed by setter methods
3. **Java reflection mechanisms** - Use reflection to construct and set object fields directly

### Object Registry

An object registry will be used to store objects during deserialization until the process is complete. This is necessary because some objects may depend on other objects to be constructed first (dependency ordering).

## Design Components

### 1. Output Format

The output format will be JSON.

### 2. Object ID Generation

Strategy for generating unique object IDs that:
- Are unique across the serialization session
- Can be used as references in the serialized format
- Are deterministic and reproducible for the same object graph

The serializer/deserializer will use a user-supplied serialization key, followed by a sequential integer counter (with user-defined starting ID), and object class name. For example:

- Serialization key: REV-A
- Counter beginning at 1001
- Serializing Objects of type org.fruit.Apple, org.fruit.Orange, org.fruit.Lemon

Resulting IDs:

- `REV-A_1001_org.fruit.Apple`
- `REV-A_1002_org.fruit.Orange`
- `REV-A_1003_org.fruit.Lemon`

**Note:** This approach ensures deterministic and reproducible object IDs, which is required for reproducible deserialization order.

### 3. Field Classification Logic

Logic to classify fields into categories using JSON native representations:

| Category | Types | Serialization Strategy |
|----------|-------|------------------------|
| Primitives | byte, short, int, long, float, double, char, boolean | JSON default representation |
| String | java.lang.String | JSON string |
| Numbers | java.lang.Number and subclasses (except BigDecimal) | JSON number |
| BigDecimal | java.math.BigDecimal | JSON number |
| Atomic Boolean | java.util.concurrent.atomic.AtomicBoolean | JSON boolean |
| Atomic Numbers | java.util.concurrent.atomic.AtomicInteger, AtomicLong | JSON number |
| Object References | All other Object types | Object ID reference |

### 4. Constructor Selection Algorithm

Algorithm for selecting the best constructor when multiple constructors exist:

**Requirements:**
- Prefer constructors with more parameters
- Match parameter names to field names
- Handle type conversions (e.g., int to long)

**Selection Decisions:**
- When multiple constructors with the same number of parameters exist, use the constructor with the most matching field names to parameter names. If there are still multiple options, use the first available.
- Field names must match constructor parameter names. Type conversion, when necessary, will be best effort.

### 5. Deserialization Order

Algorithm for determining the order in which objects should be deserialized:

**Approach:**
- Build a dependency graph of objects
- Perform topological sort to determine construction order
- Objects with no dependencies will be deserialized first
- Sort objects by object ID to ensure reproducible deserialization order
- Handle circular references using multi-pass approach with placeholder objects

**Circular Reference Handling:**
- Use empty proxy placeholders where possible (i.e., when default constructors are available)
- Use null placeholders when an empty proxy is not possible or practical
- After all objects are constructed, perform a second pass to replace placeholders with actual object references

## Critical Design Decisions

1. **Output Format**
   - JSON

2. **Circular References**
    - When detectected, circular references will employ a multipass approach, where a placeholder object (or null) will be used for one reference until the circular-dependent object is constructed, then the placeholder will be replaced with the newly constructed object.
    - Circular dependencies must be maintained, that is, objects that hold references to each other, or other circular dependencies, must be able to be reconstructed from the serialized format.
3. **Inheritance**
    - Fields from parent classes will be included in serialization

4. **Static Fields**
    - Exclude static fields (like Java serialization)

5. **Transient Fields**
    - Exclude transient fields (like Java serialization)

6. **Final Fields**
   - How to handle final fields during deserialization:
     - Use constructor injection (preferred)
     - Use setter if available
     - If constructor/setter fails, use reflection to bypass the final modifier using `setAccessible(true)` and `Field.set()`
     - Skip if not settable and log warning

7. **Arrays and Collections**
   - How to handle arrays, List, Set, Map:
     - Use Inline collection contents in JSON default format.

8. **Enum Types**
    - Serialize by name (enum constant name)

9. **Generic Types**
    - Ignore generics and use runtime types

10. **Access Modifiers**
    - How to handle private/protected fields/methods:
      - Use setAccessible(true) for all

11. **Constructor Selection**
    - When multiple constructors match, how to choose:
      - Prefer most matching parameters names to field names
      - Choose the first in a tie

12. **Immutable Objects**
    - How to handle objects without setters:
      - Must use constructor injection
      - Fail if no suitable constructor

13. **Null Values**
    - How to represent null in serialized format:
      - Use explicit "null" marker

14. **Custom Serialization**
    - Ignore custom serialization methods readObject/writeObject

15. **Versioning**
    - How to handle serialVersionUID mismatches:
      - Log warning and attempt best-effort deserialization

16. **Thread Safety**
    - The serializer/deserializer will be stateless and thread-safe:
      - A separate instance of serializer/deserializer will be constructed for each use and will be used in a single thread, for example:
       - serialization:
       ```
       ObjectSerializer serializer = new ObjectSerializer();
       serializer.serialize(myComplexJavaObject, outputStream);
       ```
       - deserialization:
       ```
       ObjectDeserializer deserializer = new ObjectDeserializer(ComplexJavaObject.class);
       ComplexJavaObject myComplexJavaObject = deserializer.deserialize(inputStream);
       ```
       
   **API Design:**
   - Output destination for `serialize()`: `OutputStream`
   - `serialize()` method returns `void`, throws `SerializationException` on error
   - No overloaded methods for different output types at this time

17. **Error Handling**
    - What should happen on deserialization failure:
      - Partial deserialization while collecting all errors and report at end

18. **Performance**
    - There will be no memory or timing constraints, but wrap entire operation in a try block and catch any Throwable, if one occurs, logging the details.

## Package Structure (Proposed)

```
com.pjr22.serializer
├── core
│   ├── Serializer.java
│   ├── Deserializer.java
│   └── SerializationException.java
├── model
│   ├── SerializedObject.java
│   ├── SerializedField.java
│   └── ObjectReference.java
├── inspector
│   ├── FieldInspector.java
│   ├── FieldClassifier.java
│   └── ConstructorAnalyzer.java
├── registry
│   ├── ObjectRegistry.java
│   └── ObjectIdGenerator.java
├── util
│   ├── ReflectionUtils.java
│   └── TypeUtils.java
└── format
    ├── JsonSerializer.java
    └── JsonParser.java
```

## Example Serialized Format (JSON - Proposed)

**Note:** The following was the originally proposed format. The actual implementation uses a nested format (shown below) for better readability and simplicity. The Deserializer supports both formats for backward compatibility.

### Proposed Format

```json
{
  "objects": [
    {
      "id": "REV-A_1001_com.example.Person",
      "className": "com.example.Person",
      "serialVersionUID": "123456789L",
      "fields": {
        "name": "John Doe",
        "age": 30,
        "salary": 75000.50,
        "active": true
      },
      "references": {
        "address": "REV-A_1002_com.example.Address",
        "department": "REV-A_1003_com.example.Department"
      }
    },
    {
      "id": "REV-A_1002_com.example.Address",
      "className": "com.example.Address",
      "fields": {
        "street": "123 Main St",
        "city": "Springfield",
        "zipCode": "12345"
      },
      "references": {}
    },
    {
      "id": "REV-A_1003_com.example.Department",
      "className": "com.example.Department",
      "fields": {
        "name": "Engineering",
        "budget": 500000.00
      },
      "references": {
        "manager": "REV-A_1001_com.example.Person"
      }
    }
  ]
}
```

### Actual Implementation Format

The actual implementation uses a nested format with inline object references:

```json
{
  "$id": "REV-A_1001_com.example.Person",
  "$class": "com.example.Person",
  "serialVersionUID": 123456789,
  "fields": {
    "name": "John Doe",
    "age": 30,
    "salary": 75000.50,
    "active": true,
    "address": {
      "$id": "REV-A_1002_com.example.Address",
      "$class": "com.example.Address",
      "fields": {
        "street": "123 Main St",
        "city": "Springfield",
        "zipCode": "12345"
      }
    },
    "department": {
      "$id": "REV-A_1003_com.example.Department",
      "$class": "com.example.Department",
      "fields": {
        "name": "Engineering",
        "budget": 500000.00,
        "manager": {
          "$ref": "REV-A_1001_com.example.Person"
        }
      }
    }
  }
}
```

**Key differences from proposed format:**
- Objects are nested inline rather than in a separate `objects` array
- Object references use `$ref` field pointing to `$id` of previously serialized objects
- Circular references are handled using `$ref` to point back to parent objects

## Notes

- This document will be updated as decisions are made
- The output format is JSON with native type representations
- Object IDs use the format: `{serializationKey}_{counter}_{className}`
