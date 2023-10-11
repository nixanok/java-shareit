package ru.practicum.shareit.comment.model.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import java.time.LocalDateTime;

@Data
@Builder
public class CommentDto {

    private Long id;

    @NotBlank
    private String text;

    private String authorName;

    private LocalDateTime created;
}
