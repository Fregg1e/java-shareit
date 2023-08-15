package ru.practicum.shareit.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import javax.validation.ConstraintViolationException;

@Slf4j
@RestControllerAdvice
public class ErrorHandler {
    @ExceptionHandler({MethodArgumentTypeMismatchException.class, ConstraintViolationException.class})
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleValidationException(final RuntimeException e) {
        if (e instanceof MethodArgumentTypeMismatchException) {
            String message = "Unknown "
                    + ((MethodArgumentTypeMismatchException) e).getName()
                    + ": " + ((MethodArgumentTypeMismatchException) e).getValue();
            log.error("Произошло исключение! " + message);
            return new ErrorResponse(
                    message,
                    HttpStatus.BAD_REQUEST
            );
        }
        log.error("Произошло исключение!" + e.getMessage());
        return new ErrorResponse(
                e.getMessage(),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMethodArgumentNotValidException(final MethodArgumentNotValidException e) {
        log.error("Произошло исключение!" + e.getMessage());
        return new ErrorResponse(
                String.format("Ошибка с полем \"%s\".", e.getParameter()),
                HttpStatus.BAD_REQUEST
        );
    }

    @ExceptionHandler
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleThrowable(final Throwable e) {
        log.error("Произошла непредвиденная ошибка.");
        return new ErrorResponse(
                "Произошла непредвиденная ошибка.",
                HttpStatus.INTERNAL_SERVER_ERROR
        );
    }
}
