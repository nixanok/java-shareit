package ru.practicum.shareit.booking.model;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class Booking {

    private Long id;

    private LocalDateTime start;

    private LocalDateTime end;

    private Long itemId;

    private Long bookerId;

    private Status status;

    public enum Status {WAITING, APPROVED, REJECTED, CANCELED}

}
