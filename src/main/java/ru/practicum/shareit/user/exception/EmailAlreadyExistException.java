package ru.practicum.shareit.user.exception;

public class EmailAlreadyExistException extends RuntimeException {
    public EmailAlreadyExistException(String email) {
        super(String.format("Email = \"%s\" already exist.", email));
    }
}
