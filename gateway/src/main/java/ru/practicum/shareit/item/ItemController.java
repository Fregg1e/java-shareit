package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.PositiveOrZero;

@RestController
@RequestMapping("/items")
@Validated
@RequiredArgsConstructor
public class ItemController {
    private final ItemClient client;

    @GetMapping("/all")
    public ResponseEntity<Object> getAll(@RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size) {
        return client.getAll(from, size);
    }

    @GetMapping("/{itemId}")
    public ResponseEntity<Object> getItemById(@RequestHeader(value = "X-Sharer-User-Id", required = false) Long userId,
            @PathVariable("itemId") Long itemId) {
        return client.getItemById(userId, itemId);
    }

    @GetMapping
    public ResponseEntity<Object> getItemsByUserId(@RequestHeader(value = "X-Sharer-User-Id") Long userId,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size) {
        return client.getItemsByUserId(userId, from, size);
    }

    @GetMapping("/search")
    public ResponseEntity<Object> search(@RequestParam("text") String text,
            @RequestParam(value = "from", defaultValue = "0") @PositiveOrZero Integer from,
            @RequestParam(value = "size", defaultValue = "20") @Min(1) Integer size) {
        return client.search(text, from, size);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestHeader("X-Sharer-User-Id") Long userId,
                          @Valid @RequestBody ItemDto itemDto) {
        return client.create(userId, itemDto);
    }

    @PatchMapping("/{itemId}")
    public ResponseEntity<Object> update(@PathVariable("itemId") Long itemId,
                          @RequestHeader("X-Sharer-User-Id") Long userId,
                          @RequestBody ItemDto itemDto) {
        return client.update(itemId, userId, itemDto);
    }

    @DeleteMapping("/{itemId}")
    public void delete(@PathVariable("itemId") Long itemId,
                       @RequestHeader("X-Sharer-User-Id") Long userId) {
        client.delete(itemId, userId);
    }

    @PostMapping("/{itemId}/comment")
    public ResponseEntity<Object> createComment(@RequestHeader("X-Sharer-User-Id") Long userId,
            @PathVariable("itemId") Long itemId, @RequestBody CommentDto commentDto) {
        return client.createComment(userId, itemId, commentDto);
    }
}
