package ru.practicum.shareit.error;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.booking.exception.BookingNotFoundException;
import ru.practicum.shareit.booking.exception.BookingStatusException;
import ru.practicum.shareit.booking.exception.BookingTimeException;
import ru.practicum.shareit.booking.exception.OwnerNotFoundException;
import ru.practicum.shareit.comment.exception.CommentException;
import ru.practicum.shareit.item.exception.ItemNotAvailableException;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.request.exception.PaginationParamException;
import ru.practicum.shareit.request.exception.RequestNotFoundException;
import ru.practicum.shareit.user.exception.EmailAlreadyExistsException;
import ru.practicum.shareit.user.exception.IdUserNotFoundException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDateTime;

@RestControllerAdvice(basePackages = "ru.practicum.shareit")
@Slf4j
public final class ErrorController {

    @ExceptionHandler({
            ItemNotFoundException.class,
            UserNotFoundException.class,
            IdUserNotFoundException.class,
            BookingNotFoundException.class,
            OwnerNotFoundException.class,
            RequestNotFoundException.class
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundError(final RuntimeException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("NOT_FOUND", 404, e.getMessage(), LocalDateTime.now().withNano(0));
    }

    @ExceptionHandler({ EmailAlreadyExistsException.class })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistError(final RuntimeException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("CONFLICT", 409, e.getMessage(), LocalDateTime.now().withNano(0));
    }

    @ExceptionHandler({
            BookingTimeException.class,
            ItemNotAvailableException.class,
            BookingStatusException.class,
            CommentException.class,
            PaginationParamException.class
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleBadRequest(final RuntimeException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("BAD_REQUEST", 400, e.getMessage(), LocalDateTime.now().withNano(0));
    }

}
