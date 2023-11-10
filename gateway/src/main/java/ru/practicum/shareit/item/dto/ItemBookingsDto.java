package ru.practicum.shareit.item.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ItemBookingsDto {

    private Long id;

    private String name;

    private String description;

    private Boolean available;

    private BookItemRequestDto lastBooking;

    private BookItemRequestDto nextBooking;

    private List<CommentDto> comments;
}
