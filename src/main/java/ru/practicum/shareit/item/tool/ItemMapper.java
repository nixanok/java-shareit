package ru.practicum.shareit.item.tool;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.tool.BookingMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.tool.CommentMapper;
import ru.practicum.shareit.item.model.dto.ItemBookingsDto;
import ru.practicum.shareit.item.model.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.model.dto.ItemRequestDto;

import java.util.List;
import java.util.stream.Collectors;

public final class ItemMapper {

    private ItemMapper() {
    }

    public static ItemDto toDto(final Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                .build();
    }

    public static Item fromDto(final ItemDto itemDto) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .build();
    }

    public static ItemBookingsDto toBookingsDto(final Item item, Booking last, Booking next) {
        return ItemBookingsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(BookingMapper.toBookingItemDto(last))
                .nextBooking(BookingMapper.toBookingItemDto(next))
                .build();
    }

    public static ItemBookingsDto toBookingsDto(final Item item, Booking last, Booking next, List<Comment> comments) {
        return ItemBookingsDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .lastBooking(BookingMapper.toBookingItemDto(last))
                .nextBooking(BookingMapper.toBookingItemDto(next))
                .comments(comments
                        .stream()
                        .map(CommentMapper::toDto)
                        .collect(Collectors.toList()))
                .build();
    }

    public static ItemRequestDto toItemRequestDto(final Item item) {
        return ItemRequestDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(item.getRequest() == null ? null : item.getRequest().getId())
                .build();
    }


}
