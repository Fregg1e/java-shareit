package ru.practicum.shareit.request.mapper.impl;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class ItemRequestMapperImplTest {
    private final ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();

    @Test
    void toItemRequestTest() {
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().description("test description").build();

        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);

        assertEquals(itemRequestDto.getDescription(), itemRequest.getDescription());
    }

    @Test
    void toItemRequestDtoTest() {
        LocalDateTime created = LocalDateTime.now();
        ItemRequest itemRequest = ItemRequest.builder().id(1L).description("test description")
                .created(created).build();

        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);

        assertEquals(itemRequest.getId(), itemRequestDto.getId());
        assertEquals(itemRequest.getDescription(), itemRequestDto.getDescription());
        assertEquals(itemRequest.getCreated(), itemRequestDto.getCreated());
    }
}