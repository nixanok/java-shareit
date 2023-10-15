package ru.practicum.shareit.item.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.Status;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.item.model.dto.ItemBookingsDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.tool.ItemMapper;
import ru.practicum.shareit.user.exception.UserNotFoundException;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {

    private final ItemRepository itemRepository;

    private final UserRepository userRepository;

    private final CommentRepository commentRepository;

    private final BookingRepository bookingRepository;

    @Override
    @Transactional
    public ItemDto create(ItemDto itemDto, Long ownerId) {
        Optional<User> optionalUser = userRepository.findById(ownerId);
        if (optionalUser.isEmpty()) {
            throw new UserNotFoundException(ownerId);
        }
        User user = optionalUser.get();
        Item item = ItemMapper.fromDto(itemDto);
        item.setOwner(user);
        return ItemMapper.toDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto patch(Long id, ItemDto patchItem, Long ownerId) {
        Optional<Item> optionalItem = itemRepository.findById(id);
        if (optionalItem.isEmpty()) {
            throw new ItemNotFoundException(id);
        }
        Item item = optionalItem.get();
        if (!item.getOwner().getId().equals(ownerId)) {
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
        itemRepository.save(item);
        return ItemMapper.toDto(item);
    }

    @Override
    public List<ItemDto> getAll() {
        return itemRepository.findAll()
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.findAllByNameAndDescription(text)
                .stream()
                .map(ItemMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemBookingsDto> getByOwnerId(Long ownerId) {
        List<Item> items = itemRepository.findAllByOwnerId(ownerId);
        List<Long> idList = items
                .stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Map<Long, Booking> beforeBookings = bookingRepository.findLatestBookingsForItems(idList)
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));

        Map<Long, Booking> afterBookings = bookingRepository.findFutureBookingsForItems(idList)
                .stream()
                .collect(Collectors.toMap(booking -> booking.getItem().getId(), booking -> booking));

        return items
                .stream()
                .map(item -> ItemMapper.toBookingsDto(
                        item,
                        beforeBookings.get(item.getId()),
                        afterBookings.get(item.getId())))
                .sorted(Comparator.comparing(ItemBookingsDto::getId))
                .collect(Collectors.toList());

//        return itemRepository.findAllByOwnerId(ownerId)
//                .stream()
//                .map(item -> ItemMapper.toBookingsDto(item,
//                        bookingRepository.findFirstByItemIdAndStartIsBeforeAndStatus(
//                                    item.getId(),
//                                    LocalDateTime.now(),
//                                    Status.APPROVED,
//                                    Sort.by(Sort.Direction.DESC, "start")),
//                        bookingRepository.findFirstByItemIdAndStartIsAfterAndStatus(
//                                    item.getId(),
//                                    LocalDateTime.now(),
//                                    Status.APPROVED,
//                                    Sort.by(Sort.Direction.ASC, "start")),
//                        getComments(item.getId()))
//                )
//                .sorted(Comparator.comparing(ItemBookingsDto::getId))
//                .collect(Collectors.toList());
    }

    @Override
    public ItemBookingsDto getById(Long id, Long ownerId) {
        Optional<Item> item = itemRepository.findById(id);
        if (item.isEmpty()) {
            throw new ItemNotFoundException(id);
        }
        if (item.get().getOwner().getId().equals(ownerId)) {
            return ItemMapper.toBookingsDto(item.get(),
                    bookingRepository.findFirstByItemIdAndStartIsBeforeAndStatus(
                            id,
                            LocalDateTime.now(),
                            Status.APPROVED,
                            Sort.by(Sort.Direction.DESC, "start")),
                    bookingRepository.findFirstByItemIdAndStartIsAfterAndStatus(
                            id,
                            LocalDateTime.now(),
                            Status.APPROVED,
                            Sort.by(Sort.Direction.ASC, "start")),
                    getComments(item.get().getId()));
        } else {
            return ItemMapper.toBookingsDto(item.get(),
                   null,
                    null,
                    getComments(item.get().getId()));
        }

    }

    private List<Comment> getComments(Long itemId) {
        return commentRepository.findAllByItemIdOrderByCreated(itemId);
    }

    @Override
    @Transactional
    public void removeById(Long id) {
        itemRepository.deleteById(id);
    }

}
