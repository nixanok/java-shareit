package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookItemRequestDto;
import ru.practicum.shareit.booking.dto.BookingCreationInfo;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;

import static ru.practicum.shareit.tool.Headers.USER_ID;

@Controller
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
@Slf4j
@Validated
public class BookingController {

	private final BookingClient bookingClient;

	@GetMapping
	public ResponseEntity<Object> getBookings(
			@Positive @RequestHeader(USER_ID) long userId,
			@RequestParam(name = "state", defaultValue = "all") String stateParam,
			@PositiveOrZero @RequestParam(name = "from", defaultValue = "0") Integer from,
			@Positive @RequestParam(name = "size", defaultValue = "10") Integer size) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.info("Get booking with state {}, userId={}, from={}, size={}", stateParam, userId, from, size);
		return bookingClient.getBookings(userId, state, from, size);
	}

	@PostMapping
	public ResponseEntity<Object> bookItem(
			@Positive @RequestHeader(USER_ID) long userId,
			@RequestBody @Validated(BookingCreationInfo.class) BookItemRequestDto requestDto) {
		log.info("Creating booking {}, userId={}", requestDto, userId);
		return bookingClient.createBooking(requestDto, userId);
	}

	@GetMapping("/{bookingId}")
	public ResponseEntity<Object> getBooking(
			@Positive @RequestHeader(USER_ID) long userId,
			@Positive @PathVariable Long bookingId) {
		log.info("Get booking {}, userId={}", bookingId, userId);
		return bookingClient.getBookingById(bookingId, userId);
	}

	@PatchMapping("/{bookingId}")
	public ResponseEntity<Object> updateBooking(@PathVariable int bookingId,
												@RequestHeader(USER_ID) int ownerId,
												@RequestParam Boolean approved) {
		log.info("Update booking {}.", bookingId);
		return bookingClient.updateBooking(bookingId, ownerId, approved);
	}

	@GetMapping("/owner")
	public ResponseEntity<Object> getBookingsItemsByUserId(
			@Positive @RequestHeader(USER_ID) long ownerId,
			@RequestParam(name = "state", defaultValue = "ALL") String stateParam,
			@RequestParam(name = "from", defaultValue = "0") int from,
			@RequestParam(name = "size", defaultValue = "10") int size
	) {
		BookingState state = BookingState.from(stateParam)
				.orElseThrow(() -> new IllegalArgumentException("Unknown state: " + stateParam));
		log.debug("Request \"getBookingsItemsByUserId\"is called.");
		return bookingClient.getBookingsOwner(ownerId, state, from, size);
	}
}
