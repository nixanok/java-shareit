package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto item, Long ownerId);

    ItemDto patch(Long id, Item item, Long ownerId);

    List<ItemDto> getAll();

    List<ItemDto> search(String text);

    ItemDto getById(Long id);

    List<ItemDto> getByOwnerId(Long ownerId);

    void removeById(Long id);

}
