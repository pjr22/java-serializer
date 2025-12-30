package com.pjr22.serialization.util;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.TreeSet;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Factory for creating appropriate collection instances based on the target field type.
 * Handles both concrete collection classes and collection interfaces by selecting
 * a suitable implementation.
 */
public class CollectionFactory {

    /**
     * Creates a collection instance appropriate for the specified field type.
     * 
     * @param fieldType the class type of the field (may be an interface or concrete class)
     * @return a new collection instance of the appropriate type
     * @throws IllegalArgumentException if the field type is not a collection type
     */
    @SuppressWarnings("unchecked")
    public static <T> Collection<T> createCollection(Class<?> fieldType) {
        if (!Collection.class.isAssignableFrom(fieldType)) {
            throw new IllegalArgumentException("Field type " + fieldType.getName() + " is not a Collection");
        }

        // Handle concrete implementations first
        try {
            return (Collection<T>) createConcreteInstance(fieldType);
        } catch (Exception e) {
            // Fall through to interface handling
        }

        // Handle interfaces by selecting a suitable implementation
        if (fieldType.isInterface()) {
            return (Collection<T>) createInterfaceImplementation(fieldType);
        }

        // Default to ArrayList for unknown types
        return new ArrayList<>();
    }

    /**
     * Creates an instance of a concrete collection class.
     */
    private static Collection<?> createConcreteInstance(Class<?> clazz) throws Exception {
        // Try to use the no-arg constructor
        try {
            return (Collection<?>) clazz.getDeclaredConstructor().newInstance();
        } catch (NoSuchMethodException e) {
            // Some concurrent collections require initial capacity
            if (clazz == LinkedBlockingQueue.class) {
                return new LinkedBlockingQueue<>();
            }
            if (clazz == LinkedBlockingDeque.class) {
                return new LinkedBlockingDeque<>();
            }
            throw e;
        }
    }

    /**
     * Creates a suitable implementation for a collection interface.
     */
    private static Collection<?> createInterfaceImplementation(Class<?> interfaceType) {
        // List interface - use ArrayList (good general-purpose choice)
        if (interfaceType == java.util.List.class || interfaceType == Collection.class) {
            return new ArrayList<>();
        }

        // Set interface - use LinkedHashSet (preserves insertion order)
        if (interfaceType == java.util.Set.class) {
            return new LinkedHashSet<>();
        }

        // SortedSet interface - use TreeSet
        if (interfaceType == java.util.SortedSet.class || interfaceType == java.util.NavigableSet.class) {
            return new TreeSet<>();
        }

        // Queue interface - use LinkedList (implements both Queue and Deque)
        if (interfaceType == java.util.Queue.class) {
            return new LinkedList<>();
        }

        // Deque interface - use ArrayDeque (more efficient than LinkedList for stack/queue operations)
        if (interfaceType == java.util.Deque.class) {
            return new ArrayDeque<>();
        }

        // BlockingQueue interface - use LinkedBlockingQueue
        if (interfaceType == java.util.concurrent.BlockingQueue.class) {
            return new LinkedBlockingQueue<>();
        }

        // BlockingDeque interface - use LinkedBlockingDeque
        if (interfaceType == java.util.concurrent.BlockingDeque.class) {
            return new LinkedBlockingDeque<>();
        }

        // TransferQueue interface - use LinkedBlockingQueue (basic implementation)
        if (interfaceType == java.util.concurrent.TransferQueue.class) {
            return new LinkedBlockingQueue<>();
        }

        // Default fallback
        return new ArrayList<>();
    }
}
