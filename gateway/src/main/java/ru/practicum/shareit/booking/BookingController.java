package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingState;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/bookings")
@Validated
@RequiredArgsConstructor
public class BookingController {
    private final BookingClient client;

    @GetMapping
    public ResponseEntity<Object> getBookingsByUserId(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestParam(value = "state", defaultValue = "ALL") BookingState state,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size) {
        return client.getBookingsByUserId(userId, state, from, size);
    }

    @GetMapping("/{bookingId}")
    public ResponseEntity<Object> getBookingById(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @PathVariable("bookingId") Long bookingId) {
        return client.getBookingById(userId, bookingId);
    }

    @GetMapping("/owner")
    public ResponseEntity<Object> getBookingsByOwnerId(@RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
            @RequestParam(value = "state", defaultValue = "ALL") BookingState state,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size) {
        return client.getBookingsByOwnerId(ownerId, state, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @Valid @RequestBody BookingDto bookingDto) {
        return client.create(userId, bookingDto);
    }

    @PatchMapping("/{bookingId}")
    public ResponseEntity<Object> update(@PathVariable("bookingId") Long bookingId,
            @RequestHeader(value = "X-Sharer-User-Id") Long ownerId,
            @RequestParam(value = "approved", required = false) Boolean approved) {
        return client.update(bookingId, ownerId, approved);
    }
}
