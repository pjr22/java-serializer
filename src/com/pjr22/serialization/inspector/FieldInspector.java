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
}
