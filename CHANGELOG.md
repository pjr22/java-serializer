# Changelog

All notable changes to this project are documented in this file.

## [Unreleased]

### Fixed
- **Critical**: Fixed `$ref` references in map values not being resolved - map values that reference other objects via `$ref` were returned as raw `LinkedHashMap` instead of the referenced object
- **Critical**: Fixed silent exception swallowing in `convertToMap` that caused nested objects in maps to be returned as raw `LinkedHashMap` instead of being properly deserialized
- Removed duplicate AtomicReference handling code in Deserializer
- Fixed null map key handling (null keys now deserialize correctly)
- Added proper atomic type reconstruction for map values (AtomicBoolean, AtomicInteger, AtomicLong, AtomicReference)
- Simplified constructor fallback logic by removing brittle enum-guessing code
- Consolidated BigDecimal conversion logic into a single helper method
- Removed unnecessary quote-stripping hack for $value deserialization

### Added
- New test class `AtomicMapValueTest` for atomic types in map values
- New test class `MapValueObjectDeserializationTest` for complex object values in maps (covers Map<UUID, Item> and polymorphic scenarios)
- Type validation in `convertToMap` to detect and report type mismatches early with descriptive error messages

## [1.0.0] - 2026-01-01

### Features
- Zero-dependency JSON serialization for Java objects
- Support for complex object graphs with circular references
- Full support for primitive types, collections, maps, and arrays
- Immutable object support via constructor injection
- SerialVersionUID validation for version compatibility

### Fixed
- Nested Map Type Deserialization: Inner maps with typed keys are now correctly deserialized
- JDK Map Serialization: Maps are serialized as plain JSON objects without metadata
- Map Key Type Deserialization: Complex key types (UUID, Date, etc.) are properly converted
- Constructor Parameter Value Deserialization: Non-null parameters are properly passed
- Map Values Containing Nested Objects: Proper serialization without toString() conversion
- Circular Reference Constructor Handling: Placeholder-based resolution for circular references
