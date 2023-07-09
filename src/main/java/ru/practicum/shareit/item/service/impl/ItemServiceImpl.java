package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.AccessException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.item.storage.ItemStorage;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.validator.ItemDtoValidator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemStorage itemStorage;
    private final UserStorage userStorage;
    private final ItemMapper itemMapper;

    @Override
    public ItemDto getItemById(Long itemId) {
        return itemMapper.toItemDto(itemStorage.getItemById(itemId));
    }

    @Override
    public List<ItemDto> getItemsByUserIdOrGetAll(Long userId) {
        if (userId == null) {
            return itemStorage.getAll().stream()
                    .map(itemMapper::toItemDto)
                    .collect(Collectors.toList());
        }
        User user = userStorage.getById(userId);
        return itemStorage.getItemsByUserId(user.getId()).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemStorage.search(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = userStorage.getById(userId);
        ItemDtoValidator.validateItemDto(itemDto);
        Item item = itemMapper.toItem(itemDto, user.getId(), null);
        return itemMapper.toItemDto(itemStorage.create(item));
    }

    @Override
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        ItemDtoValidator.validateAllFieldNotNull(itemDto);
        User user = userStorage.getById(userId);
        ItemDto updatebleItemDto = getItemById(itemId);
        if (!itemStorage.getItemOwnerId(itemId).equals(userId)) {
            log.error("Произошло исключение! Отказано в доступе пользователь с ID = {} "
                    + "к вещи с ID = {}.", userId, itemId);
            throw new AccessException(String.format("Отказано в доступе пользователь с ID = %d "
                    + "к вещи с ID = %d.", userId, itemId));
        }
        if (itemDto.getName() != null) {
            ItemDtoValidator.validateName(itemDto);
            updatebleItemDto.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            ItemDtoValidator.validateDescription(itemDto);
            updatebleItemDto.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            ItemDtoValidator.validateAvailable(itemDto);
            updatebleItemDto.setAvailable(itemDto.getAvailable());
        }
        Item item = itemMapper.toItem(updatebleItemDto, user.getId(), null);
        log.debug("Вещь с id={} обновлена.", updatebleItemDto.getId());
        return itemMapper.toItemDto(itemStorage.update(itemId, item));
    }

    @Override
    public void delete(Long itemId, Long userId) {
        if (!itemStorage.getItemOwnerId(itemId).equals(userId)) {
            log.error("Произошло исключение! Отказано в доступе пользователь с ID = {} "
                    + "к вещи с ID = {}.", userId, itemId);
            throw new AccessException(String.format("Отказано в доступе пользователь с ID = %d "
                    + "к вещи с ID = %d.", userId, itemId));
        }
        itemStorage.delete(itemId, userId);
    }
}
