package ru.practicum.shareit.item.mapper.impl;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class ItemMapperImplTest {
    private final ItemMapper itemMapper = new ItemMapperImpl();

    @Test
    void toItemDtoTest_whenRequestNull_thenItemDtoWithRequestIdNull() {
        Item item = Item.builder().id(1L).name("test").description("test description").available(true).build();

        ItemDto itemDto = itemMapper.toItemDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertNull(itemDto.getRequestId());
    }

    @Test
    void toItemDtoTest_whenRequestNotNull_thenItemDtoWithRequestIdNotNull() {
        ItemRequest itemRequest = ItemRequest.builder().id(1L).build();
        Item item = Item.builder().id(1L).name("test").description("test description").available(true)
                .request(itemRequest).build();

        ItemDto itemDto = itemMapper.toItemDto(item);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getRequest().getId(), itemDto.getRequestId());
    }

    @Test
    void toItemTest() {
        User owner = User.builder().id(1L).email("test@test.test").name("test").build();
        ItemDto itemDto = ItemDto.builder().id(1L).name("test")
                .description("test description").available(true).build();

        Item item = itemMapper.toItem(itemDto, owner);

        assertEquals(item.getId(), itemDto.getId());
        assertEquals(item.getName(), itemDto.getName());
        assertEquals(item.getDescription(), itemDto.getDescription());
        assertEquals(item.getAvailable(), itemDto.getAvailable());
        assertEquals(item.getOwner().getId(), owner.getId());
    }
}