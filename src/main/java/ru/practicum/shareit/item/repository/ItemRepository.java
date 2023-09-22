package ru.practicum.shareit.item.repository;

import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;
import java.util.Optional;

public interface ItemRepository {

    ItemDto add(Item item);

    boolean contains(final Long id);

    Optional<Item> getById(final Long id);

    List<Item> getAll();

    List<Item> getByOwnerId(Long ownerId);

    void removeById(final Long id);

    void removeAll();

}
