package ru.practicum.shareit.validator;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.exception.model.ValidationException;

import java.time.LocalDateTime;

public class BookingCreationDtoValidator {
    public static void validateBookingCreationDto(BookingCreationDto bookingCreationDto) {
        validateStartNotNull(bookingCreationDto);
        validateEndNotNull(bookingCreationDto);
        validateStartNotInThePast(bookingCreationDto);
        validateEndNotInThePast(bookingCreationDto);
        validateStartNotEqualsEnd(bookingCreationDto);
        validateEndIsNotBeforeStart(bookingCreationDto);
    }

    public static void validateStartNotNull(BookingCreationDto bookingCreationDto) {
        if (bookingCreationDto.getStart() == null) {
            throw new ValidationException("Start равен null!");
        }
    }

    public static void validateEndNotNull(BookingCreationDto bookingCreationDto) {
        if (bookingCreationDto.getEnd() == null) {
            throw new ValidationException("End равен null!");
        }
    }

    public static void validateStartNotInThePast(BookingCreationDto bookingCreationDto) {
        if (bookingCreationDto.getStart().isBefore(LocalDateTime.now())) {
            throw new ValidationException("Start раньше текущего времени!");
        }
    }

    public static void validateEndNotInThePast(BookingCreationDto bookingCreationDto) {
        if (bookingCreationDto.getEnd().isBefore(LocalDateTime.now())) {
            throw new ValidationException("End раньше текущего времени!");
        }
    }

    public static void validateStartNotEqualsEnd(BookingCreationDto bookingCreationDto) {
        if (bookingCreationDto.getStart().isEqual(bookingCreationDto.getEnd())) {
            throw new ValidationException("End раньше Start!");
        }
    }

    public static void validateEndIsNotBeforeStart(BookingCreationDto bookingCreationDto) {
        if (bookingCreationDto.getEnd().isBefore(bookingCreationDto.getStart())) {
            throw new ValidationException("End раньше Start!");
        }
    }
}
