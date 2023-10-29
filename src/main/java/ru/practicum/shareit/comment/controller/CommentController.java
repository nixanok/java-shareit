package ru.practicum.shareit.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class CommentController {

    @Autowired
    private final CommentService commentService;

    @PostMapping(path = "/{itemId}/comment")
    public CommentDto postComment(@PositiveOrZero @PathVariable(name = "itemId") long itemId,
                                  @PositiveOrZero @RequestHeader("X-Sharer-User-Id") long authorId,
                                  @RequestBody @Valid CommentDto commentDto) {
        log.debug("Request \"postComment\"is called.");
        return commentService.postComment(itemId, authorId, commentDto);
    }

}
