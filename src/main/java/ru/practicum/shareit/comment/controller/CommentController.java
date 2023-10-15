package ru.practicum.shareit.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class CommentController {

    @Autowired
    private final CommentService commentService;

    @PostMapping(path = "/{itemId}/comment")
    public CommentDto postComment(@PathVariable(name = "itemId") long itemId,
                                  @RequestHeader("X-Sharer-User-Id") long authorId,
                                  @RequestBody @Valid CommentDto commentDto) {
        log.debug("Request \"postComment\"is called.");
        return commentService.postComment(itemId, authorId, commentDto);
    }

}
