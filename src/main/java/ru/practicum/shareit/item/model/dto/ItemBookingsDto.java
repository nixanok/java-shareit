package ru.practicum.shareit.item.model.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.model.dto.BookingItemDto;
import ru.practicum.shareit.comment.model.dto.CommentDto;

import java.util.List;

@Data
@Builder
public class ItemBookingsDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookingItemDto lastBooking;

    private BookingItemDto nextBooking;

    private List<CommentDto> comments;
}
