package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class ItemRequest {

    private Long id;

    @NotBlank(message = "Description cannot be blank.")
    private String description;

    private Long requesterId;

    private LocalDateTime created;

}
