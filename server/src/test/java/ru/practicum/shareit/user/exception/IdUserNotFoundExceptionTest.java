package ru.practicum.shareit.user.exception;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class IdUserNotFoundExceptionTest {

    @Test
    public void testIdUserNotFoundExceptionMessage() {
        IdUserNotFoundException exception = new IdUserNotFoundException();
        String expectedMessage = "User with id = null cannot be updated.";
        String actualMessage = exception.getMessage();
        assertEquals(expectedMessage, actualMessage);
    }

}
