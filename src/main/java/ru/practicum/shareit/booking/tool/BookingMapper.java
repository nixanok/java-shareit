package ru.practicum.shareit.booking.tool;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.dto.BookingApproveDto;
import ru.practicum.shareit.booking.model.dto.BookingCreationDto;
import ru.practicum.shareit.booking.model.dto.BookingItemDto;
import ru.practicum.shareit.booking.model.dto.BookingSendingDto;
import ru.practicum.shareit.item.tool.ItemMapper;
import ru.practicum.shareit.user.tool.UserMapper;

public final class BookingMapper {

    private BookingMapper() {
    }

    public static BookingCreationDto toCreationDto(final Booking booking) {
        return BookingCreationDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .itemId(booking.getItem().getId())
                .build();
    }

    public static Booking fromCreationDto(final BookingCreationDto bookingCreationDto) {
        return Booking.builder()
                .id(bookingCreationDto.getId())
                .start(bookingCreationDto.getStart())
                .end(bookingCreationDto.getEnd())
                .build();
    }

    public static BookingSendingDto toSendingDto(final Booking booking) {
        return booking == null ? null : BookingSendingDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .item(ItemMapper.toDto(booking.getItem()))
                .booker(UserMapper.toDto(booking.getBooker()))
                .status(booking.getStatus())
                .build();
    }

    public static BookingApproveDto toApproveDto(final Booking booking) {
        return BookingApproveDto.builder()
                .id(booking.getId())
                .start(booking.getStart())
                .end(booking.getEnd())
                .booker(UserMapper.toDto(booking.getBooker()))
                .status(booking.getStatus())
                .item(ItemMapper.toDto(booking.getItem()))
                .build();
    }

    public static BookingItemDto toBookingItemDto(final Booking booking) {
        return booking == null ? null : BookingItemDto.builder()
                .id(booking.getId())
                .bookerId(booking.getBooker().getId())
                .build();
    }

}
