package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    @Test
    public void testEqualsSameUser() {
        User user1 = new User();
        user1.setEmail("user@example.com");

        assertTrue(user1.equals(user1));
    }

    @Test
    public void testEqualsEqualUsers() {
        User user1 = new User();
        user1.setEmail("user@example.com");

        User user2 = new User();
        user2.setEmail("user@example.com");

        assertTrue(user1.equals(user2));
    }

    @Test
    public void testEqualsDifferentUsers() {
        User user1 = new User();
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setEmail("user2@example.com");

        assertFalse(user1.equals(user2));
    }

    @Test
    public void testHashCode() {
        User user1 = new User();
        user1.setEmail("user@example.com");

        User user2 = new User();
        user2.setEmail("user@example.com");

        assertEquals(user1.hashCode(), user2.hashCode());
    }
}
