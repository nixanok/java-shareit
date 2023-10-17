package ru.practicum.shareit.booking.model.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Future;
import javax.validation.constraints.FutureOrPresent;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BookingCreationDto {

    private Long id;

    private Long itemId;

    @NotNull(message = "Start cannot be null", groups = BookingCreationInfo.class)
    @FutureOrPresent(message = "Start should be in future or present", groups = BookingCreationInfo.class)
    private LocalDateTime start;

    @NotNull(message = "End cannot be null", groups = BookingCreationInfo.class)
    @Future(message = "End should be in future", groups = BookingCreationInfo.class)
    private LocalDateTime end;
}
