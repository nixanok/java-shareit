package ru.practicum.shareit.comment.model;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CommentTest {

    @Test
    public void testEquals() {
        Comment comment1 = new Comment();
        comment1.setId(1L);

        Comment comment2 = new Comment();
        comment2.setId(1L);

        Comment comment3 = new Comment();
        comment3.setId(2L);

        assertTrue(comment1.equals(comment2));

        assertFalse(comment1.equals(comment3));
    }

    @Test
    public void testHashCode() {
        Comment comment1 = new Comment();
        comment1.setId(1L);

        Comment comment2 = new Comment();
        comment2.setId(1L);

        assertEquals(comment1.hashCode(), comment2.hashCode());

    }

}
