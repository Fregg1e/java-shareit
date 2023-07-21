package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingForItemDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.repository.BookingRepository;
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

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAll() {
        return itemRepository.findAll().stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ID = %d не существует.", itemId)));
        ItemDto itemDto = itemMapper.toItemDto(item);
        if (item.getOwner().getId().equals(userId)) {
            setLastAndNextBooking(itemDto);
        }
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с ID = %d "
                        + "не существует.", userId)));
        List<ItemDto> itemDtos = itemRepository.findByOwnerId(user.getId()).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
        for (ItemDto itemDto : itemDtos) {
            setLastAndNextBooking(itemDto);
        }
        itemDtos = itemDtos.stream().sorted((it1, it2) -> {
            if (it1.getNextBooking() == null) {
                return 1;
            }
            if (it2.getNextBooking() == null) {
                return -1;
            }
            return it1.getNextBooking().getStart().compareTo(it2.getNextBooking().getStart());
        }).collect(Collectors.toList());
        return itemDtos;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> search(String text) {
        if (text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        ItemDtoValidator.validateItemDto(itemDto);
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с ID = %d "
                        + "не существует.", userId)));
        Item item = itemMapper.toItem(itemDto, user);
        return itemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
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
    @Transactional
    public void delete(Long itemId, Long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ID = %d не существует.", itemId)));
        if (!item.getOwner().getId().equals(userId)) {
            throw new AccessException(String.format("Отказано в доступе пользователь с ID = %d "
                    + "к вещи с ID = %d.", userId, itemId));
        }
        itemRepository.deleteById(itemId);
    }

    private void setLastAndNextBooking(ItemDto itemDto) {
        BookingForItemDto lastBooking = null;
        List<Booking> pastBookings = bookingRepository.findByItemIdAndEndIsBeforeOrderByEndDesc(itemDto.getId(),
                LocalDateTime.now());
        if (!pastBookings.isEmpty()) {
            lastBooking = bookingMapper.toBookingForItemDto(pastBookings.get(0));
        }
        itemDto.setLastBooking(lastBooking);
        BookingForItemDto nextBooking = null;
        List<Booking> futureBookings = bookingRepository.findByItemIdAndStartIsAfterOrderByStartAsc(itemDto.getId(),
                LocalDateTime.now());
        if (!futureBookings.isEmpty()) {
            nextBooking = bookingMapper.toBookingForItemDto(futureBookings.get(0));
        }
        itemDto.setNextBooking(nextBooking);
    }
}
