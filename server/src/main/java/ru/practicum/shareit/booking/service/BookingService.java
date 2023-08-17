package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;

import java.util.List;

public interface BookingService {
    List<BookingDto> getBookingsByUserId(Long userId, BookingState state, Integer from, Integer size);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getBookingsByOwnerId(Long ownerId, BookingState state, Integer from, Integer size);

    BookingDto create(Long userId, BookingDto bookingDto);

    BookingDto update(Long bookingId, Long ownerId, Boolean approved);
}
