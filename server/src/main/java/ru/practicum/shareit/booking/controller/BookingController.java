package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.dto.BookingApproveDto;
import ru.practicum.shareit.booking.model.dto.BookingCreationDto;
import ru.practicum.shareit.booking.model.dto.BookingSendingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.tool.BookingMapper;

import java.util.List;

@RestController
@RequiredArgsConstructor(onConstructor = @__(@Autowired))
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    private final BookingService bookingService;

    @PostMapping
    public BookingSendingDto createBooking(
            @RequestBody BookingCreationDto bookingCreationDto,
            @RequestHeader("X-Sharer-User-Id") long ownerId
    ) {
        log.debug("Request \"createBooking\"is called.");
        return bookingService.create(bookingCreationDto, ownerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingApproveDto approveOrRejectBooking(
            @PathVariable(name = "bookingId") long bookingId,
            @RequestParam(name = "approved") Boolean isApproved,
            @RequestHeader("X-Sharer-User-Id") long ownerId
    ) {
        log.debug("Request \"approveOrRejectBooking\"is called.");
        return BookingMapper.toApproveDto(bookingService.approve(bookingId, isApproved, ownerId));
    }

    @GetMapping("/{bookingId}")
    public BookingSendingDto getBooking(
            @PathVariable(name = "bookingId") long bookingId,
            @RequestHeader("X-Sharer-User-Id") long userId
    ) {
        log.debug("Request \"getBooking\"is called.");
        return bookingService.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingSendingDto> getBookingsByBookerId(
            @RequestHeader("X-Sharer-User-Id") long bookerId,
        @RequestParam(name = "state", defaultValue = "ALL") State state,
        @RequestParam(name = "from", defaultValue = "0") int from,
        @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.debug("Request \"getBookingsByUserId\"is called.");
        return bookingService.getBookingsByBookerId(bookerId, state, from, size);
    }

    @GetMapping("/owner")
    public List<BookingSendingDto> getBookingsItemsByUserId(
            @RequestHeader("X-Sharer-User-Id") long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") State state,
            @RequestParam(name = "from", defaultValue = "0") int from,
            @RequestParam(name = "size", defaultValue = "10") int size
    ) {
        log.debug("Request \"getBookingsItemsByUserId\"is called.");
        return bookingService.getBookingsItemsByUserId(ownerId, state, from, size);
    }

}
