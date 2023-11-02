package ru.practicum.shareit.booking.exception;

public class OwnerNotFoundException extends RuntimeException {
    public OwnerNotFoundException() {
        super("Owner id is not found.");
    }
}
