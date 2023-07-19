package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.model.AccessException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.validator.ItemDtoValidator;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final ItemMapper itemMapper;

    @Override
    public List<ItemDto> getAll() {
        return itemRepository.findAll().stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto getItemById(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ID = %d не существует.", itemId)));
        return itemMapper.toItemDto(item);
    }

    @Override
    public List<ItemDto> getItemsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с ID = %d "
                        + "не существует.", userId)));
        return itemRepository.findByOwnerId(user.getId()).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<ItemDto> search(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    public ItemDto create(Long userId, ItemDto itemDto) {
        ItemDtoValidator.validateItemDto(itemDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с ID = %d "
                        + "не существует.", userId)));
        Item item = itemMapper.toItem(itemDto, user);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public ItemDto update(Long itemId, Long userId, ItemDto itemDto) {
        ItemDtoValidator.validateAllFieldNotNull(itemDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с ID = %d "
                        + "не существует.", userId)));
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ID = %d не существует.", itemId)));
        if (!item.getOwner().getId().equals(user.getId())) {
            throw new AccessException(String.format("Отказано в доступе пользователь с ID = %d "
                    + "к вещи с ID = %d.", userId, itemId));
        }
        if (itemDto.getName() != null) {
            ItemDtoValidator.validateName(itemDto);
            item.setName(itemDto.getName());
        }
        if (itemDto.getDescription() != null) {
            ItemDtoValidator.validateDescription(itemDto);
            item.setDescription(itemDto.getDescription());
        }
        if (itemDto.getAvailable() != null) {
            ItemDtoValidator.validateAvailable(itemDto);
            item.setAvailable(itemDto.getAvailable());
        }
        log.debug("Вещь с id={} обновлена.", item.getId());
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    public void delete(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ID = %d не существует.", itemId)));
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessException(String.format("Отказано в доступе пользователь с ID = %d "
                    + "к вещи с ID = %d.", userId, itemId));
        }
        itemRepository.deleteById(itemId);
    }
}
