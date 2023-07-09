package ru.practicum.shareit.item.service;

import ru.practicum.shareit.item.dto.ItemDto;

import java.util.List;

public interface ItemService {
    ItemDto getItemById(Long itemId);
    List<ItemDto> getItemsByUserIdOrGetAll(Long userId);
    List<ItemDto> search(String text);
    ItemDto create(Long userId, ItemDto itemDto);
    ItemDto update(Long itemId, Long userId, ItemDto itemDto);
    void delete(Long itemId, Long userId);
}
