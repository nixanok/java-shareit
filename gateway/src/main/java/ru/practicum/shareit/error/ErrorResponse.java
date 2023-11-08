package ru.practicum.shareit.error;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ErrorResponse {
    private final String error;
    private final int code;
    private final String description;
    private final LocalDateTime timestamp;
}

