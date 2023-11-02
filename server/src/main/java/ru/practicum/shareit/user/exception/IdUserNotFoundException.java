package ru.practicum.shareit.user.exception;

public class IdUserNotFoundException extends RuntimeException {
    public IdUserNotFoundException() {
        super("User with id = null cannot be updated.");
    }
}
