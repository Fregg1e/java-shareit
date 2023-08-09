package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAll(Integer from, Integer size);

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getItemsByUserId(Long userId, Integer from, Integer size);

    List<ItemDto> search(String text, Integer from, Integer size);

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long itemId, Long userId, ItemDto itemDto);

    void delete(Long itemId, Long userId);

    CommentDto createComment(Long userId, Long itemId,CommentDto commentDto);
}
