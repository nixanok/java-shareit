package ru.practicum.shareit.item.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemDto {

    private Long id;

    @NotBlank(message = "Name cannot be blank.", groups = BasicUserInfo.class)
    @Size(max = 100, message = "Max name length = 100.", groups = BasicUserInfo.class)
    private String name;

    @NotBlank(message = "Description cannot be blank.", groups = BasicUserInfo.class)
    private String description;

    @NotNull(message = "Available cannot be null.", groups = BasicUserInfo.class)
    private Boolean available;

    private Long requestId;

}
