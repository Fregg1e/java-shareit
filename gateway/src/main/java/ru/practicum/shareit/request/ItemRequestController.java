package ru.practicum.shareit.request;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.request.dto.ItemRequestDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping(path = "/requests")
@RequiredArgsConstructor
@Validated
public class ItemRequestController {
    private final ItemRequestClient client;

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestBody @Valid ItemRequestDto itemRequestDto) {
        return client.create(userId, itemRequestDto);
    }

    @GetMapping
    public ResponseEntity<Object> getRequestsByUserId(@RequestHeader("X-Sharer-User-Id") Long userId) {
        return client.getRequestsByUserId(userId);
    }

    @GetMapping("/all")
    public ResponseEntity<Object> getAllRequests(@RequestHeader("X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size) {
        return client.getAllRequests(userId, from, size);
    }

    @GetMapping("/{requestId}")
    public ResponseEntity<Object> getRequestById(@RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("requestId") Long requestId) {
        return client.getRequestById(userId, requestId);
    }
}
