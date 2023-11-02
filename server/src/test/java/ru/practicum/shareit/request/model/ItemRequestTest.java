package ru.practicum.shareit.request.model;

import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

public class ItemRequestTest {

    @Test
    public void testEquals() {
        ItemRequest request1 = new ItemRequest(1L, "Test Description", null, LocalDateTime.now());
        ItemRequest request2 = new ItemRequest(1L, "Test Description", null, LocalDateTime.now());
        ItemRequest request3 = new ItemRequest(2L, "Different Description", null, LocalDateTime.now());

        assertEquals(request1, request2, "Equals method should return true for identical objects.");
        assertNotEquals(request1, request3, "Equals method should return false for different objects.");
    }

    @Test
    public void testHashCode() {
        ItemRequest request1 = new ItemRequest(1L, "Test Description", null, LocalDateTime.now());
        ItemRequest request2 = new ItemRequest(1L, "Test Description", null, LocalDateTime.now());

        assertEquals(request1.hashCode(), request2.hashCode(), "Hash code should be the same for identical objects.");
    }
}
