package ru.practicum.shareit.validator;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.exception.model.ValidationException;

import java.time.LocalDateTime;

public class BookingCreationDtoValidator {
    public static void validateBookingCreationDto(BookingDto bookingDto) {
        validateStartNotNull(bookingDto);
        validateEndNotNull(bookingDto);
        validateStartNotInThePast(bookingDto);
        validateEndNotInThePast(bookingDto);
        validateStartNotEqualsEnd(bookingDto);
        validateEndIsNotBeforeStart(bookingDto);
    }

    public static void validateStartNotNull(BookingDto bookingDto) {
        if (bookingDto.getStart() == null) {
            throw new ValidationException("Start равен null!");
        }
    }

    public static void validateEndNotNull(BookingDto bookingDto) {
        if (bookingDto.getEnd() == null) {
            throw new ValidationException("End равен null!");
        }
    }

    public static void validateStartNotInThePast(BookingDto bookingDto) {
        if (bookingDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start раньше текущего времени!");
        }
    }

    public static void validateEndNotInThePast(BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("End раньше текущего времени!");
        }
    }

    public static void validateStartNotEqualsEnd(BookingDto bookingDto) {
        if (bookingDto.getStart().isEqual(bookingDto.getEnd())) {
            throw new ValidationException("End раньше Start!");
        }
    }

    public static void validateEndIsNotBeforeStart(BookingDto bookingDto) {
        if (bookingDto.getEnd().isBefore(bookingDto.getStart())) {
            throw new ValidationException("End раньше Start!");
        }
    }
}
