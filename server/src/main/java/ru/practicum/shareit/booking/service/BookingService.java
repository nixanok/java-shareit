package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.dto.BookingCreationDto;
import ru.practicum.shareit.booking.model.dto.BookingSendingDto;

import java.util.List;

public interface BookingService {

    BookingSendingDto create(BookingCreationDto item, Long ownerId);

    List<BookingCreationDto> getAll();

    BookingCreationDto getById(Long id);

    void removeById(Long id);

    Booking approve(Long bookingId, Boolean isApproved, Long ownerId);

    BookingSendingDto get(Long bookingId, Long userId);

    List<BookingSendingDto> getBookingsByBookerId(Long ownerId, State state, int from, int size);

    List<BookingSendingDto> getBookingsItemsByUserId(Long userId, State state, int from, int size);
}
