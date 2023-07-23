package ru.practicum.shareit.booking.mapper;

import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.Booking;

public interface BookingMapper {
    BookingDto toBookingDto(Booking booking);

    Booking toBooking(BookingDto bookingDto);

    BookingDto toBookingForItemDto(Booking booking);
}
