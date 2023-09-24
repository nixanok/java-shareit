package ru.practicum.shareit.item.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.tool.ItemMapper;

import java.util.*;
import java.util.stream.Collectors;

@Repository
public class ItemRepositoryImpl implements ItemRepository {

    private final Map<Long, Item> items = new HashMap<>();

    private long nextId = 1;

    @Override
    public ItemDto add(Item item) {
        item.setId(nextId++);
        items.put(item.getId(), item);
        return ItemMapper.toDto(item);
    }

    @Override
    public boolean contains(Long id) {
        return items.containsKey(id);
    }

    @Override
    public Optional<Item> getById(Long id) {
        return items.containsKey(id) ? Optional.of(items.get(id)) : Optional.empty();
    }

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public List<Item> searchByNameAndDescription(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return items.values()
                .stream()
                .filter(item -> item.getName().toLowerCase().contains((text.toLowerCase())) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase()) &&
                        item.getAvailable())
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> getByOwnerId(Long ownerId) {
        return items.values().stream()
                .filter(item -> item.getOwnerId().equals(ownerId))
                .collect(Collectors.toList());
    }

    @Override
    public void removeById(Long id) {
        if (!items.containsKey(id)) {
            throw new RuntimeException();
        }
        items.remove(id);
    }

    @Override
    public void removeAll() {
        items.clear();
    }

}
