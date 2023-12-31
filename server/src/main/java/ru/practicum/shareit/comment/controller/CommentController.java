package ru.practicum.shareit.comment.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.model.dto.CommentDto;
import ru.practicum.shareit.comment.service.CommentService;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Validated
@Slf4j
public class CommentController {

    private final CommentService commentService;

    @PostMapping(path = "/{itemId}/comment")
    public CommentDto postComment(@PathVariable(name = "itemId") long itemId,
                                  @RequestHeader("X-Sharer-User-Id") long authorId,
                                  @RequestBody CommentDto commentDto) {
        log.debug("Request \"postComment\"is called.");
        return commentService.postComment(itemId, authorId, commentDto);
    }

}
