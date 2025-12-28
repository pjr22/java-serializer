package com.pjr22.serialization.format;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Parses JSON strings into Java objects.
 * Handles null, boolean, number, string, array, and object types.
 */
public class JsonParser {

    private String json;
    private int index;

    /**
     * Parses a JSON string and returns the corresponding Java object.
     *
     * @param json the JSON string to parse
     * @return the parsed Java object
     */
    public static Object parse(String json) {
        if (json == null || json.trim().isEmpty()) {
            throw new IllegalArgumentException("JSON string cannot be null or empty");
        }
        JsonParser parser = new JsonParser(json);
        Object result = parser.parseValue();
        parser.skipWhitespace();
        if (parser.index < parser.json.length()) {
            throw new IllegalArgumentException("Unexpected character at position " + parser.index + ": " + parser.json.charAt(parser.index));
        }
        return result;
    }

    private JsonParser(String json) {
        this.json = json;
        this.index = 0;
    }

    /**
     * Parses a JSON value (null, boolean, number, string, array, or object).
     *
     * @return the parsed value
     */
    private Object parseValue() {
        skipWhitespace();
        if (index >= json.length()) {
            throw new IllegalArgumentException("Unexpected end of JSON");
        }

        char c = json.charAt(index);
        switch (c) {
            case 'n':
                return parseNull();
            case 't':
            case 'f':
                return parseBoolean();
            case '"':
                return parseString();
            case '[':
                return parseArray();
            case '{':
                return parseObject();
            case '-':
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return parseNumber();
            default:
                throw new IllegalArgumentException("Unexpected character at position " + index + ": " + c);
        }
    }

    /**
     * Parses a JSON null value.
     *
     * @return null
     */
    private Object parseNull() {
        if (json.startsWith("null", index)) {
            index += 4;
            return null;
        }
        throw new IllegalArgumentException("Expected 'null' at position " + index);
    }

    /**
     * Parses a JSON boolean value.
     *
     * @return the parsed boolean
     */
    private Object parseBoolean() {
        if (json.startsWith("true", index)) {
            index += 4;
            return true;
        }
        if (json.startsWith("false", index)) {
            index += 5;
            return false;
        }
        throw new IllegalArgumentException("Expected 'true' or 'false' at position " + index);
    }

    /**
     * Parses a JSON string value.
     *
     * @return the parsed string
     */
    private String parseString() {
        if (json.charAt(index) != '"') {
            throw new IllegalArgumentException("Expected '\"' at position " + index);
        }
        index++; // skip opening quote

        StringBuilder sb = new StringBuilder();
        while (index < json.length()) {
            char c = json.charAt(index);
            if (c == '"') {
                index++; // skip closing quote
                return sb.toString();
            }
            if (c == '\\') {
                index++;
                if (index >= json.length()) {
                    throw new IllegalArgumentException("Unexpected end of string at position " + index);
                }
                c = json.charAt(index);
                switch (c) {
                    case '"':
                        sb.append('"');
                        break;
                    case '\\':
                        sb.append('\\');
                        break;
                    case '/':
                        sb.append('/');
                        break;
                    case 'b':
                        sb.append('\b');
                        break;
                    case 'f':
                        sb.append('\f');
                        break;
                    case 'n':
                        sb.append('\n');
                        break;
                    case 'r':
                        sb.append('\r');
                        break;
                    case 't':
                        sb.append('\t');
                        break;
                    case 'u':
                        if (index + 4 >= json.length()) {
                            throw new IllegalArgumentException("Invalid unicode escape at position " + index);
                        }
                        String hex = json.substring(index + 1, index + 5);
                        try {
                            int codePoint = Integer.parseInt(hex, 16);
                            sb.append((char) codePoint);
                            index += 4;
                        } catch (NumberFormatException e) {
                            throw new IllegalArgumentException("Invalid unicode escape at position " + index + ": \\u" + hex);
                        }
                        break;
                    default:
                        sb.append(c);
                }
                index++;
            } else {
                sb.append(c);
                index++;
            }
        }
        throw new IllegalArgumentException("Unterminated string");
    }

    /**
     * Parses a JSON number value.
     *
     * @return the parsed number (Integer, Long, or Double)
     */
    private Number parseNumber() {
        skipWhitespace();
        int start = index;

        // Handle optional minus sign
        if (index < json.length() && json.charAt(index) == '-') {
            index++;
        }

        // Handle integer part
        if (index < json.length() && json.charAt(index) == '0') {
            index++;
        } else {
            while (index < json.length() && Character.isDigit(json.charAt(index))) {
                index++;
            }
        }

        // Handle fractional part
        boolean hasFraction = false;
        if (index < json.length() && json.charAt(index) == '.') {
            hasFraction = true;
            index++;
            while (index < json.length() && Character.isDigit(json.charAt(index))) {
                index++;
            }
        }

        // Handle exponent part
        boolean hasExponent = false;
        if (index < json.length() && (json.charAt(index) == 'e' || json.charAt(index) == 'E')) {
            hasExponent = true;
            index++;
            if (index < json.length() && (json.charAt(index) == '+' || json.charAt(index) == '-')) {
                index++;
            }
            while (index < json.length() && Character.isDigit(json.charAt(index))) {
                index++;
            }
        }

        String numberStr = json.substring(start, index);

        try {
            if (hasFraction || hasExponent) {
                return Double.parseDouble(numberStr);
            } else {
                long value = Long.parseLong(numberStr);
                if (value >= Integer.MIN_VALUE && value <= Integer.MAX_VALUE) {
                    return (int) value;
                }
                return value;
            }
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("Invalid number at position " + start + ": " + numberStr);
        }
    }

    /**
     * Parses a JSON array value.
     *
     * @return the parsed list
     */
    private List<Object> parseArray() {
        if (json.charAt(index) != '[') {
            throw new IllegalArgumentException("Expected '[' at position " + index);
        }
        index++; // skip opening bracket

        List<Object> list = new ArrayList<>();
        skipWhitespace();

        if (index < json.length() && json.charAt(index) == ']') {
            index++; // skip closing bracket
            return list;
        }

        while (index < json.length()) {
            skipWhitespace();
            Object value = parseValue();
            list.add(value);
            skipWhitespace();

            if (index >= json.length()) {
                throw new IllegalArgumentException("Unexpected end of array");
            }

            char c = json.charAt(index);
            if (c == ']') {
                index++;
                return list;
            }
            if (c != ',') {
                throw new IllegalArgumentException("Expected ',' or ']' at position " + index);
            }
            index++; // skip comma
        }
        throw new IllegalArgumentException("Unterminated array");
    }

    /**
     * Parses a JSON object value.
     *
     * @return the parsed map
     */
    private Map<String, Object> parseObject() {
        if (json.charAt(index) != '{') {
            throw new IllegalArgumentException("Expected '{' at position " + index);
        }
        index++; // skip opening brace

        Map<String, Object> map = new LinkedHashMap<>();
        skipWhitespace();

        if (index < json.length() && json.charAt(index) == '}') {
            index++; // skip closing brace
            return map;
        }

        while (index < json.length()) {
            skipWhitespace();

            // Parse key
            if (json.charAt(index) != '"') {
                throw new IllegalArgumentException("Expected '\"' for object key at position " + index);
            }
            String key = parseString();

            skipWhitespace();

            // Parse colon
            if (index >= json.length() || json.charAt(index) != ':') {
                throw new IllegalArgumentException("Expected ':' after object key at position " + index);
            }
            index++; // skip colon

            // Parse value
            Object value = parseValue();
            map.put(key, value);

            skipWhitespace();

            if (index >= json.length()) {
                throw new IllegalArgumentException("Unexpected end of object");
            }

            char c = json.charAt(index);
            if (c == '}') {
                index++;
                return map;
            }
            if (c != ',') {
                throw new IllegalArgumentException("Expected ',' or '}' at position " + index);
            }
            index++; // skip comma
        }
        throw new IllegalArgumentException("Unterminated object");
    }

    /**
     * Skips whitespace characters.
     */
    private void skipWhitespace() {
        while (index < json.length() && Character.isWhitespace(json.charAt(index))) {
            index++;
        }
    }
}
