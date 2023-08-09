package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.model.AccessException;
import ru.practicum.shareit.exception.model.NotAvailableException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.OffsetPageRequest;
import ru.practicum.shareit.validator.CommentDtoValidator;
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
    private final CommentRepository commentRepository;
    private final ItemRequestRepository itemRequestRepository;
    private final ItemMapper itemMapper;
    private final BookingMapper bookingMapper;
    private final CommentMapper commentMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getAll(Integer from, Integer size) {
        return itemRepository.findAll(new OffsetPageRequest(from, size))
                .map(itemMapper::toItemDto)
                .getContent();
    }

    @Override
    @Transactional(readOnly = true)
    public ItemDto getItemById(Long userId, Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ID = %d не существует.", itemId)));
        ItemDto itemDto = itemMapper.toItemDto(item);
        itemDto.setComments(commentRepository.findByItemId(item.getId()).stream()
                .map(commentMapper::toCommentDto)
                .collect(Collectors.toList()));
        if (item.getOwner().getId().equals(userId)) {
            setLastAndNextBooking(itemDto);
        }
        return itemDto;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemDto> getItemsByUserId(Long userId, Integer from, Integer size) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с ID = %d "
                        + "не существует.", userId)));
        List<ItemDto> itemDtos = itemRepository.findByOwnerId(user.getId(), new OffsetPageRequest(from, size)).stream()
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
    public List<ItemDto> search(String text, Integer from, Integer size) {
        if (text.isEmpty() || text.isBlank()) {
            return Collections.emptyList();
        }
        return itemRepository.search(text, new OffsetPageRequest(from, size)).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public ItemDto create(Long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с ID = %d "
                        + "не существует.", userId)));
        Item item = itemMapper.toItem(itemDto, user);
        if (itemDto.getRequestId() != null) {
            ItemRequest itemRequest = itemRequestRepository.findById(itemDto.getRequestId())
                    .orElseThrow(() -> new NotFoundException(String.format("Запроса с ID = %d "
                            + "не существует.", itemDto.getRequestId())));
            item.setRequest(itemRequest);
        }
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

    @Override
    @Transactional
    public CommentDto createComment(Long userId, Long itemId, CommentDto commentDto) {
        CommentDtoValidator.validateText(commentDto);
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ID = %d не существует.", itemId)));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с ID = %d "
                        + "не существует.", userId)));
        List<Booking> bookings = bookingRepository.findByBookerIdAndItemIdAndStatusAndEndIsBefore(user.getId(),
                item.getId(), BookingStatus.APPROVED, LocalDateTime.now());
        if (bookings.isEmpty()) {
            throw new NotAvailableException(String.format("Невозможно создать комментарий к вещи с ID = %d",
                    item.getId()));
        }
        Comment comment = commentMapper.toComment(commentDto, item, user, LocalDateTime.now());
        return commentMapper.toCommentDto(commentRepository.save(comment));
    }

    private void setLastAndNextBooking(ItemDto itemDto) {
        BookingDto lastBooking = null;
        Booking pastBookings = bookingRepository
                .findFirstByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(itemDto.getId(), BookingStatus.APPROVED,
                        LocalDateTime.now());
        if (pastBookings != null) {
            lastBooking = bookingMapper.toBookingForItemDto(pastBookings);
        }
        itemDto.setLastBooking(lastBooking);
        BookingDto nextBooking = null;
        Booking futureBooking = bookingRepository
                .findFirstByItemIdAndStatusAndStartIsAfterOrderByStartAsc(itemDto.getId(), BookingStatus.APPROVED,
                        LocalDateTime.now());
        if (futureBooking != null) {
            nextBooking = bookingMapper.toBookingForItemDto(futureBooking);
        }
        itemDto.setNextBooking(nextBooking);
    }
}
