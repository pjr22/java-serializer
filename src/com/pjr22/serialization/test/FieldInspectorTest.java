package com.pjr22.serialization.test;

import com.pjr22.serialization.inspector.FieldInspector;
import com.pjr22.serialization.inspector.FieldClassifier;
import com.pjr22.serialization.inspector.FieldClassifier.FieldCategory;
import com.pjr22.serialization.test.data.*;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Test class for FieldInspector and FieldClassifier.
 */
public class FieldInspectorTest extends TestCase {

    public void testDetectPrimitiveFields() throws Exception {
        Field[] fields = FieldInspector.getAllFields(SimplePerson.class);
        assertTrue(fields.length >= 3, "Should have at least 3 fields");

        for (Field field : fields) {
            if (field.getName().equals("age")) {
                assertEquals(int.class, field.getType(), "Age should be int");
            } else if (field.getName().equals("active")) {
                assertEquals(boolean.class, field.getType(), "Active should be boolean");
            }
        }
    }

    public void testDetectStringFields() throws Exception {
        Field[] fields = FieldInspector.getAllFields(SimplePerson.class);
        boolean hasNameField = false;
        for (Field field : fields) {
            if (field.getName().equals("name")) {
                assertEquals(String.class, field.getType(), "Name should be String");
                hasNameField = true;
            }
        }
        assertTrue(hasNameField, "Should have name field");
    }

    public void testDetectNumberFields() throws Exception {
        Field[] fields = FieldInspector.getAllFields(SimplePerson.class);
        boolean hasSalaryField = false;
        for (Field field : fields) {
            if (field.getName().equals("salary")) {
                assertEquals(double.class, field.getType(), "Salary should be double");
                hasSalaryField = true;
            }
        }
        assertTrue(hasSalaryField, "Should have salary field");
    }

    public void testDetectBigDecimalFields() throws Exception {
        Field[] fields = FieldInspector.getAllFields(Department.class);
        boolean hasBudgetField = false;
        for (Field field : fields) {
            if (field.getName().equals("budget")) {
                assertEquals(BigDecimal.class, field.getType(), "Budget should be BigDecimal");
                hasBudgetField = true;
            }
        }
        assertTrue(hasBudgetField, "Should have budget field");
    }

    public void testDetectAtomicBooleanField() throws Exception {
        Field[] fields = FieldInspector.getAllFields(PersonWithAtomic.class);
        boolean hasFlagField = false;
        for (Field field : fields) {
            if (field.getName().equals("flag")) {
                assertEquals(AtomicBoolean.class, field.getType(), "Flag should be AtomicBoolean");
                hasFlagField = true;
            }
        }
        assertTrue(hasFlagField, "Should have flag field");
    }

    public void testDetectAtomicIntegerField() throws Exception {
        Field[] fields = FieldInspector.getAllFields(PersonWithAtomic.class);
        boolean hasCounterField = false;
        for (Field field : fields) {
            if (field.getName().equals("counter")) {
                assertEquals(AtomicInteger.class, field.getType(), "Counter should be AtomicInteger");
                hasCounterField = true;
            }
        }
        assertTrue(hasCounterField, "Should have counter field");
    }

    public void testDetectObjectReferenceFields() throws Exception {
        Field[] fields = FieldInspector.getAllFields(PersonWithReferences.class);
        boolean hasAddressField = false;
        boolean hasDepartmentField = false;
        for (Field field : fields) {
            if (field.getName().equals("address")) {
                assertEquals(Address.class, field.getType(), "Address should be Address type");
                hasAddressField = true;
            } else if (field.getName().equals("department")) {
                assertEquals(Department.class, field.getType(), "Department should be Department type");
                hasDepartmentField = true;
            }
        }
        assertTrue(hasAddressField, "Should have address field");
        assertTrue(hasDepartmentField, "Should have department field");
    }

    public void testExcludeStaticFields() throws Exception {
        Field[] fields = FieldInspector.getAllFields(PersonWithStaticTransient.class);
        boolean hasStaticField = false;
        for (Field field : fields) {
            if (field.getName().equals("staticField")) {
                hasStaticField = true;
            }
        }
        assertFalse(hasStaticField, "Static fields should be excluded");
    }

    public void testExcludeTransientFields() throws Exception {
        Field[] fields = FieldInspector.getAllFields(PersonWithStaticTransient.class);
        boolean hasTransientField = false;
        for (Field field : fields) {
            if (field.getName().equals("transientField")) {
                hasTransientField = true;
            }
        }
        assertFalse(hasTransientField, "Transient fields should be excluded");
    }

    public void testIncludeInheritedFields() throws Exception {
        Field[] fields = FieldInspector.getAllFields(PersonWithInheritance.class);
        boolean hasFirstName = false;
        boolean hasLastName = false;
        boolean hasAge = false;
        boolean hasEmail = false;

        for (Field field : fields) {
            if (field.getName().equals("firstName")) hasFirstName = true;
            if (field.getName().equals("lastName")) hasLastName = true;
            if (field.getName().equals("age")) hasAge = true;
            if (field.getName().equals("email")) hasEmail = true;
        }

        assertTrue(hasFirstName, "Should include inherited firstName field");
        assertTrue(hasLastName, "Should include inherited lastName field");
        assertTrue(hasAge, "Should include age field");
        assertTrue(hasEmail, "Should include email field");
    }

    public void testAccessPrivateFields() throws Exception {
        Field[] fields = FieldInspector.getAllFields(SimplePerson.class);
        assertTrue(fields.length > 0, "Should have fields");
        // All fields should be private
        for (Field field : fields) {
            assertTrue(java.lang.reflect.Modifier.isPrivate(field.getModifiers()),
                    "All fields should be private");
        }
    }

    public void testClassifyPrimitiveField() throws Exception {
        Field field = SimplePerson.class.getDeclaredField("age");
        FieldCategory category = FieldClassifier.classify(field);
        assertEquals(FieldCategory.PRIMITIVE, category, "int should be classified as PRIMITIVE");
    }

    public void testClassifyStringField() throws Exception {
        Field field = SimplePerson.class.getDeclaredField("name");
        FieldCategory category = FieldClassifier.classify(field);
        assertEquals(FieldCategory.STRING, category, "String should be classified as STRING");
    }

    public void testClassifyNumberField() throws Exception {
        Field field = SimplePerson.class.getDeclaredField("salary");
        FieldCategory category = FieldClassifier.classify(field);
        assertEquals(FieldCategory.NUMBER, category, "double should be classified as NUMBER");
    }

    public void testClassifyBigDecimalField() throws Exception {
        Field field = Department.class.getDeclaredField("budget");
        FieldCategory category = FieldClassifier.classify(field);
        assertEquals(FieldCategory.BIG_DECIMAL, category, "BigDecimal should be classified as BIG_DECIMAL");
    }

    public void testClassifyAtomicBooleanField() throws Exception {
        Field field = PersonWithAtomic.class.getDeclaredField("flag");
        FieldCategory category = FieldClassifier.classify(field);
        assertEquals(FieldCategory.ATOMIC_BOOLEAN, category, "AtomicBoolean should be classified as ATOMIC_BOOLEAN");
    }

    public void testClassifyAtomicIntegerField() throws Exception {
        Field field = PersonWithAtomic.class.getDeclaredField("counter");
        FieldCategory category = FieldClassifier.classify(field);
        assertEquals(FieldCategory.ATOMIC_INTEGER, category, "AtomicInteger should be classified as ATOMIC_INTEGER");
    }

    public void testClassifyObjectReferenceField() throws Exception {
        Field field = PersonWithReferences.class.getDeclaredField("address");
        FieldCategory category = FieldClassifier.classify(field);
        assertEquals(FieldCategory.OBJECT_REFERENCE, category, "Address should be classified as OBJECT_REFERENCE");
    }

    public void testClassifyCollectionField() throws Exception {
        Field field = PersonWithCollections.class.getDeclaredField("tags");
        FieldCategory category = FieldClassifier.classify(field);
        assertEquals(FieldCategory.COLLECTION, category, "List should be classified as COLLECTION");
    }

    public void testClassifyMapField() throws Exception {
        Field field = PersonWithMap.class.getDeclaredField("properties");
        FieldCategory category = FieldClassifier.classify(field);
        assertEquals(FieldCategory.MAP, category, "Map should be classified as MAP");
    }

    public void testClassifyArrayField() throws Exception {
        Field field = PersonWithArray.class.getDeclaredField("values");
        FieldCategory category = FieldClassifier.classify(field);
        assertEquals(FieldCategory.ARRAY, category, "Array should be classified as ARRAY");
    }

    public void testClassifyEnumField() throws Exception {
        Field field = PersonWithEnum.class.getDeclaredField("status");
        FieldCategory category = FieldClassifier.classify(field);
        assertEquals(FieldCategory.ENUM, category, "Enum should be classified as ENUM");
    }

    public static void main(String[] args) {
        FieldInspectorTest test = new FieldInspectorTest();
        test.run();
    }
}
