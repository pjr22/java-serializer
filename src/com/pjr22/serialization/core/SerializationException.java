package com.pjr22.serialization.core;

/**
 * Exception thrown when a serialization or deserialization error occurs.
 */
public class SerializationException extends Exception {

    /**
     * Constructs a new SerializationException with the specified detail message.
     *
     * @param message the detail message
     */
    public SerializationException(String message) {
        super(message);
    }

    /**
     * Constructs a new SerializationException with the specified detail message and cause.
     *
     * @param message the detail message
     * @param cause the cause
     */
    public SerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
