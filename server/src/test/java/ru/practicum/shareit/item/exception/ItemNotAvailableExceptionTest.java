package ru.practicum.shareit.item.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ItemNotAvailableExceptionTest {

    @Test
    public void testItemNotAvailableExceptionWithId() {
        Long itemId = 1L;
        String expectedMessage = String.format("Item with id = %s not available for booking.", itemId);

        ItemNotAvailableException exception = new ItemNotAvailableException(itemId);

        assertEquals(expectedMessage, exception.getMessage());
    }

}
