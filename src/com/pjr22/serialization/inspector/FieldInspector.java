package com.pjr22.serialization.inspector;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

/**
 * Inspects Java classes to extract fields for serialization.
 * This class uses reflection to get all fields from a class and its superclasses,
 * excluding static and transient fields.
 */
public class FieldInspector {

    /**
     * Gets all non-static, non-transient fields from a class and its superclasses.
     * Private fields are made accessible.
     *
     * @param clazz the class to inspect
     * @return array of fields
     */
    public static Field[] getAllFields(Class<?> clazz) {
        List<Field> fieldList = new ArrayList<>();
        Class<?> currentClass = clazz;

        // Walk up the class hierarchy to get all inherited fields
        while (currentClass != null && currentClass != Object.class) {
            // Skip JDK and system classes to avoid module system access restrictions
            // in Java 9+. These classes don't need field inspection for serialization.
            if (isSystemClass(currentClass)) {
                currentClass = currentClass.getSuperclass();
                continue;
            }

            Field[] declaredFields = currentClass.getDeclaredFields();

            for (Field field : declaredFields) {
                int modifiers = field.getModifiers();

                // Exclude static and transient fields
                if (!Modifier.isStatic(modifiers) && !Modifier.isTransient(modifiers)) {
                    // Make private fields accessible
                    field.setAccessible(true);
                    fieldList.add(field);
                }
            }

            currentClass = currentClass.getSuperclass();
        }

        return fieldList.toArray(new Field[0]);
    }

    /**
     * Checks if a class is a JDK or system class.
     * System classes (java.*, javax.*, sun.*) should not be inspected via reflection
     * to avoid InaccessibleObjectException in Java 9+ due to module system restrictions.
     *
     * @param clazz the class to check
     * @return true if the class is a system class, false otherwise
     */
    private static boolean isSystemClass(Class<?> clazz) {
        // Check if class loader is null (bootstrap class loader)
        if (clazz.getClassLoader() == null) {
            return true;
        }

        // Check for known system package prefixes
        String className = clazz.getName();
        return className.startsWith("java.") ||
               className.startsWith("javax.") ||
               className.startsWith("sun.");
    }
}
