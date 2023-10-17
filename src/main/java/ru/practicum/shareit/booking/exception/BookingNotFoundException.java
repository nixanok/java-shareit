package ru.practicum.shareit.booking.exception;

public class BookingNotFoundException extends RuntimeException {
    public BookingNotFoundException(long id) {
        super(String.format("Booking with id = %s not found.", id));
    }
}
