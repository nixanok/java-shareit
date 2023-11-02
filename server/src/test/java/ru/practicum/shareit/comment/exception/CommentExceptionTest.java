package ru.practicum.shareit.comment.exception;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CommentExceptionTest {

    @Test
    public void testCommentExceptionWithMessage() {
        String errorMessage = "Test Comment Exception Message";

        CommentException commentException = new CommentException(errorMessage);

        assertEquals(errorMessage, commentException.getMessage());
    }
}
