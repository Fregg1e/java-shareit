package ru.practicum.shareit.item.mapper.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

@Component
public class ItemMapperImpl implements ItemMapper {
    @Override
    public ItemDto toItemDto(Item item) {
        return ItemDto.builder()
                .id(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .available(item.getAvailable())
                .requestId(getRequestIdOrNull(item))
                .build();
    }

    @Override
    public Item toItem(ItemDto itemDto, User owner) {
        return Item.builder()
                .id(itemDto.getId())
                .name(itemDto.getName())
                .description(itemDto.getDescription())
                .available(itemDto.getAvailable())
                .owner(owner)
                .build();
    }

    private Long getRequestIdOrNull(Item item) {
        if (item.getRequest() != null) {
            return item.getRequest().getId();
        }
        return null;
    }
}
