package com.pjr22.serialization.test;

import com.pjr22.serialization.inspector.ConstructorAnalyzer;
import com.pjr22.serialization.inspector.FieldInspector;
import com.pjr22.serialization.test.data.*;

import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Test class for ConstructorAnalyzer.
 */
public class ConstructorAnalyzerTest extends TestCase {

    public void testSelectConstructorWithNoMatchingNames() throws Exception {
        // Provide field names that don't match any constructor parameters
        Set<String> fieldNames = new HashSet<>(Arrays.asList("unknownField1", "unknownField2"));
        Constructor<?> constructor = ConstructorAnalyzer.selectBestConstructor(SimplePerson.class, fieldNames);
        assertNotNull(constructor, "Should select a constructor");
        // With no field names matching, should prefer constructor with more parameters
        assertEquals(4, constructor.getParameterCount(), "Should prefer constructor with more parameters when no names match");
    }

    public void testSelectParameterizedConstructor() throws Exception {
        Set<String> fieldNames = getFieldNames(SimplePerson.class);
        Constructor<?> constructor = ConstructorAnalyzer.selectBestConstructor(SimplePerson.class, fieldNames);
        assertNotNull(constructor, "Should select a constructor");
        // SimplePerson has matching field names for parameterized constructor
        assertEquals(4, constructor.getParameterCount(), "Should select parameterized constructor with matching fields");
    }

    public void testSelectConstructorWithMatchingParameterNames() throws Exception {
        Set<String> fieldNames = getFieldNames(Address.class);
        Constructor<?> constructor = ConstructorAnalyzer.selectBestConstructor(Address.class, fieldNames);
        assertNotNull(constructor, "Should select a constructor");
        assertEquals(3, constructor.getParameterCount(), "Should select constructor with 3 parameters");
        
        Class<?>[] paramTypes = constructor.getParameterTypes();
        assertEquals(String.class, paramTypes[0], "First parameter should be String");
        assertEquals(String.class, paramTypes[1], "Second parameter should be String");
        assertEquals(String.class, paramTypes[2], "Third parameter should be String");
    }

    public void testSelectConstructorForImmutableObject() throws Exception {
        Set<String> fieldNames = getFieldNames(ImmutablePerson.class);
        Constructor<?> constructor = ConstructorAnalyzer.selectBestConstructor(ImmutablePerson.class, fieldNames);
        assertNotNull(constructor, "Should select a constructor for immutable object");
        assertEquals(3, constructor.getParameterCount(), "Should select the only available constructor");
        
        Class<?>[] paramTypes = constructor.getParameterTypes();
        assertEquals(String.class, paramTypes[0], "First parameter should be String");
        assertEquals(int.class, paramTypes[1], "Second parameter should be int");
        assertEquals(boolean.class, paramTypes[2], "Third parameter should be boolean");
    }

    public void testSelectConstructorWithFinalFields() throws Exception {
        Set<String> fieldNames = getFieldNames(PersonWithFinalFields.class);
        Constructor<?> constructor = ConstructorAnalyzer.selectBestConstructor(PersonWithFinalFields.class, fieldNames);
        assertNotNull(constructor, "Should select a constructor for class with final fields");
        assertEquals(3, constructor.getParameterCount(), "Should select parameterized constructor for final fields");
    }

    public void testSelectConstructorForInheritedClass() throws Exception {
        Set<String> fieldNames = getFieldNames(PersonWithInheritance.class);
        Constructor<?> constructor = ConstructorAnalyzer.selectBestConstructor(PersonWithInheritance.class, fieldNames);
        assertNotNull(constructor, "Should select a constructor for inherited class");
        assertEquals(4, constructor.getParameterCount(), "Should select constructor with 4 parameters");
    }

    public void testSelectConstructorForDepartment() throws Exception {
        Set<String> fieldNames = getFieldNames(Department.class);
        Constructor<?> constructor = ConstructorAnalyzer.selectBestConstructor(Department.class, fieldNames);
        assertNotNull(constructor, "Should select a constructor for Department");
        assertEquals(3, constructor.getParameterCount(), "Should select constructor with 3 parameters");
        
        Class<?>[] paramTypes = constructor.getParameterTypes();
        assertEquals(String.class, paramTypes[0], "First parameter should be String");
        assertEquals(java.math.BigDecimal.class, paramTypes[1], "Second parameter should be BigDecimal");
        assertEquals(SimplePerson.class, paramTypes[2], "Third parameter should be SimplePerson");
    }

    public void testCountMatchingParameterNames() throws Exception {
        Set<String> fieldNames = new HashSet<>(Arrays.asList("name", "age", "salary", "active"));
        Constructor<?> constructor = SimplePerson.class.getConstructor(String.class, int.class, double.class, boolean.class);
        int matchCount = ConstructorAnalyzer.countMatchingParameterNames(constructor, fieldNames);
        assertEquals(4, matchCount, "Should match all 4 parameter names");
    }

    public void testCountPartialMatchingParameterNames() throws Exception {
        Set<String> fieldNames = new HashSet<>(Arrays.asList("name", "age"));
        Constructor<?> constructor = SimplePerson.class.getConstructor(String.class, int.class, double.class, boolean.class);
        int matchCount = ConstructorAnalyzer.countMatchingParameterNames(constructor, fieldNames);
        assertEquals(2, matchCount, "Should match 2 parameter names");
    }

    public void testCountNoMatchingParameterNames() throws Exception {
        Set<String> fieldNames = new HashSet<>(Arrays.asList("unknown", "field"));
        Constructor<?> constructor = SimplePerson.class.getConstructor(String.class, int.class, double.class, boolean.class);
        int matchCount = ConstructorAnalyzer.countMatchingParameterNames(constructor, fieldNames);
        assertEquals(0, matchCount, "Should match 0 parameter names");
    }

    public void testSelectConstructorWhenNoConstructorsAvailable() throws Exception {
        // Create a class with no public constructors
        class NoPublicConstructor {
            private NoPublicConstructor() {}
        }
        
        Set<String> fieldNames = new HashSet<>();
        Constructor<?> constructor = ConstructorAnalyzer.selectBestConstructor(NoPublicConstructor.class, fieldNames);
        assertNull(constructor, "Should return null when no public constructors available");
    }

    public void testSelectConstructorWithEmptyFieldNames() throws Exception {
        Set<String> fieldNames = new HashSet<>();
        Constructor<?> constructor = ConstructorAnalyzer.selectBestConstructor(SimplePerson.class, fieldNames);
        assertNotNull(constructor, "Should select default constructor when no field names provided");
        assertEquals(0, constructor.getParameterCount(), "Should select default constructor");
    }

    /**
     * Helper method to get all field names for a class.
     */
    private Set<String> getFieldNames(Class<?> clazz) {
        Set<String> fieldNames = new HashSet<>();
        java.lang.reflect.Field[] fields = FieldInspector.getAllFields(clazz);
        for (java.lang.reflect.Field field : fields) {
            fieldNames.add(field.getName());
        }
        return fieldNames;
    }

    public static void main(String[] args) {
        ConstructorAnalyzerTest test = new ConstructorAnalyzerTest();
        test.run();
    }
}
