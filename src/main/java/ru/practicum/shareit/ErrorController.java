package ru.practicum.shareit;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import ru.practicum.shareit.item.exception.ItemNotFoundException;
import ru.practicum.shareit.user.exception.EmailAlreadyExistException;
import ru.practicum.shareit.user.exception.IdUserNotFoundException;
import ru.practicum.shareit.user.exception.UserNotFoundException;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestControllerAdvice(basePackages = "ru.practicum.shareit")
@Slf4j
public final class ErrorController {

    @Data
    public static final class ErrorResponse {
        private final String error;
        private final int code;
        private final String description;
        private final LocalDateTime timestamp;
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public List<ErrorResponse> handleValidationDataError(final MethodArgumentNotValidException e) {
        List<FieldError> fieldErrors = e.getBindingResult().getFieldErrors();
        List<ErrorResponse> errorResponses = new ArrayList<>();
        for (FieldError fieldError : fieldErrors) {
            ErrorResponse errorResponse = new ErrorResponse(
                    "BAD_REQUEST",
                    400,
                    String.format("Bad argument : %s. %s", fieldError.getField(), fieldError.getDefaultMessage()),
                    LocalDateTime.now().withNano(0)
            );
            errorResponses.add(errorResponse);
        }
        log.warn(fieldErrors.toString());
        return errorResponses;
    }


    @ExceptionHandler({
            ItemNotFoundException.class,
            UserNotFoundException.class,
            IdUserNotFoundException.class,
    })
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleNotFoundError(final RuntimeException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("NOT_FOUND", 404, e.getMessage(), LocalDateTime.now().withNano(0));
    }

    @ExceptionHandler({
            EmailAlreadyExistException.class
    })
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleAlreadyExistError(final RuntimeException e) {
        log.warn(e.getMessage());
        return new ErrorResponse("CONFLICT", 409, e.getMessage(), LocalDateTime.now().withNano(0));
    }

}
