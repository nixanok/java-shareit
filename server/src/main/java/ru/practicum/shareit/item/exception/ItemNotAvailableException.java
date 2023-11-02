package ru.practicum.shareit.item.exception;

public class ItemNotAvailableException extends RuntimeException {
    public ItemNotAvailableException(Long id) {
        super(String.format("Item with id = %s not available for booking.", id));
    }
}
