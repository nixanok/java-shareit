package ru.practicum.shareit.comment.tool;

import lombok.experimental.UtilityClass;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.model.dto.CommentDto;

@UtilityClass
public class CommentMapper {

    public CommentDto toDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor() == null ? null : comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }

    public Comment fromDto(CommentDto comment) {
        return Comment.builder()
                .id(comment.getId())
                .text(comment.getText())
                .created(comment.getCreated())
                .build();
    }

}
