package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto {

    private Long id;

    @NotBlank(message = "Name cannot be blank.", groups = BasicInfo.class)
    @Size(max = 100, message = "Max name length = 100.", groups = BasicInfo.class)
    private String name;

    @NotBlank(message = "Description cannot be blank.", groups = BasicInfo.class)
    private String description;

    @NotNull(message = "Available cannot be null.", groups = BasicInfo.class)
    private Boolean available;

}
