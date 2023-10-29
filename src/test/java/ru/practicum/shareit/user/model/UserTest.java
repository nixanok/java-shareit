package ru.practicum.shareit.user.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class UserTest {
    @Test
    public void testEqualsSameUser() {
        User user1 = new User();
        user1.setEmail("user@example.com");

        assertEquals(user1, user1);
    }

    @Test
    public void testEqualsEqualUsers() {
        User user1 = new User();
        user1.setEmail("user@example.com");

        User user2 = new User();
        user2.setEmail("user@example.com");

        assertEquals(user1, user2);
    }

    @Test
    public void testEqualsDifferentUsers() {
        User user1 = new User();
        user1.setEmail("user1@example.com");

        User user2 = new User();
        user2.setEmail("user2@example.com");

        assertNotEquals(user1, user2);
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
