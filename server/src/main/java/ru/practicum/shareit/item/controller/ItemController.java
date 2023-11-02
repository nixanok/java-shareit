package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.dto.BasicUserInfo;
import ru.practicum.shareit.item.model.dto.ItemBookingsDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.PositiveOrZero;
import java.util.List;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Validated
@Slf4j
public class ItemController {

    @Autowired
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(
            @RequestBody @Validated(BasicUserInfo.class) ItemDto item,
            @PositiveOrZero @RequestHeader("X-Sharer-User-Id") long ownerId
    ) {
        log.debug("Request \"createItem\"is called.");
        return itemService.create(item, ownerId);
    }

    @PatchMapping(path = "/{itemId}")
    public ItemDto patchItem(
            @PositiveOrZero @PathVariable(name = "itemId") long id,
            @RequestBody final ItemDto item,
            @PositiveOrZero @RequestHeader("X-Sharer-User-Id") long ownerId
    ) {
        log.debug("Request \"patchItem\"is called.");
        return itemService.patch(id, item, ownerId);
    }

    @GetMapping
    public List<ItemBookingsDto> getItemsByOwnerId(@PositiveOrZero @RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.debug("Request \"getItemsByOwnerId\"is called.");
        return itemService.getByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.debug("Request \"searchItems\"is called.");
        return itemService.search(text);
    }

    @GetMapping("/{itemId}")
    public ItemBookingsDto getItem(
            @PositiveOrZero @RequestHeader("X-Sharer-User-Id") long ownerId,
            @PositiveOrZero @PathVariable(name = "itemId") long id) {
        log.debug("Request \"getItem\"is called.");
        return itemService.getById(id, ownerId);
    }

    @DeleteMapping("/{itemId}")
    public void removeItemById(@PositiveOrZero @PathVariable(name = "itemId") long id) {
        log.debug("Request \"removeItemById\"is called.");
        itemService.removeById(id);
    }

}
