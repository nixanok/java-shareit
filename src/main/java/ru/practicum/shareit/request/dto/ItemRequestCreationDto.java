package ru.practicum.shareit.request.dto;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

public class ItemRequestCreationDto {

    @NotBlank(message = "Description cannot be blank.")
    private String description;

    private Long requesterId;

    private LocalDateTime created;
}
