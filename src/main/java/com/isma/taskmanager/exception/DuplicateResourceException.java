package com.isma.taskmanager.exception;

/**
 * Thrown when a resource conflicts with an existing one (e.g. duplicate name).
 */
public class DuplicateResourceException extends RuntimeException {

    public DuplicateResourceException(String message) {
        super(message);
    }
}
