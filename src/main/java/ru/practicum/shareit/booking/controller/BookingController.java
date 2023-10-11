package ru.practicum.shareit.booking.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.model.State;
import ru.practicum.shareit.booking.model.dto.BookingApproveDto;
import ru.practicum.shareit.booking.model.dto.BookingCreationInfo;
import ru.practicum.shareit.booking.model.dto.BookingCreationDto;
import ru.practicum.shareit.booking.model.dto.BookingSendingDto;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.booking.tool.BookingMapper;

import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/bookings")
@Slf4j
public class BookingController {

    @Autowired
    private final BookingService bookingService;

    @PostMapping
    public BookingSendingDto createBooking(
            @RequestBody @Validated(BookingCreationInfo.class) BookingCreationDto bookingCreationDto,
            @NotNull @RequestHeader("X-Sharer-User-Id") Long ownerId
    ) {
        log.debug("Request \"createBooking\"is called.");
        return bookingService.create(bookingCreationDto, ownerId);
    }

    @PatchMapping("/{bookingId}")
    public BookingApproveDto approveOrRejectBooking(
            @PathVariable(name = "bookingId") final Long bookingId,
            @NotNull @RequestParam(name = "approved") Boolean isApproved,
            @NotNull @RequestHeader("X-Sharer-User-Id") Long ownerId
    ) {
        log.debug("Request \"approveOrRejectBooking\"is called.");
        return BookingMapper.toApproveDto(bookingService.approve(bookingId, isApproved, ownerId));
    }

    @GetMapping("/{bookingId}")
    public BookingSendingDto getBooking(
            @PathVariable(name = "bookingId") final Long bookingId,
            @NotNull @RequestHeader("X-Sharer-User-Id") Long userId
    ) {
        log.debug("Request \"getBooking\"is called.");
        return bookingService.get(bookingId, userId);
    }

    @GetMapping
    public List<BookingSendingDto> getBookingsByBookerId(
        @NotNull @RequestHeader("X-Sharer-User-Id") Long bookerId,
        @RequestParam(name = "state", defaultValue = "ALL") State state
    ) {
        log.debug("Request \"getBookingsByUserId\"is called.");
        return bookingService.getBookingsByBookerId(bookerId, state);
    }

    @GetMapping("/owner")
    public List<BookingSendingDto> getBookingsItemsByUserId(
            @NotNull @RequestHeader("X-Sharer-User-Id") Long ownerId,
            @RequestParam(name = "state", defaultValue = "ALL") State state
    ) {
        log.debug("Request \"getBookingsItemsByUserId\"is called.");
        return bookingService.getBookingsItemsByUserId(ownerId, state);
    }

}
