# API Documentation

This document provides detailed API documentation for all public classes in the Java Serializer/Deserializer library.

## Table of Contents

- [Core Classes](#core-classes)
  - [Serializer](#serializer)
  - [Deserializer](#deserializer)
  - [SerializationException](#serializationexception)
- [Inspector Classes](#inspector-classes)
  - [FieldInspector](#fieldinspector)
  - [FieldClassifier](#fieldclassifier)
  - [ConstructorAnalyzer](#constructoranalyzer)
- [Registry Classes](#registry-classes)
  - [ObjectIdGenerator](#objectidgenerator)
  - [ObjectRegistry](#objectregistry)
- [Format Classes](#format-classes)
  - [JsonSerializer](#jsonserializer)
  - [JsonParser](#jsonparser)
- [Utility Classes](#utility-classes)
  - [ValueSerializer](#valueserializer)

---

## Core Classes

### Serializer

**Package:** `com.pjr22.serialization.core`

Serializes Java objects to JSON format. Handles object references, circular references, and complex object graphs.

#### Constructors

##### `Serializer(String serializationKey, int startingId)`

Creates a new Serializer with the specified serialization key and starting ID.

**Parameters:**
- `serializationKey` - The prefix to use for all generated object IDs
- `startingId` - The starting value for the object ID counter

**Example:**
```java
Serializer serializer = new Serializer("myapp", 1);
```

#### Methods

##### `void serialize(Object object, OutputStream outputStream) throws SerializationException`

Serializes an object to JSON format and writes it to the output stream.

**Parameters:**
- `object` - The object to serialize
- `outputStream` - The output stream to write to

**Throws:**
- `SerializationException` - If a serialization error occurs

**Example:**
```java
SimplePerson person = new SimplePerson();
person.setName("John Doe");
person.setAge(30);

ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
serializer.serialize(person, outputStream);
String json = outputStream.toString();
```

##### `int getCounter()`

Returns the current object ID counter value.

**Returns:** The current counter value

**Example:**
```java
int counter = serializer.getCounter();
```

##### `String getSerializationKey()`

Returns the serialization key used by this serializer.

**Returns:** The serialization key

**Example:**
```java
String key = serializer.getSerializationKey();
```

#### Serialization Output Format

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

**Fields:**
- `$id` - Unique object identifier in format `{serializationKey}_{counter}_{className}`
- `$class` - Fully qualified class name
- `serialVersionUID` - (Optional) The serialVersionUID value if present in the class
- `fields` - Object containing all non-static, non-transient fields

---

### Deserializer<T>

**Package:** `com.pjr22.serialization.core`

Deserializes Java objects from JSON format. Handles object references, circular references, and complex object graphs.

#### Type Parameters

- `T` - The type of object to deserialize

#### Constructors

##### `Deserializer(Class<T> targetType)`

Creates a new Deserializer for the specified target type.

**Parameters:**
- `targetType` - The class to deserialize to

**Example:**
```java
Deserializer<SimplePerson> deserializer = new Deserializer<>(SimplePerson.class);
```

#### Methods

##### `T deserialize(InputStream inputStream) throws SerializationException`

Deserializes an object from the input stream.

**Parameters:**
- `inputStream` - The input stream to read from

**Returns:** The deserialized object

**Throws:**
- `SerializationException` - If a deserialization error occurs

**Example:**
```java
String json = "{\"$id\":\"app_1_com.example.SimplePerson\",\"$class\":\"com.example.SimplePerson\",\"fields\":{\"name\":\"John Doe\",\"age\":30}}";
ByteArrayInputStream inputStream = new ByteArrayInputStream(json.getBytes());
SimplePerson person = deserializer.deserialize(inputStream);
```

##### `List<String> getWarnings()`

Returns any warnings generated during deserialization.

**Returns:** List of warning messages

**Example:**
```java
List<String> warnings = deserializer.getWarnings();
for (String warning : warnings) {
    System.out.println("Warning: " + warning);
}
```

#### Supported JSON Formats

The deserializer supports two JSON formats:

1. **Nested Format** (from Serializer)
   ```json
   {
     "$id": "app_1_com.example.SimplePerson",
     "$class": "com.example.SimplePerson",
     "fields": { ... }
   }
   ```

2. **Objects Array Format** (for backward compatibility)
   ```json
   {
     "objects": [
       {
         "id": "obj1",
         "className": "com.example.SimplePerson",
         "fields": { ... },
         "references": { ... }
       }
     ]
   }
   ```

#### Map Key Type Conversion

The deserializer automatically converts JSON string keys to the appropriate types based on the map's generic type parameters. This enables proper deserialization of maps with non-String keys.

**Supported Key Types:**
- Primitive types: `long`, `int`, `short`, `byte`, `double`, `float`, `boolean`, `char`
- Wrapper types: `Long`, `Integer`, `Short`, `Byte`, `Double`, `Float`, `Boolean`, `Character`
- `String` (no conversion needed)
- `Enum` types (converted via `name()` and reflection)
- JDK complex types: `UUID`, `Date`, and other types with `fromString(String)` method or single-parameter constructor

**Example:**
```java
// Class with Map<Long, String> field
public class PersonWithLongKeyMap {
    private final Map<Long, String> longKeyMap;
    
    public PersonWithLongKeyMap(Map<Long, String> longKeyMap) {
        this.longKeyMap = new LinkedHashMap<>(longKeyMap);
    }
    
    public Map<Long, String> getLongKeyMap() {
        return longKeyMap;
    }
}

// Serialization
Map<Long, String> originalMap = new LinkedHashMap<>();
originalMap.put(1001L, "Room 1001");
originalMap.put(1002L, "Room 1002");
PersonWithLongKeyMap original = new PersonWithLongKeyMap(originalMap);

Serializer serializer = new Serializer("test", 0);
ByteArrayOutputStream out = new ByteArrayOutputStream();
serializer.serialize(original, out);

// Deserialization - keys are automatically converted to Long type
Deserializer<PersonWithLongKeyMap> deserializer = new Deserializer<>(PersonWithLongKeyMap.class);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
PersonWithLongKeyMap deserialized = deserializer.deserialize(in);

// Keys are Long type, not String
for (Map.Entry<Long, String> entry : deserialized.getLongKeyMap().entrySet()) {
    System.out.println(entry.getKey() instanceof Long); // true
}
```

**Note:** The deserializer uses reflection to extract generic type information from the field's `ParameterizedType`. If the type cannot be determined, keys will remain as strings.

#### Parameterized Type Deserialization

The deserializer supports proper deserialization of parameterized types, particularly `AtomicReference<T>`. When deserializing fields with parameterized types, the deserializer:

1. Extracts the generic type parameter from the field's `ParameterizedType` using reflection
2. Converts the JSON value to the appropriate target type before creating the container object

This ensures that `AtomicReference<CombatStance>` correctly deserializes a string value like `"BALANCED"` to the `CombatStance` enum, rather than creating an `AtomicReference<String>`.

**Supported Parameterized Types:**
- `AtomicReference<T>` - The referenced value is converted to the type parameter `T`

**Example:**
```java
// Class with AtomicReference<CombatStance> field
public class PlayerCharacter {
    private AtomicReference<CombatStance> combatStance =
        new AtomicReference<>(CombatStance.BALANCED);
}

// Serialization - enum value is serialized as string
PlayerCharacter original = new PlayerCharacter();
Serializer serializer = new Serializer("game", 0);
ByteArrayOutputStream out = new ByteArrayOutputStream();
serializer.serialize(original, out);
// JSON contains: "combatStance": "BALANCED"

// Deserialization - string is converted to CombatStance enum
Deserializer<PlayerCharacter> deserializer = new Deserializer<>(PlayerCharacter.class);
ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
PlayerCharacter deserialized = deserializer.deserialize(in);

// combatStance.get() returns CombatStance enum, not String
CombatStance stance = deserialized.getCombatStance().get();
System.out.println(stance instanceof CombatStance); // true
System.out.println(stance == CombatStance.BALANCED); // true
```

**Note:** This feature also handles null values correctly - when a field value is null, the deserializer creates an `AtomicReference` with a null value rather than setting the field to null.

---

### SerializationException

**Package:** `com.pjr22.serialization.core`

Exception thrown when a serialization or deserialization error occurs.

#### Constructors

##### `SerializationException(String message)`

Constructs a new SerializationException with the specified detail message.

**Parameters:**
- `message` - The detail message

**Example:**
```java
throw new SerializationException("Invalid object format");
```

##### `SerializationException(String message, Throwable cause)`

Constructs a new SerializationException with the specified detail message and cause.

**Parameters:**
- `message` - The detail message
- `cause` - The cause

**Example:**
```java
try {
    // serialization code
} catch (IOException e) {
    throw new SerializationException("Error writing to output stream", e);
}
```

---

## Inspector Classes

### FieldInspector

**Package:** `com.pjr22.serialization.inspector`

Inspects Java classes to extract fields for serialization. Uses reflection to get all fields from a class and its superclasses, excluding static and transient fields.

#### Methods

##### `static Field[] getAllFields(Class<?> clazz)`

Gets all non-static, non-transient fields from a class and its superclasses. Private fields are made accessible.

**Parameters:**
- `clazz` - The class to inspect

**Returns:** Array of fields

**Example:**
```java
Field[] fields = FieldInspector.getAllFields(SimplePerson.class);
for (Field field : fields) {
    System.out.println(field.getName());
}
```

**Behavior:**
- Walks up the class hierarchy to get all inherited fields
- Excludes static fields
- Excludes transient fields
- Makes private fields accessible using `setAccessible(true)`
- Skips JDK and system classes (java.*, javax.*, sun.*) to avoid module system access restrictions in Java 9+

---

### FieldClassifier

**Package:** `com.pjr22.serialization.inspector`

Classifies Java fields into categories for serialization purposes.

#### Nested Class

##### `FieldCategory`

Represents the category of a field for serialization.

**Values:**
- `PRIMITIVE` - byte, short, int, long, float, double, char, boolean
- `STRING` - java.lang.String
- `NUMBER` - java.lang.Number and subclasses except BigDecimal
- `BIG_DECIMAL` - java.math.BigDecimal
- `ATOMIC_BOOLEAN` - java.util.concurrent.atomic.AtomicBoolean
- `ATOMIC_INTEGER` - java.util.concurrent.atomic.AtomicInteger
- `ATOMIC_LONG` - java.util.concurrent.atomic.AtomicLong
- `ATOMIC_REFERENCE` - java.util.concurrent.atomic.AtomicReference<T> (supports parameterized types)
- `COLLECTION` - List, Set, etc.
- `MAP` - Map implementations
- `ARRAY` - Arrays
- `ENUM` - Enum types
- `OBJECT_REFERENCE` - All other Object types

#### Methods

##### `static FieldCategory classify(Field field)`

Classifies a field into a category based on its type.

**Parameters:**
- `field` - The field to classify

**Returns:** The field category

**Example:**
```java
Field field = SimplePerson.class.getDeclaredField("name");
FieldClassifier.FieldCategory category = FieldClassifier.classify(field);
// Returns FieldCategory.STRING
```

**Classification Order:**
1. String
2. BigDecimal (checked before Number since BigDecimal extends Number)
3. AtomicBoolean, AtomicInteger, AtomicLong (checked before Number)
4. Primitive types (double, float classified as NUMBER; others as PRIMITIVE)
5. Number types (Integer, Long, Double, Float, etc.)
6. Collection types
7. Map types
8. Array types
9. Enum types
10. Default to OBJECT_REFERENCE

---

### ConstructorAnalyzer

**Package:** `com.pjr22.serialization.inspector`

Analyzes and selects the best constructor for deserialization.

#### Methods

##### `static Constructor<?> selectBestConstructor(Class<?> clazz, Set<String> fieldNames)`

Selects the best constructor for deserialization based on field names.

**Parameters:**
- `clazz` - The class to analyze
- `fieldNames` - The set of field names to match against constructor parameters

**Returns:** The best constructor, or null if no suitable constructor is found

**Example:**
```java
Set<String> fieldNames = new HashSet<>(Arrays.asList("name", "age", "salary"));
Constructor<?> constructor = ConstructorAnalyzer.selectBestConstructor(SimplePerson.class, fieldNames);
```

**Selection Algorithm:**
1. If no field names provided, prefer default constructor
2. Find constructor with most matching parameter names (considers all visibility levels)
3. If multiple constructors have same match count, prefer one with more parameters
4. Choose first available in case of tie

**Note:** This method considers all constructors regardless of visibility (public, protected, package-private, private). The deserializer will make non-public constructors accessible via reflection.

##### `static int countMatchingParameterNames(Constructor<?> constructor, Set<String> fieldNames)`

Counts how many constructor parameter names match the given field names.

**Parameters:**
- `constructor` - The constructor to analyze
- `fieldNames` - The set of field names to match against

**Returns:** The count of matching parameter names

**Example:**
```java
Constructor<?> constructor = SimplePerson.class.getConstructor(String.class, int.class);
Set<String> fieldNames = new HashSet<>(Arrays.asList("name", "age"));
int matchCount = ConstructorAnalyzer.countMatchingParameterNames(constructor, fieldNames);
```

**Note:** Requires `-parameters` compiler flag to preserve parameter names at runtime.

##### `static boolean isTypeCompatible(Class<?> paramType, Class<?> fieldType)`

Checks if a constructor parameter type is compatible with a field type.

**Parameters:**
- `paramType` - The constructor parameter type
- `fieldType` - The field type

**Returns:** true if the types are compatible, false otherwise

**Example:**
```java
boolean compatible = ConstructorAnalyzer.isTypeCompatible(long.class, int.class);
// Returns true (int can be widened to long)
```

**Supported Conversions:**
- Primitive to primitive widening (e.g., int to long)
- Boxing/unboxing conversions
- Number type compatibility (e.g., Integer to Long)

---

## Registry Classes

### ObjectIdGenerator

**Package:** `com.pjr22.serialization.registry`

Generates unique object IDs for serialization. IDs are generated in the format: `{serializationKey}_{counter}_{className}`. Each generator instance maintains its own independent counter.

#### Constructors

##### `ObjectIdGenerator(String serializationKey, int startingId)`

Creates a new ObjectIdGenerator with the specified serialization key and starting ID.

**Parameters:**
- `serializationKey` - The prefix to use for all generated IDs
- `startingId` - The starting value for the counter

**Example:**
```java
ObjectIdGenerator generator = new ObjectIdGenerator("myapp", 1);
```

#### Methods

##### `String generateId(String className)`

Generates a unique object ID for the given class name. The counter is incremented after each call.

**Parameters:**
- `className` - The fully qualified class name

**Returns:** The generated object ID

**Example:**
```java
String id1 = generator.generateId("com.example.Person"); // "myapp_1_com.example.Person"
String id2 = generator.generateId("com.example.Person"); // "myapp_2_com.example.Person"
```

##### `int getCounter()`

Returns the current counter value.

**Returns:** The current counter value

**Example:**
```java
int counter = generator.getCounter();
```

##### `String getSerializationKey()`

Returns the serialization key used by this generator.

**Returns:** The serialization key

**Example:**
```java
String key = generator.getSerializationKey();
```

---

### ObjectRegistry

**Package:** `com.pjr22.serialization.registry`

Registry for storing objects during deserialization. Objects are stored by their unique object ID and can be retrieved later. This is necessary for handling object references and circular dependencies.

#### Constructors

##### `ObjectRegistry()`

Creates a new empty ObjectRegistry.

**Example:**
```java
ObjectRegistry registry = new ObjectRegistry();
```

#### Methods

##### `void register(String objectId, Object object)`

Registers an object with the given object ID. If an object with the same ID already exists, it will be overwritten.

**Parameters:**
- `objectId` - The unique object ID
- `object` - The object to register (can be null)

**Example:**
```java
SimplePerson person = new SimplePerson();
person.setName("John Doe");
registry.register("obj1", person);
```

##### `Object get(String objectId)`

Retrieves an object by its ID.

**Parameters:**
- `objectId` - The unique object ID

**Returns:** The registered object, or null if not found or if the registered object is null

**Example:**
```java
SimplePerson person = (SimplePerson) registry.get("obj1");
```

##### `boolean contains(String objectId)`

Checks if an object with the given ID is registered.

**Parameters:**
- `objectId` - The unique object ID

**Returns:** true if an object with the given ID is registered, false otherwise

**Example:**
```java
if (registry.contains("obj1")) {
    System.out.println("Object exists");
}
```

##### `void clear()`

Clears all registered objects from the registry.

**Example:**
```java
registry.clear();
```

##### `Set<String> getAllObjectIds()`

Returns all registered object IDs.

**Returns:** A set of all registered object IDs

**Example:**
```java
Set<String> ids = registry.getAllObjectIds();
for (String id : ids) {
    System.out.println(id);
}
```

##### `int size()`

Returns the number of registered objects.

**Returns:** The size of the registry

**Example:**
```java
int count = registry.size();
```

---

## Format Classes

### JsonSerializer

**Package:** `com.pjr22.serialization.format`

Serializes Java objects to JSON format. Handles primitives, strings, numbers, arrays, collections, maps, and null values.

#### Methods

##### `static String serialize(Object value)`

Serializes an object to its JSON string representation.

**Parameters:**
- `value` - The object to serialize

**Returns:** The JSON string representation

**Example:**
```java
String json = JsonSerializer.serialize("Hello World"); // "\"Hello World\""
String json = JsonSerializer.serialize(42); // "42"
String json = JsonSerializer.serialize(true); // "true"
String json = JsonSerializer.serialize(null); // "null"
```

**Supported Types:**
- Primitive types (boolean, byte, short, int, long, float, double, char)
- Primitive wrappers (Boolean, Byte, Short, Integer, Long, Float, Double, Character)
- String
- Number types (including BigDecimal)
- Atomic types (AtomicBoolean, AtomicInteger, AtomicLong)
- Arrays
- Collections (List, Set, etc.)
- Maps
- Enums
- null

**Special Character Escaping:**
The serializer properly escapes special characters in strings:
- `"` → `\"`
- `\` → `\\`
- `\b` → `\b`
- `\f` → `\f`
- `\n` → `\n`
- `\r` → `\r`
- `\t` → `\t`
- Other control characters → `\uXXXX`

---

### JsonParser

**Package:** `com.pjr22.serialization.format`

Parses JSON strings into Java objects. Handles null, boolean, number, string, array, and object types.

#### Methods

##### `static Object parse(String json)`

Parses a JSON string and returns the corresponding Java object.

**Parameters:**
- `json` - The JSON string to parse

**Returns:** The parsed Java object

**Throws:**
- `IllegalArgumentException` - If the JSON string is null, empty, or invalid

**Example:**
```java
Object result = JsonParser.parse("null"); // null
Object result = JsonParser.parse("true"); // true
Object result = JsonParser.parse("42"); // 42 (Integer)
Object result = JsonParser.parse("3.14"); // 3.14 (Double)
Object result = JsonParser.parse("\"Hello\""); // "Hello"
Object result = JsonParser.parse("[1, 2, 3]"); // List
Object result = JsonParser.parse("{\"key\":\"value\"}"); // LinkedHashMap
```

**Return Types:**
- `null` → `null`
- `true`/`false` → `Boolean`
- Integer numbers → `Integer` or `Long`
- Floating point numbers → `Double`
- Strings → `String`
- Arrays → `List<Object>`
- Objects → `LinkedHashMap<String, Object>`

**String Escape Sequences:**
The parser supports standard JSON escape sequences:
- `\"` → `"`
- `\\` → `\`
- `\/` → `/`
- `\b` → Backspace
- `\f` → Form feed
- `\n` → Newline
- `\r` → Carriage return
- `\t` → Tab
- `\uXXXX` → Unicode character

**Number Parsing:**
- Supports optional minus sign
- Supports integer part (no leading zeros except for zero itself)
- Supports fractional part
- Supports exponent part (e or E, with optional + or -)
- Returns `Integer` for values within Integer range
- Returns `Long` for integers outside Integer range
- Returns `Double` for floating point numbers

---

## Utility Classes

### ValueSerializer

**Package:** `com.pjr22.serialization.util`

Utility class for serializing and deserializing JDK classes that can be constructed with a single value (String or Number). This provides a generic mechanism to handle classes like UUID, Date, Random, etc. without requiring special cases for each type.

#### Methods

##### `static boolean canSerializeAsValue(Class<?> clazz)`

Checks if a class can be serialized as a simple value. A class is considered serializable as a simple value if it:
1. Is a JDK class (java.* package)
2. Has a `toString()` method that produces a reconstructible value
3. Has either a `fromString(String)` method or a single-parameter constructor that can reliably construct a new instance from the value

**Parameters:**
- `clazz` - The class to check

**Returns:** true if the class can be serialized as a simple value, false otherwise

**Example:**
```java
boolean canSerialize = ValueSerializer.canSerializeAsValue(UUID.class); // true
boolean canSerialize = ValueSerializer.canSerializeAsValue(String.class); // false
```

##### `static Object serializeAsValue(Object obj)`

Serializes an object to a simple value (String or Number). Returns null if the object cannot be serialized as a simple value.

**Parameters:**
- `obj` - The object to serialize

**Returns:** The serialized value (String, Number, or null)

**Example:**
```java
UUID uuid = UUID.randomUUID();
Object value = ValueSerializer.serializeAsValue(uuid); // String representation

Date date = new Date();
Object dateValue = ValueSerializer.serializeAsValue(date); // ISO 8601 string

Random random = new Random();
Object randomValue = ValueSerializer.serializeAsValue(random); // nextLong() value
```

**Supported Types:**
- `UUID` - Serialized as string representation
- `Date` - Serialized as ISO 8601 string format (`yyyy-MM-dd'T'HH:mm:ss.SSSZ`)
- `Random` - Serialized as `nextLong()` value
- Other JDK classes with `fromString(String)` method or single-parameter constructor

##### `static <T> T deserializeFromValue(Object value, Class<T> targetClass)`

Deserializes a simple value to an object of the specified class. Returns null if deserialization is not possible.

**Parameters:**
- `value` - The value to deserialize (String, Number, or null)
- `targetClass` - The target class to deserialize to

**Returns:** The deserialized object, or null if not possible

**Example:**
```java
// UUID deserialization
String uuidString = "550e8400-e29b-41d4-a716-446655440000";
UUID uuid = ValueSerializer.deserializeFromValue(uuidString, UUID.class);

// Date deserialization
String dateString = "2025-12-29T12:30:45.123-0700";
Date date = ValueSerializer.deserializeFromValue(dateString, Date.class);

// Random deserialization
long seed = 123456789L;
Random random = ValueSerializer.deserializeFromValue(seed, Random.class);
```

**Deserialization Strategy:**
1. Try `fromString(String)` static method first
2. Try single-parameter constructor with String parameter
3. Try single-parameter constructor with Number parameter (long, int, etc.)
4. Return null if none of the above work

---

## Type Conversion Reference

### Primitive Type Widening

| From | To |
|------|-----|
| byte | short, int, long, float, double |
| short | int, long, float, double |
| char | int, long, float, double |
| int | long, float, double |
| long | float, double |
| float | double |

### Boxing/Unboxing

| Primitive | Wrapper |
|-----------|---------|
| boolean | Boolean |
| byte | Byte |
| short | Short |
| int | Integer |
| long | Long |
| float | Float |
| double | Double |
| char | Character |

### Number Type Compatibility

All Number types are considered compatible for type conversion:
- Integer ↔ Long ↔ Double ↔ Float ↔ Short ↔ Byte
- BigDecimal ↔ Any Number type

## Class Requirements for Deserialization

### Constructor Requirements

For a class to be successfully deserialized, it must have a suitable constructor. The deserializer follows this priority order:

1. **Default (no-arg) constructor** - Preferred as it's the safest option
2. **Constructor with matching parameter names** - Parameters that match field names in the serialized data will receive the actual values
3. **Constructor with primitive/wrapper parameters** - If no matching fields, the constructor must only use primitive types or their wrappers (which have safe default values like 0, false, null)

**Example of a deserializable class:**
```java
public class Person {
    private String name;
    private int age;
    
    // Default constructor - preferred
    public Person() {
    }
    
    // OR: Constructor with matching parameters
    public Person(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
```

**Example of a non-deserializable class:**
```java
public class Config {
    private Map<String, String> settings;
    
    // This constructor requires a non-null args parameter
    // and the parameter name doesn't match any field
    public Config(String[] args) {
        // This will fail deserialization because:
        // 1. No default constructor
        // 2. String[] parameter cannot be safely initialized with a default value
        // 3. Parameter name "args" doesn't match any field
        if (args != null) {
            // ...
        }
    }
}
```

### Final Fields Limitation

**Classes with `final` fields that are initialized inline cannot be deserialized.** This is a Java language limitation - final fields cannot be modified after object construction, even with reflection in modern Java versions.

**Problematic pattern:**
```java
public class Config {
    // These final fields are initialized inline
    public final Map<String, String> client = new TreeMap<>();
    public final Map<String, Integer> game = new TreeMap<>();
    
    public Config() {
        setDefaults();
    }
}
```

**Solution 1: Make fields non-final**
```java
public class Config {
    // Remove final modifier
    public Map<String, String> client = new TreeMap<>();
    public Map<String, Integer> game = new TreeMap<>();
    
    public Config() {
        setDefaults();
    }
}
```

**Solution 2: Use constructor injection**
```java
public class Config {
    private final Map<String, String> client;
    private final Map<String, Integer> game;
    
    // Constructor that accepts the map values
    public Config(Map<String, String> client, Map<String, Integer> game) {
        this.client = client != null ? client : new TreeMap<>();
        this.game = game != null ? game : new TreeMap<>();
    }
}
```

### Parameter Name Preservation

For constructor parameter matching to work correctly, compile your code with the `-parameters` flag:

```bash
javac -parameters YourClass.java
```

Without this flag, parameter names are not preserved in the bytecode, and the deserializer cannot match them to field names.

### Summary of Requirements

For a class to be deserializable:

1. ✅ Have a default (no-arg) constructor, OR
2. ✅ Have a constructor with parameters that match field names, OR
3. ✅ Have a constructor with only primitive/wrapper parameters (which can use default values)

4. ❌ Avoid `final` fields initialized inline, OR
5. ✅ Provide a constructor that accepts all final field values

6. ✅ Compile with `-parameters` flag for constructor parameter matching

---

## Error Handling

### SerializationException

All serialization and deserialization errors are reported through [`SerializationException`](#serializationexception). Common causes include:

- Invalid object format
- Missing or inaccessible fields
- Type conversion errors
- Class not found during deserialization
- Constructor not found
- Circular reference resolution failure

### Warnings

The [`Deserializer`](#deserializer) may generate warnings during deserialization (e.g., serialVersionUID mismatches). These can be retrieved using the [`getWarnings()`](#liststring-getwarnings) method.

---

## Thread Safety

- [`Serializer`](#serializer) - Not thread-safe. Create a new instance for each thread or use external synchronization.
- [`Deserializer`](#deserializer) - Not thread-safe. Create a new instance for each thread or use external synchronization.
- [`ObjectIdGenerator`](#objectidgenerator) - Not thread-safe due to mutable counter.
- [`ObjectRegistry`](#objectregistry) - Not thread-safe. Use external synchronization if sharing across threads.
- [`JsonSerializer`](#jsonserializer) - Thread-safe (static methods only).
- [`JsonParser`](#jsonparser) - Not thread-safe (creates instance per parse).
- [`FieldInspector`](#fieldinspector) - Thread-safe (static methods only).
- [`FieldClassifier`](#fieldclassifier) - Thread-safe (static methods only).
- [`ConstructorAnalyzer`](#constructoranalyzer) - Thread-safe (static methods only).
#### Circular Reference Handling

The deserializer handles circular references in object graphs using a placeholder-based approach:

**How it works:**
1. When deserializing an object, a placeholder is registered in the ObjectRegistry before the object is constructed
2. If nested objects reference back to the parent object during construction, they receive a marker instead of the actual object
3. After the parent object is fully constructed, it replaces the placeholder in the registry
4. Unresolved references are tracked and resolved after the target object is available

**Example of circular reference:**
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

**Limitations:**
- Circular references in constructor parameters that use final fields are resolved after construction
- For non-final fields, the circular reference is properly set during the resolution phase
- For final fields in constructor parameters, the value is set during construction and cannot be changed afterward
