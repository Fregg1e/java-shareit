package ru.practicum.shareit.item.service;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    List<ItemDto> getAll();

    ItemDto getItemById(Long userId, Long itemId);

    List<ItemDto> getItemsByUserId(Long userId);

    List<ItemDto> search(String text);

    ItemDto create(Long userId, ItemDto itemDto);

    ItemDto update(Long itemId, Long userId, ItemDto itemDto);

    void delete(Long itemId, Long userId);

    CommentDto createComment(Long userId, Long itemId,CommentDto commentDto);
}
