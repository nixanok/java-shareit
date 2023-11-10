package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import ru.practicum.shareit.item.dto.BasicUserInfo;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import static ru.practicum.shareit.tool.Headers.USER_ID;

@Controller
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
@Validated
public class ItemController {

    private final ItemClient itemClient;

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> itemById(@Positive @PathVariable int itemId,
                                           @Positive @RequestHeader(USER_ID) int userId) {
        log.info("Получен GET-запрос к эндпоинту /items/{itemId} на получение вещи по id.");
        return itemClient.itemById(itemId, userId);
    }

    @GetMapping
    public ResponseEntity<Object> allItemsByOwner(@Positive @RequestHeader(USER_ID) int ownerId,
                                                  @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                                  @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен GET-запрос к эндпоинту /items на получение всех вещей владельца по id.");
        return itemClient.allItemsByOwner(ownerId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> searchItem(@RequestParam String text,
                                             @PositiveOrZero @RequestParam(defaultValue = "0") int from,
                                             @Positive @RequestParam(defaultValue = "10") int size) {
        log.info("Получен GET-запрос к эндпоинту /items/search на поиск вещи.");
        return itemClient.searchItem(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> createItem(@Validated(BasicUserInfo.class) @RequestBody ItemDto itemDto,
                                             @Positive @RequestHeader(USER_ID) int ownerId) {
        log.info("Получен POST-запрос к эндпоинту /items на добавление вещи.");
        return itemClient.createItem(itemDto, ownerId);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> updateItem(@RequestBody ItemDto itemDto,
                                             @Positive @PathVariable int itemId,
                                             @Positive @RequestHeader(USER_ID) int ownerId) {
        log.info("Получен PATCH-запрос к эндпоинту /items/{itemId} на обновление вещи по id.");
        return itemClient.updateItem(itemDto, itemId, ownerId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> addComment(@Valid @RequestBody CommentDto commentDto,
                                             @Positive @PathVariable int itemId,
                                             @Positive @RequestHeader(USER_ID) int userId) {
        log.info("Получен POST-запрос к эндпоинту /items/{itemId}/comment на добавление комментария.");
        return itemClient.addComment(commentDto, itemId, userId);
    }
}
