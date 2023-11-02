package ru.practicum.shareit.request.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ItemRequestCreationDto {

    private Long id;

    @NotBlank(message = "Description cannot be blank.")
    private String description;

    private Long requesterId;

    private LocalDateTime created;
}
