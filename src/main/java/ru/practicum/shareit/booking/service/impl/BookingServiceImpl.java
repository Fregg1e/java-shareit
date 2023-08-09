package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.exception.model.NotAvailableException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.OffsetPageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final BookingMapper bookingMapper;

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByUserId(Long userId, BookingState state, Integer from, Integer size) {
        if (state == null) {
            state = BookingState.ALL;
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с ID = %d "
                        + "не существует.", userId)));
        switch (state) {
            case CURRENT:
                return bookingRepository.findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(user.getId(),
                                LocalDateTime.now(), LocalDateTime.now(), new OffsetPageRequest(from, size)).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByBookerIdAndEndIsBeforeOrderByStartDesc(user.getId(),
                                LocalDateTime.now(), new OffsetPageRequest(from, size)).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByBookerIdAndStartIsAfterOrderByStartDesc(user.getId(),
                                LocalDateTime.now(), new OffsetPageRequest(from, size)).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(user.getId(),
                                BookingStatus.WAITING, new OffsetPageRequest(from, size)).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByBookerIdAndStatusOrderByStartDesc(user.getId(),
                                BookingStatus.REJECTED, new OffsetPageRequest(from, size)).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findByBookerIdOrderByStartDesc(user.getId(),
                                new OffsetPageRequest(from, size)).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional(readOnly = true)
    public BookingDto getBookingById(Long userId, Long bookingId) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с ID = %d "
                        + "не существует.", bookingId)));
        if (!booking.getBooker().getId().equals(userId)
                && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new NotFoundException(String.format(String.format("Бронирование с ID = %d "
                    + "не существует.", bookingId)));
        }
        return bookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional(readOnly = true)
    public List<BookingDto> getBookingsByOwnerId(Long ownerId, BookingState state, Integer from, Integer size) {
        if (state == null) {
            state = BookingState.ALL;
        }
        User user = userRepository.findById(ownerId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с ID = %d "
                        + "не существует.", ownerId)));
        switch (state) {
            case CURRENT:
                return bookingRepository.findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(user.getId(),
                                LocalDateTime.now(), LocalDateTime.now(), new OffsetPageRequest(from, size)).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case PAST:
                return bookingRepository.findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(user.getId(),
                                LocalDateTime.now(), new OffsetPageRequest(from, size)).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case FUTURE:
                return bookingRepository.findByItemOwnerIdAndStartIsAfterOrderByStartDesc(user.getId(),
                                LocalDateTime.now(), new OffsetPageRequest(from, size)).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case WAITING:
                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(user.getId(),
                                BookingStatus.WAITING, new OffsetPageRequest(from, size)).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            case REJECTED:
                return bookingRepository.findByItemOwnerIdAndStatusOrderByStartDesc(user.getId(),
                                BookingStatus.REJECTED, new OffsetPageRequest(from, size)).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
            default:
                return bookingRepository.findByItemOwnerIdOrderByStartDesc(user.getId(),
                                new OffsetPageRequest(from, size)).stream()
                        .map(bookingMapper::toBookingDto)
                        .collect(Collectors.toList());
        }
    }

    @Override
    @Transactional
    public BookingDto create(Long userId, BookingDto bookingDto) {
        Booking booking = bookingMapper.toBooking(bookingDto);
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new NotFoundException(String.format("Вещь с ID = %d не существует.",
                        bookingDto.getItemId())));
        if (!item.getAvailable()) {
            throw new NotAvailableException("Вещь не доступна для бронирования!");
        }
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с ID = %d "
                        + "не существует.", userId)));
        if (item.getOwner().getId().equals(user.getId())) {
            throw new NotFoundException(String.format("Вещь с ID = %d не существует.",
                    bookingDto.getItemId()));
        }
        booking.setItem(item);
        booking.setBooker(user);
        booking.setStatus(BookingStatus.WAITING);
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDto update(Long bookingId, Long ownerId, Boolean approved) {
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new NotFoundException(String.format("Бронирование с ID = %d "
                + "не существует.", bookingId)));
        if (!booking.getItem().getOwner().getId().equals(ownerId) && booking.getBooker().getId().equals(ownerId)) {
            throw new NotFoundException(String.format(String.format("Бронирование с ID = %d "
                    + "не существует.", bookingId)));
        }
        if (!booking.getItem().getOwner().getId().equals(ownerId)) {
            throw new NotAvailableException(String.format("Бронирование с ID = %d "
                    + "не доступно.", bookingId));
        }
        if (booking.getStatus().equals(BookingStatus.APPROVED) && approved) {
            throw new NotAvailableException(String.format("Бронирование с ID = %d уже подтверждено!", bookingId));
        }
        if (approved) {
            booking.setStatus(BookingStatus.APPROVED);
        } else {
            booking.setStatus(BookingStatus.REJECTED);
        }
        return bookingMapper.toBookingDto(bookingRepository.save(booking));
    }
}
