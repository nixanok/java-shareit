package ru.practicum.shareit.request.exception;

public class RequestNotFoundException extends RuntimeException {
    public RequestNotFoundException(Long id)  {
        super(String.format("Request with id = %s not found.", id));
    }
}

