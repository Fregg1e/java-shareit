package ru.practicum.shareit.booking.service;

import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;

import java.util.List;

public interface BookingService {
    List<BookingDto> getBookingsByUserId(Long userId, String state);

    BookingDto getBookingById(Long userId, Long bookingId);

    List<BookingDto> getBookingsByOwnerId(Long ownerId, String state);

    BookingDto create(Long userId, BookingCreationDto bookingCreationDto);

    BookingDto update(Long bookingId, Long ownerId, Boolean approved);
}
