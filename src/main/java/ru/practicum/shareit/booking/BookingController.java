package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingCreationDto;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.service.BookingService;

import java.util.List;

@RestController
@RequestMapping(path = "/bookings")
@RequiredArgsConstructor
public class BookingController {
    private final BookingService bookingService;

    @GetMapping
    public List<BookingDto> getBookingsByUserId(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", required = false) BookingState state) {
        return bookingService.getBookingsByUserId(userId, state);
    }

    @GetMapping("/{bookingId}")
    public BookingDto getBookingById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @PathVariable("bookingId") Long bookingId) {
        return bookingService.getBookingById(userId, bookingId);
    }

    @GetMapping("/owner")
    public List<BookingDto> getBookingsByOwnerId(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
            @RequestParam(value = "state", required = false) BookingState state) {
        return bookingService.getBookingsByOwnerId(ownerId, state);
    }

    @PostMapping
    public BookingDto create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestBody BookingCreationDto bookingCreationDto) {
        return bookingService.create(userId, bookingCreationDto);
    }

    @PatchMapping("/{bookingId}")
    public BookingDto update(@PathVariable("bookingId") Long bookingId,
            @RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
            @RequestParam(value = "approved", required = false) Boolean approved) {
        return bookingService.update(bookingId, ownerId, approved);
    }
}
