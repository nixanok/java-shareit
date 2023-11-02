package ru.practicum.shareit.comment.service;

import ru.practicum.shareit.comment.model.dto.CommentDto;

public interface CommentService {

    CommentDto postComment(Long itemId, Long authorId, CommentDto commentDto);

}
