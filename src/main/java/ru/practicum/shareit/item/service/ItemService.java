package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.model.dto.ItemBookingsDto;
import ru.practicum.shareit.item.model.dto.ItemDto;

import java.util.List;

public interface ItemService {

    ItemDto create(ItemDto item, Long ownerId);

    ItemDto patch(Long id, ItemDto item, Long ownerId);

    List<ItemDto> getAll();

    List<ItemDto> search(String text);

    ItemBookingsDto getById(Long id, Long ownerId);

    List<ItemBookingsDto> getByOwnerId(Long ownerId);

    void removeById(Long id);

}
