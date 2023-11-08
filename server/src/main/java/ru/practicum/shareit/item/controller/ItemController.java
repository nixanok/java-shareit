package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.model.dto.ItemBookingsDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import java.util.List;

@RestController
@RequestMapping(path = "/items")
@RequiredArgsConstructor
@Slf4j
public class ItemController {

    @Autowired
    private final ItemService itemService;

    @PostMapping
    public ItemDto createItem(
            @RequestBody ItemDto item,
            @RequestHeader("X-Sharer-User-Id") long ownerId
    ) {
        log.debug("Request \"createItem\"is called.");
        return itemService.create(item, ownerId);
    }

    @PatchMapping(path = "/{itemId}")
    public ItemDto patchItem(
            @PathVariable(name = "itemId") long id,
            @RequestBody final ItemDto item,
            @RequestHeader("X-Sharer-User-Id") long ownerId
    ) {
        log.debug("Request \"patchItem\"is called.");
        return itemService.patch(id, item, ownerId);
    }

    @GetMapping
    public List<ItemBookingsDto> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") long ownerId) {
        log.debug("Request \"getItemsByOwnerId\"is called.");
        return itemService.getByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text,
                                     @RequestParam(defaultValue = "0") int from,
                                     @RequestParam(defaultValue = "10") int size) {
        log.debug("Request \"searchItems\"is called.");
        return itemService.search(text);
    }

    @GetMapping("/{itemId}")
    public ItemBookingsDto getItem(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @PathVariable(name = "itemId") long id) {
        log.debug("Request \"getItem\"is called.");
        return itemService.getById(id, ownerId);
    }

    @DeleteMapping("/{itemId}")
    public void removeItemById(@PathVariable(name = "itemId") long id) {
        log.debug("Request \"removeItemById\"is called.");
        itemService.removeById(id);
    }

}
