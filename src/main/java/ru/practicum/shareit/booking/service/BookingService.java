package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    List<BookingDto> getBookingsByUserId(Long userId, BookingState state);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getBookingsByOwnerId(Long ownerId, BookingState state);

    BookingDto create(Long userId, BookingDto bookingDto);

    BookingDto update(Long bookingId, Long ownerId, Boolean approved);
}
