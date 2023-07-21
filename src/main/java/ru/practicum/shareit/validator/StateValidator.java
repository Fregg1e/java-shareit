package ru.practicum.shareit.validator;

import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.exception.model.ValidationException;

public class StateValidator {
    public static BookingState validateBookingState(String state) {
        try {
            return BookingState.valueOf(state);
        } catch (IllegalArgumentException e) {
            throw new ValidationException(String.format("Unknown state: %s", state));
        }
    }
}
