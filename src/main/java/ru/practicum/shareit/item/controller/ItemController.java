package ru.practicum.shareit.item.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.BasicInfo;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.service.ItemService;

import javax.validation.constraints.NotNull;
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
            @RequestBody @Validated(BasicInfo.class) final ItemDto item,
            @NotNull @RequestHeader("X-Sharer-User-Id") Long ownerId
    ) {
        log.debug("Request \"createItem\"is called.");
        return itemService.create(item, ownerId);
    }

    @PatchMapping(path = "/{itemId}")
    public ItemDto patchItem(
            @PathVariable(name = "itemId") final Long id,
                          @RequestBody final ItemDto item,
                          @NotNull @RequestHeader("X-Sharer-User-Id") Long ownerId
    ) {
        log.debug("Request \"patchItem\"is called.");
        return itemService.patch(id, item, ownerId);
    }

    @GetMapping
    public List<ItemDto> getItemsByOwnerId(@RequestHeader("X-Sharer-User-Id") @NotNull Long ownerId) {
        log.debug("Request \"getItemsByOwnerId\"is called.");
        return itemService.getByOwnerId(ownerId);
    }

    @GetMapping("/search")
    public List<ItemDto> searchItems(@RequestParam String text) {
        log.debug("Request \"searchItems\"is called.");
        return itemService.search(text);
    }

    @GetMapping("/{itemId}")
    public ItemDto getItem(@PathVariable(name = "itemId") Long id) {
        log.debug("Request \"getItem\"is called.");
        return itemService.getById(id);
    }

    @DeleteMapping("/{itemId}")
    public void removeItemById(@PathVariable(name = "itemId") Long id) {
        log.debug("Request \"removeItemById\"is called.");
        itemService.removeById(id);
    }

}
