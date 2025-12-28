package com.pjr22.serialization.test;

import com.pjr22.serialization.format.JsonParser;
import com.pjr22.serialization.format.JsonSerializer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Test cases for JsonSerializer and JsonParser.
 */
public class JsonFormatTest extends TestCase {

    public void testSerializeNull() {
        String result = JsonSerializer.serialize(null);
        assertEquals("null", result, "Null should serialize to 'null'");
    }

    public void testSerializeBoolean() {
        assertEquals("true", JsonSerializer.serialize(true), "Boolean true should serialize to 'true'");
        assertEquals("false", JsonSerializer.serialize(false), "Boolean false should serialize to 'false'");
    }

    public void testSerializeInteger() {
        assertEquals("42", JsonSerializer.serialize(42), "Integer should serialize to number");
        assertEquals("-42", JsonSerializer.serialize(-42), "Negative integer should serialize to number");
        assertEquals("0", JsonSerializer.serialize(0), "Zero should serialize to '0'");
    }

    public void testSerializeLong() {
        assertEquals("1234567890123", JsonSerializer.serialize(1234567890123L), "Long should serialize to number");
    }

    public void testSerializeDouble() {
        assertEquals("3.14", JsonSerializer.serialize(3.14), "Double should serialize to number");
        assertEquals("-3.14", JsonSerializer.serialize(-3.14), "Negative double should serialize to number");
    }

    public void testSerializeFloat() {
        assertEquals("2.5", JsonSerializer.serialize(2.5f), "Float should serialize to number");
    }

    public void testSerializeCharacter() {
        assertEquals("\"a\"", JsonSerializer.serialize('a'), "Character should serialize to quoted string");
        assertEquals("\"\\n\"", JsonSerializer.serialize('\n'), "Newline character should be escaped");
    }

    public void testSerializeString() {
        assertEquals("\"hello\"", JsonSerializer.serialize("hello"), "String should serialize to quoted string");
        assertEquals("\"\"", JsonSerializer.serialize(""), "Empty string should serialize to empty quoted string");
        assertEquals("\"hello\\nworld\"", JsonSerializer.serialize("hello\nworld"), "String with newline should escape");
        assertEquals("\"hello\\\"world\"", JsonSerializer.serialize("hello\"world"), "String with quote should escape");
        assertEquals("\"hello\\\\world\"", JsonSerializer.serialize("hello\\world"), "String with backslash should escape");
    }

    public void testSerializeAtomicBoolean() {
        assertEquals("true", JsonSerializer.serialize(new AtomicBoolean(true)), "AtomicBoolean true should serialize to 'true'");
        assertEquals("false", JsonSerializer.serialize(new AtomicBoolean(false)), "AtomicBoolean false should serialize to 'false'");
    }

    public void testSerializeAtomicInteger() {
        assertEquals("42", JsonSerializer.serialize(new AtomicInteger(42)), "AtomicInteger should serialize to number");
    }

    public void testSerializeAtomicLong() {
        assertEquals("1234567890123", JsonSerializer.serialize(new AtomicLong(1234567890123L)), "AtomicLong should serialize to number");
    }

    public void testSerializeArray() {
        int[] intArray = {1, 2, 3};
        String result = JsonSerializer.serialize(intArray);
        assertEquals("[1,2,3]", result, "Array should serialize to JSON array");

        String[] stringArray = {"a", "b", "c"};
        result = JsonSerializer.serialize(stringArray);
        assertEquals("[\"a\",\"b\",\"c\"]", result, "String array should serialize to JSON array of strings");

        Object[] emptyArray = {};
        result = JsonSerializer.serialize(emptyArray);
        assertEquals("[]", result, "Empty array should serialize to empty JSON array");
    }

    public void testSerializeCollection() {
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        list.add("c");
        String result = JsonSerializer.serialize(list);
        assertEquals("[\"a\",\"b\",\"c\"]", result, "List should serialize to JSON array");

        List<Integer> intList = new ArrayList<>();
        intList.add(1);
        intList.add(2);
        result = JsonSerializer.serialize(intList);
        assertEquals("[1,2]", result, "Integer list should serialize to JSON array of numbers");

        List<Object> emptyList = new ArrayList<>();
        result = JsonSerializer.serialize(emptyList);
        assertEquals("[]", result, "Empty list should serialize to empty JSON array");
    }

    public void testSerializeMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("name", "John");
        map.put("age", 30);
        map.put("active", true);
        String result = JsonSerializer.serialize(map);
        // Order may vary, so check that it contains the expected elements
        assertTrue(result.contains("\"name\":\"John\""), "Map should contain name key-value pair");
        assertTrue(result.contains("\"age\":30"), "Map should contain age key-value pair");
        assertTrue(result.contains("\"active\":true"), "Map should contain active key-value pair");
        assertTrue(result.startsWith("{"), "Map should start with '{'");
        assertTrue(result.endsWith("}"), "Map should end with '}'");

        Map<String, Object> emptyMap = new HashMap<>();
        result = JsonSerializer.serialize(emptyMap);
        assertEquals("{}", result, "Empty map should serialize to empty JSON object");
    }

    public void testParseNull() {
        Object result = JsonParser.parse("null");
        assertEquals(null, result, "Parsing 'null' should return null");
    }

    public void testParseBoolean() {
        assertEquals(true, JsonParser.parse("true"), "Parsing 'true' should return true");
        assertEquals(false, JsonParser.parse("false"), "Parsing 'false' should return false");
    }

    public void testParseInteger() {
        assertEquals(42, JsonParser.parse("42"), "Parsing '42' should return 42");
        assertEquals(-42, JsonParser.parse("-42"), "Parsing '-42' should return -42");
        assertEquals(0, JsonParser.parse("0"), "Parsing '0' should return 0");
    }

    public void testParseLong() {
        assertEquals(1234567890123L, JsonParser.parse("1234567890123"), "Parsing long should return long value");
    }

    public void testParseDouble() {
        assertEquals(3.14, JsonParser.parse("3.14"), "Parsing '3.14' should return 3.14");
        assertEquals(-3.14, JsonParser.parse("-3.14"), "Parsing '-3.14' should return -3.14");
        assertEquals(1.5e10, JsonParser.parse("1.5e10"), "Parsing '1.5e10' should return 1.5e10");
    }

    public void testParseString() {
        assertEquals("hello", JsonParser.parse("\"hello\""), "Parsing quoted string should return string");
        assertEquals("", JsonParser.parse("\"\""), "Parsing empty quoted string should return empty string");
        assertEquals("hello\nworld", JsonParser.parse("\"hello\\nworld\""), "Parsing escaped newline should return newline");
        assertEquals("hello\"world", JsonParser.parse("\"hello\\\"world\""), "Parsing escaped quote should return quote");
        assertEquals("hello\\world", JsonParser.parse("\"hello\\\\world\""), "Parsing escaped backslash should return backslash");
    }

    public void testParseArray() {
        List<?> result = (List<?>) JsonParser.parse("[1,2,3]");
        assertEquals(3, result.size(), "Parsed array should have 3 elements");
        assertEquals(1, result.get(0), "First element should be 1");
        assertEquals(2, result.get(1), "Second element should be 2");
        assertEquals(3, result.get(2), "Third element should be 3");

        result = (List<?>) JsonParser.parse("[]");
        assertEquals(0, result.size(), "Parsed empty array should have 0 elements");

        result = (List<?>) JsonParser.parse("[\"a\",\"b\",\"c\"]");
        assertEquals(3, result.size(), "Parsed string array should have 3 elements");
        assertEquals("a", result.get(0), "First element should be 'a'");
        assertEquals("b", result.get(1), "Second element should be 'b'");
        assertEquals("c", result.get(2), "Third element should be 'c'");
    }

    public void testParseObject() {
        Map<?, ?> result = (Map<?, ?>) JsonParser.parse("{\"name\":\"John\",\"age\":30}");
        assertEquals(2, result.size(), "Parsed object should have 2 properties");
        assertEquals("John", result.get("name"), "name property should be 'John'");
        assertTrue(((Number) result.get("age")).longValue() == 30L, "age property should be 30");

        result = (Map<?, ?>) JsonParser.parse("{}");
        assertEquals(0, result.size(), "Parsed empty object should have 0 properties");
    }

    public void testSerializeParseRoundTrip() {
        // Test primitives
        assertEquals(42, JsonParser.parse(JsonSerializer.serialize(42)), "Integer round trip should match");
        assertEquals(true, JsonParser.parse(JsonSerializer.serialize(true)), "Boolean round trip should match");
        assertEquals("hello", JsonParser.parse(JsonSerializer.serialize("hello")), "String round trip should match");

        // Test array
        int[] array = {1, 2, 3};
        List<?> parsed = (List<?>) JsonParser.parse(JsonSerializer.serialize(array));
        assertEquals(3, parsed.size(), "Array round trip should have 3 elements");
        assertEquals(1, parsed.get(0), "First element should be 1");

        // Test list
        List<String> list = new ArrayList<>();
        list.add("a");
        list.add("b");
        parsed = (List<?>) JsonParser.parse(JsonSerializer.serialize(list));
        assertEquals(2, parsed.size(), "List round trip should have 2 elements");
        assertEquals("a", parsed.get(0), "First element should be 'a'");

        // Test map
        Map<String, Object> map = new HashMap<>();
        map.put("key", "value");
        Map<?, ?> parsedMap = (Map<?, ?>) JsonParser.parse(JsonSerializer.serialize(map));
        assertEquals(1, parsedMap.size(), "Map round trip should have 1 element");
        assertEquals("value", parsedMap.get("key"), "Value should match");
    }

    public static void main(String[] args) {
        JsonFormatTest test = new JsonFormatTest();
        test.run();
    }
}
