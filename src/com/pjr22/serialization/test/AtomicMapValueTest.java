package com.pjr22.serialization.test;

import com.pjr22.serialization.core.Deserializer;
import com.pjr22.serialization.core.SerializationException;
import com.pjr22.serialization.core.Serializer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Tests for atomic types as map values.
 */
public class AtomicMapValueTest extends TestCase {

    public static class ObjectWithAtomicLongMap {
        private Map<String, AtomicLong> counts;

        public ObjectWithAtomicLongMap() {
            this.counts = new LinkedHashMap<>();
        }

        public ObjectWithAtomicLongMap(Map<String, AtomicLong> counts) {
            this.counts = new LinkedHashMap<>(counts);
        }

        public Map<String, AtomicLong> getCounts() {
            return counts;
        }

        public void setCounts(Map<String, AtomicLong> counts) {
            this.counts = counts;
        }
    }

    public static class ObjectWithAtomicIntegerMap {
        private Map<String, AtomicInteger> values;

        public ObjectWithAtomicIntegerMap() {
            this.values = new LinkedHashMap<>();
        }

        public Map<String, AtomicInteger> getValues() {
            return values;
        }

        public void setValues(Map<String, AtomicInteger> values) {
            this.values = values;
        }
    }

    public static class ObjectWithAtomicBooleanMap {
        private Map<String, AtomicBoolean> flags;

        public ObjectWithAtomicBooleanMap() {
            this.flags = new LinkedHashMap<>();
        }

        public Map<String, AtomicBoolean> getFlags() {
            return flags;
        }

        public void setFlags(Map<String, AtomicBoolean> flags) {
            this.flags = flags;
        }
    }

    public void testAtomicLongMapRoundTrip() throws SerializationException, IOException {
        ObjectWithAtomicLongMap original = new ObjectWithAtomicLongMap();
        original.getCounts().put("counter1", new AtomicLong(100L));
        original.getCounts().put("counter2", new AtomicLong(200L));

        Serializer serializer = new Serializer("test", 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(original, out);

        Deserializer<ObjectWithAtomicLongMap> deserializer = new Deserializer<>(ObjectWithAtomicLongMap.class);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ObjectWithAtomicLongMap result = deserializer.deserialize(in);

        assertNotNull(result, "Result should not be null");
        assertNotNull(result.getCounts(), "Counts map should not be null");
        assertEquals(2, result.getCounts().size(), "Map should have 2 entries");

        Object counter1 = result.getCounts().get("counter1");
        assertTrue(counter1 instanceof AtomicLong, "counter1 should be AtomicLong but was: " + (counter1 == null ? "null" : counter1.getClass().getName()));
        assertEquals(100L, ((AtomicLong) counter1).get(), "counter1 value mismatch");

        Object counter2 = result.getCounts().get("counter2");
        assertTrue(counter2 instanceof AtomicLong, "counter2 should be AtomicLong but was: " + (counter2 == null ? "null" : counter2.getClass().getName()));
        assertEquals(200L, ((AtomicLong) counter2).get(), "counter2 value mismatch");
    }

    public void testAtomicIntegerMapRoundTrip() throws SerializationException, IOException {
        ObjectWithAtomicIntegerMap original = new ObjectWithAtomicIntegerMap();
        original.getValues().put("val1", new AtomicInteger(42));
        original.getValues().put("val2", new AtomicInteger(84));

        Serializer serializer = new Serializer("test", 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(original, out);

        Deserializer<ObjectWithAtomicIntegerMap> deserializer = new Deserializer<>(ObjectWithAtomicIntegerMap.class);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ObjectWithAtomicIntegerMap result = deserializer.deserialize(in);

        assertNotNull(result, "Result should not be null");
        assertNotNull(result.getValues(), "Values map should not be null");
        assertEquals(2, result.getValues().size(), "Map should have 2 entries");

        Object val1 = result.getValues().get("val1");
        assertTrue(val1 instanceof AtomicInteger, "val1 should be AtomicInteger but was: " + (val1 == null ? "null" : val1.getClass().getName()));
        assertEquals(42, ((AtomicInteger) val1).get(), "val1 value mismatch");

        Object val2 = result.getValues().get("val2");
        assertTrue(val2 instanceof AtomicInteger, "val2 should be AtomicInteger but was: " + (val2 == null ? "null" : val2.getClass().getName()));
        assertEquals(84, ((AtomicInteger) val2).get(), "val2 value mismatch");
    }

    public void testAtomicBooleanMapRoundTrip() throws SerializationException, IOException {
        ObjectWithAtomicBooleanMap original = new ObjectWithAtomicBooleanMap();
        original.getFlags().put("enabled", new AtomicBoolean(true));
        original.getFlags().put("disabled", new AtomicBoolean(false));

        Serializer serializer = new Serializer("test", 0);
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        serializer.serialize(original, out);

        Deserializer<ObjectWithAtomicBooleanMap> deserializer = new Deserializer<>(ObjectWithAtomicBooleanMap.class);
        ByteArrayInputStream in = new ByteArrayInputStream(out.toByteArray());
        ObjectWithAtomicBooleanMap result = deserializer.deserialize(in);

        assertNotNull(result, "Result should not be null");
        assertNotNull(result.getFlags(), "Flags map should not be null");
        assertEquals(2, result.getFlags().size(), "Map should have 2 entries");

        Object enabled = result.getFlags().get("enabled");
        assertTrue(enabled instanceof AtomicBoolean, "enabled should be AtomicBoolean but was: " + (enabled == null ? "null" : enabled.getClass().getName()));
        assertTrue(((AtomicBoolean) enabled).get(), "enabled value should be true");

        Object disabled = result.getFlags().get("disabled");
        assertTrue(disabled instanceof AtomicBoolean, "disabled should be AtomicBoolean but was: " + (disabled == null ? "null" : disabled.getClass().getName()));
        assertFalse(((AtomicBoolean) disabled).get(), "disabled value should be false");
    }

    public static void main(String[] args) {
        AtomicMapValueTest test = new AtomicMapValueTest();
        test.run();
    }
}
