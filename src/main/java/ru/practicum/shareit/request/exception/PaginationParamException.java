package ru.practicum.shareit.request.exception;

public class PaginationParamException extends RuntimeException {
    public PaginationParamException(String message) {
        super(message);
    }
}
