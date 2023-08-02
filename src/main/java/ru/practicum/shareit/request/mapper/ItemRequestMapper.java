package ru.practicum.shareit.request.mapper;

import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

public interface ItemRequestMapper {
    ItemRequest toItemRequest(ItemRequestDto itemRequestDto);

    ItemRequestDto toItemRequestDto(ItemRequest itemRequest);
}
