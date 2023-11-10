package ru.practicum.shareit.booking.dto;

import java.time.LocalDateTime;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class BookItemRequestDto {
	private Long id;

	private Long itemId;

	@NotNull(message = "Start cannot be null", groups = BookingCreationInfo.class)
	@FutureOrPresent(message = "Start should be in future or present", groups = BookingCreationInfo.class)
	private LocalDateTime start;

	@NotNull(message = "End cannot be null", groups = BookingCreationInfo.class)
	@Future(message = "End should be in future", groups = BookingCreationInfo.class)
	private LocalDateTime end;
}

