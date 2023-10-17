package ru.practicum.shareit.user.exception;

public class EmailAlreadyExistsException extends RuntimeException {
    public EmailAlreadyExistsException(String email) {
        super(String.format("Email = %s already exists.", email));
    }
}
