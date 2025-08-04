package ru.practicum.exception;

public class UniqueConflictException extends RuntimeException {
    public UniqueConflictException(String message) {
        super(message);
    }
}
