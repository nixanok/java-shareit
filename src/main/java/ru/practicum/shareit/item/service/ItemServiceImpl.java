package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.tool.ItemMapper;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    @Autowired
    private final ItemRepository itemRepository;

    @Autowired
    private final UserRepository userRepository;

    @Override
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        if (!userRepository.contains(ownerId)) {
            throw new UserNotFoundException(ownerId);
        }
        Item item = ItemMapper.fromDto(itemDto);
        item.setOwnerId(ownerId);
        return itemRepository.add(item);
    }

    @Override
    public ItemDto patch(Long id, Item patchItem, Long ownerId) {
        Optional<Item> optionalItem = itemRepository.getById(id);
        if (optionalItem.isEmpty()) {
            throw new ItemNotFoundException(id);
        }
        Item item = optionalItem.get();
        if (!item.getOwnerId().equals(ownerId)) {
            throw new ItemNotFoundException(id);
        }
        if (patchItem.getName() != null) {
            item.setName(patchItem.getName());
        }
        if (patchItem.getDescription() != null) {
            item.setDescription(patchItem.getDescription());
        }
        if (patchItem.getAvailable() != null) {
            item.setAvailable(patchItem.getAvailable());
        }
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getAll() {
        return itemRepository.getAll()
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.getAll()
                .stream()
                .filter(item ->
                        (item.getName().toLowerCase().contains(text.toLowerCase()) ||
                        item.getDescription().toLowerCase().contains(text.toLowerCase())) &&
                        item.getAvailable())
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> getByOwnerId(Long ownerId) {
        return itemRepository.getByOwnerId(ownerId)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getById(Long id) {
        Optional<Item> item = itemRepository.getById(id);
        if (item.isEmpty()) {
            throw new RuntimeException();
        }
        return ItemMapper.toDto(item.get());
    }

    @Override
    public void removeById(Long id) {
        itemRepository.removeById(id);
    }

}
