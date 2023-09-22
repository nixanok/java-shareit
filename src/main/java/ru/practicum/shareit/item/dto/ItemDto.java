package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDto {

    private Long id;

    @NotBlank(message = "Name cannot be blank.")
    @Size(max = 100, message = "Max name length = 100.")
    private String name;

    @NotBlank(message = "Description cannot be blank.")
    private String description;

    @NotNull
    private Boolean available;

    private Long requestId;

}
