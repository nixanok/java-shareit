package ru.practicum.shareit.item.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ItemTest {
    @Test
    public void testEqualsSameItem() {
        Item item1 = new Item();
        item1.setId(1L);

        assertTrue(item1.equals(item1));
    }

    @Test
    public void testEqualsEqualItems() {
        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(1L);

        assertTrue(item1.equals(item2));
    }

    @Test
    public void testEqualsDifferentItems() {
        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(2L);

        assertFalse(item1.equals(item2));
    }

    @Test
    public void testHashCode() {
        Item item1 = new Item();
        item1.setId(1L);

        Item item2 = new Item();
        item2.setId(1L);

        assertEquals(item1.hashCode(), item2.hashCode());
    }
}
