package ru.practicum.shareit.booking.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.impl.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.exception.model.NotAvailableException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.impl.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.impl.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class BookingServiceImplTest {
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private ItemRepository itemRepository;
    private final ItemMapper itemMapper = new ItemMapperImpl();
    private final UserMapper userMapper = new UserMapperImpl();
    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl(itemMapper, userMapper);
    @InjectMocks
    private BookingServiceImpl bookingService;

    @Test
    void getBookingsByUserIdTest_whenUserNotExist_thenNotFoundException() {
        Long userId = 1L;
        BookingState state = BookingState.ALL;
        Integer from = 0;
        Integer size = 2;
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingsByUserId(userId, state, from, size));
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    void getBookingsByUserIdTest_whenStateIsCurrent_thenReturnedCurrentBookings() {
        Long userId = 1L;
        BookingState state = BookingState.CURRENT;
        Integer from = 0;
        Integer size = 2;
        User user = User.builder().id(1L).email("test@test.test").name("test").build();
        Item item = Item.builder().id(1L).name("test").description("test description").build();
        Booking booking = Booking.builder().id(1L).booker(user).item(item).build();
        List<Booking> bookings = List.of(booking);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingDto> bookingsDto = bookingService.getBookingsByUserId(userId, state, from, size);

        assertEquals(userId, bookingsDto.get(0).getBooker().getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getBookingsByUserIdTest_whenStateIsPast_thenReturnedPastBookings() {
        Long userId = 1L;
        BookingState state = BookingState.PAST;
        Integer from = 0;
        Integer size = 2;
        User user = User.builder().id(1L).email("test@test.test").name("test").build();
        Item item = Item.builder().id(1L).name("test").description("test description").build();
        Booking booking = Booking.builder().id(1L).booker(user).item(item).build();
        List<Booking> bookings = List.of(booking);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository
                        .findByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingDto> bookingsDto = bookingService.getBookingsByUserId(userId, state, from, size);

        assertEquals(userId, bookingsDto.get(0).getBooker().getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getBookingsByUserIdTest_whenStateIsFuture_thenReturnedFutureBookings() {
        Long userId = 1L;
        BookingState state = BookingState.FUTURE;
        Integer from = 0;
        Integer size = 2;
        User user = User.builder().id(1L).email("test@test.test").name("test").build();
        Item item = Item.builder().id(1L).name("test").description("test description").build();
        Booking booking = Booking.builder().id(1L).booker(user).item(item).build();
        List<Booking> bookings = List.of(booking);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository
                        .findByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingDto> bookingsDto = bookingService.getBookingsByUserId(userId, state, from, size);

        assertEquals(userId, bookingsDto.get(0).getBooker().getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getBookingsByUserIdTest_whenStateIsWaiting_thenReturnedWaitingBookings() {
        Long userId = 1L;
        BookingState state = BookingState.WAITING;
        Integer from = 0;
        Integer size = 2;
        User user = User.builder().id(1L).email("test@test.test").name("test").build();
        Item item = Item.builder().id(1L).name("test").description("test description").build();
        Booking booking = Booking.builder().id(1L).booker(user).item(item).build();
        List<Booking> bookings = List.of(booking);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository
                        .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingDto> bookingsDto = bookingService.getBookingsByUserId(userId, state, from, size);

        assertEquals(userId, bookingsDto.get(0).getBooker().getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.WAITING,
                        PageRequest.of(0, 2));
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getBookingsByUserIdTest_whenStateIsRejected_thenReturnedRejectedBookings() {
        Long userId = 1L;
        BookingState state = BookingState.REJECTED;
        Integer from = 0;
        Integer size = 2;
        User user = User.builder().id(1L).email("test@test.test").name("test").build();
        Item item = Item.builder().id(1L).name("test").description("test description").build();
        Booking booking = Booking.builder().id(1L).booker(user).item(item).build();
        List<Booking> bookings = List.of(booking);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository
                        .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingDto> bookingsDto = bookingService.getBookingsByUserId(userId, state, from, size);

        assertEquals(userId, bookingsDto.get(0).getBooker().getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndStatusOrderByStartDesc(userId, BookingStatus.REJECTED,
                        PageRequest.of(0, 2));
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getBookingsByUserIdTest_whenStateIsAll_thenReturnedAllBookings() {
        Long userId = 1L;
        BookingState state = BookingState.ALL;
        Integer from = 0;
        Integer size = 2;
        User user = User.builder().id(1L).email("test@test.test").name("test").build();
        Item item = Item.builder().id(1L).name("test").description("test description").build();
        Booking booking = Booking.builder().id(1L).booker(user).item(item).build();
        List<Booking> bookings = List.of(booking);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository
                        .findByBookerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingDto> bookingsDto = bookingService.getBookingsByUserId(userId, state, from, size);

        assertEquals(userId, bookingsDto.get(0).getBooker().getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getBookingByIdTest_whenBookingNotExist_thenNotFoundException() {
        Long userId = 1L;
        Long bookingId = 1L;
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(userId, bookingId));
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(anyLong());
    }

    @Test
    void getBookingByIdTest_whenUserIsNotBookerOrItemOwner_thenNotFoundException() {
        Long userId = 1L;
        Long bookingId = 1L;
        User booker = User.builder().id(2L).build();
        User owner = User.builder().id(3L).build();
        Item item = Item.builder().id(1L).owner(owner).build();
        Booking booking = Booking.builder().id(1L).booker(booker).item(item).build();
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        assertThrows(NotFoundException.class, () -> bookingService.getBookingById(userId, bookingId));
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(anyLong());
    }

    @Test
    void getBookingByIdTest_whenUserIsBooker_thenReturnedBookingDto() {
        Long userId = 1L;
        Long bookingId = 1L;
        User booker = User.builder().id(userId).build();
        User owner = User.builder().id(2L).build();
        Item item = Item.builder().id(1L).owner(owner).build();
        Booking booking = Booking.builder().id(1L).booker(booker).item(item).build();
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.getBookingById(userId, bookingId);

        assertEquals(bookingId, bookingDto.getId());
        assertEquals(userId, bookingDto.getBooker().getId());
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(anyLong());
    }

    @Test
    void getBookingByIdTest_whenUserIsItemOwner_thenReturnedBookingDto() {
        Long userId = 1L;
        Long bookingId = 1L;
        User booker = User.builder().id(2L).build();
        User owner = User.builder().id(userId).build();
        Item item = Item.builder().id(1L).owner(owner).build();
        Booking booking = Booking.builder().id(1L).booker(booker).item(item).build();
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));

        BookingDto bookingDto = bookingService.getBookingById(userId, bookingId);

        assertEquals(bookingId, bookingDto.getId());
        assertNotEquals(userId, bookingDto.getBooker().getId());
        Mockito.verify(bookingRepository, Mockito.times(1)).findById(anyLong());
    }

    @Test
    void getBookingsByOwnerIdTest_whenUserNotExist_thenNotFoundException() {
        Long ownerId = 1L;
        BookingState state = BookingState.ALL;
        Integer from = 0;
        Integer size = 2;
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.getBookingsByOwnerId(ownerId, state, from, size));
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verifyNoInteractions(bookingRepository);
    }

    @Test
    void getBookingsByOwnerIdTest_whenStateIsCurrent_thenReturnedCurrentBookings() {
        Long ownerId = 1L;
        BookingState state = BookingState.CURRENT;
        Integer from = 0;
        Integer size = 2;
        User user = User.builder().id(2L).email("test@test.test").name("test").build();
        User owner = User.builder().id(ownerId).email("test-owner@test.test").name("test-owner").build();
        Item item = Item.builder().id(1L).name("test").description("test description").owner(owner).build();
        Booking booking = Booking.builder().id(1L).booker(user).item(item).build();
        List<Booking> bookings = List.of(booking);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingDto> bookingsDto = bookingService.getBookingsByOwnerId(ownerId, state, from, size);

        assertEquals(item.getId(), bookingsDto.get(0).getItem().getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getBookingsByOwnerIdTest_whenStateIsPast_thenReturnedPastBookings() {
        Long ownerId = 1L;
        BookingState state = BookingState.PAST;
        Integer from = 0;
        Integer size = 2;
        User user = User.builder().id(2L).email("test@test.test").name("test").build();
        User owner = User.builder().id(ownerId).email("test-owner@test.test").name("test-owner").build();
        Item item = Item.builder().id(1L).name("test").description("test description").owner(owner).build();
        Booking booking = Booking.builder().id(1L).booker(user).item(item).build();
        List<Booking> bookings = List.of(booking);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingDto> bookingsDto = bookingService.getBookingsByOwnerId(ownerId, state, from, size);

        assertEquals(item.getId(), bookingsDto.get(0).getItem().getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getBookingsByOwnerIdTest_whenStateIsFuture_thenReturnedFutureBookings() {
        Long ownerId = 1L;
        BookingState state = BookingState.FUTURE;
        Integer from = 0;
        Integer size = 2;
        User user = User.builder().id(2L).email("test@test.test").name("test").build();
        User owner = User.builder().id(ownerId).email("test-owner@test.test").name("test-owner").build();
        Item item = Item.builder().id(1L).name("test").description("test description").owner(owner).build();
        Booking booking = Booking.builder().id(1L).booker(user).item(item).build();
        List<Booking> bookings = List.of(booking);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingDto> bookingsDto = bookingService.getBookingsByOwnerId(ownerId, state, from, size);

        assertEquals(item.getId(), bookingsDto.get(0).getItem().getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getBookingsByOwnerIdTest_whenStateIsWaiting_thenReturnedWaitingBookings() {
        Long ownerId = 1L;
        BookingState state = BookingState.WAITING;
        Integer from = 0;
        Integer size = 2;
        User user = User.builder().id(2L).email("test@test.test").name("test").build();
        User owner = User.builder().id(ownerId).email("test-owner@test.test").name("test-owner").build();
        Item item = Item.builder().id(1L).name("test").description("test description").owner(owner).build();
        Booking booking = Booking.builder().id(1L).booker(user).item(item).build();
        List<Booking> bookings = List.of(booking);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingDto> bookingsDto = bookingService.getBookingsByOwnerId(ownerId, state, from, size);

        assertEquals(item.getId(), bookingsDto.get(0).getItem().getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.WAITING,
                        PageRequest.of(0, 2));
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getBookingsByOwnerIdTest_whenStateIsRejected_thenReturnedRejectedBookings() {
        Long ownerId = 1L;
        BookingState state = BookingState.REJECTED;
        Integer from = 0;
        Integer size = 2;
        User user = User.builder().id(2L).email("test@test.test").name("test").build();
        User owner = User.builder().id(ownerId).email("test-owner@test.test").name("test-owner").build();
        Item item = Item.builder().id(1L).name("test").description("test description").owner(owner).build();
        Booking booking = Booking.builder().id(1L).booker(user).item(item).build();
        List<Booking> bookings = List.of(booking);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        Mockito.when(bookingRepository
                        .findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingDto> bookingsDto = bookingService.getBookingsByOwnerId(ownerId, state, from, size);

        assertEquals(item.getId(), bookingsDto.get(0).getItem().getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemOwnerIdAndStatusOrderByStartDesc(ownerId, BookingStatus.REJECTED,
                        PageRequest.of(0, 2));
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void getBookingsByOwnerIdTest_whenStateIsAll_thenReturnedAllBookings() {
        Long ownerId = 1L;
        BookingState state = BookingState.ALL;
        Integer from = 0;
        Integer size = 2;
        User user = User.builder().id(2L).email("test@test.test").name("test").build();
        User owner = User.builder().id(ownerId).email("test-owner@test.test").name("test-owner").build();
        Item item = Item.builder().id(1L).name("test").description("test description").owner(owner).build();
        Booking booking = Booking.builder().id(1L).booker(user).item(item).build();
        List<Booking> bookings = List.of(booking);
        Mockito.when(userRepository.findById(any())).thenReturn(Optional.of(owner));
        Mockito.when(bookingRepository
                        .findByItemOwnerIdOrderByStartDesc(anyLong(), any()))
                .thenReturn(new PageImpl<>(bookings));

        List<BookingDto> bookingsDto = bookingService.getBookingsByOwnerId(ownerId, state, from, size);

        assertEquals(item.getId(), bookingsDto.get(0).getItem().getId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(anyLong(), any(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndStartIsAfterOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByItemOwnerIdAndStatusOrderByStartDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByItemOwnerIdOrderByStartDesc(anyLong(), any());
    }

    @Test
    void createTest_whenItemIsNotExist_thenNotFoundException() {
        Long userId = 1L;
        Long itemId = 1L;
        BookingDto bookingDto = BookingDto.builder().start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2)).itemId(itemId).build();
        Mockito.when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(userId, bookingDto));
        Mockito.verify(itemRepository, Mockito.times(1)).findById(bookingDto.getItemId());
        Mockito.verify(userRepository, Mockito.never()).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.never()).save(any());
    }

    @Test
    void createTest_whenItemIsNotAvailable_thenNotAvailableException() {
        Long userId = 1L;
        Long itemId = 1L;
        BookingDto bookingDto = BookingDto.builder().start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2)).itemId(itemId).build();
        Item item = Item.builder().id(itemId).available(false).build();
        Mockito.when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));

        assertThrows(NotAvailableException.class, () -> bookingService.create(userId, bookingDto));
        Mockito.verify(itemRepository, Mockito.times(1)).findById(bookingDto.getItemId());
        Mockito.verify(userRepository, Mockito.never()).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.never()).save(any());
    }

    @Test
    void createTest_whenUserIsNotExist_thenNotFoundException() {
        Long userId = 1L;
        Long itemId = 1L;
        BookingDto bookingDto = BookingDto.builder().start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2)).itemId(itemId).build();
        Item item = Item.builder().id(itemId).available(true).build();
        Mockito.when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.create(userId, bookingDto));
        Mockito.verify(itemRepository, Mockito.times(1)).findById(bookingDto.getItemId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.never()).save(any());
    }

    @Test
    void createTest_whenUserIsItemOwner_thenNotFoundException() {
        Long userId = 1L;
        Long itemId = 1L;
        User user = User.builder().id(userId).name("test").email("test@test.test").build();
        BookingDto bookingDto = BookingDto.builder().start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2)).itemId(itemId).build();
        Item item = Item.builder().id(itemId).available(true).owner(user).build();
        Mockito.when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(NotFoundException.class, () -> bookingService.create(userId, bookingDto));
        Mockito.verify(itemRepository, Mockito.times(1)).findById(bookingDto.getItemId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.never()).save(any());
    }

    @Test
    void createTest_whenAllExist_thenReturnedBookingDto() {
        Long userId = 1L;
        Long itemId = 1L;
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        User user = User.builder().id(userId).name("test").email("test@test.test").build();
        User owner = User.builder().id(2L).name("test2").email("test2@test.test").build();
        BookingDto bookingDto = BookingDto.builder().start(start)
                .end(end).itemId(itemId).build();
        Item item = Item.builder().id(itemId).available(true).owner(owner).build();
        Booking savedBooking = Booking.builder().id(1L).start(start).end(end).item(item)
                .booker(user).status(BookingStatus.WAITING).build();
        Mockito.when(itemRepository.findById(bookingDto.getItemId())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository.save(any())).thenReturn(savedBooking);

        BookingDto createdBookingDto = bookingService.create(userId, bookingDto);

        assertEquals(bookingDto.getItemId(), createdBookingDto.getItem().getId());
        assertEquals(bookingDto.getStart(), createdBookingDto.getStart());
        assertEquals(bookingDto.getEnd(), createdBookingDto.getEnd());
        Mockito.verify(itemRepository, Mockito.times(1)).findById(bookingDto.getItemId());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(bookingRepository, Mockito.times(1)).save(any());
    }

    @Test
    void updateTest_whenBookingIsNotExist_thenNotFoundException() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        Boolean approved = false;
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> bookingService.update(bookingId, ownerId, approved));
        Mockito.verify(bookingRepository, Mockito.never()).save(any());
    }

    @Test
    void updateTest_whenUserIsBookerAndNotItemOwner_thenNotFoundException() {
        Long bookingId = 1L;
        Long ownerId = 1L;
        Boolean approved = false;
        User booker = User.builder().id(ownerId).name("booker").email("booker@test.test").build();
        User itemOwner = User.builder().id(2L).name("itemOwner").email("itemowner@test.test").build();
        Item item = Item.builder().id(1L).name("test").owner(itemOwner).build();
        Booking bookingForUpdate = Booking.builder().id(1L).booker(booker).item(item)
                .status(BookingStatus.WAITING).build();
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingForUpdate));

        assertThrows(NotFoundException.class, () -> bookingService.update(bookingId, ownerId, approved));
        Mockito.verify(bookingRepository, Mockito.never()).save(any());
    }

    @Test
    void updateTest_whenUserIsNotItemOwner_thenNotAvailableException() {
        Long bookingId = 1L;
        Long ownerId = 3L;
        Long bookerId = 1L;
        Boolean approved = false;
        User booker = User.builder().id(bookerId).name("booker").email("booker@test.test").build();
        User itemOwner = User.builder().id(2L).name("itemOwner").email("itemowner@test.test").build();
        Item item = Item.builder().id(1L).name("test").owner(itemOwner).build();
        Booking bookingForUpdate = Booking.builder().id(1L).booker(booker).item(item)
                .status(BookingStatus.WAITING).build();
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingForUpdate));

        assertThrows(NotAvailableException.class, () -> bookingService.update(bookingId, ownerId, approved));
        Mockito.verify(bookingRepository, Mockito.never()).save(any());
    }

    @Test
    void updateTest_whenBookingIsAlreadyApprovedAndApprovedIsTrue_thenNotAvailableException() {
        Long bookingId = 1L;
        Long ownerId = 2L;
        Long bookerId = 1L;
        Boolean approved = true;
        User booker = User.builder().id(bookerId).name("booker").email("booker@test.test").build();
        User itemOwner = User.builder().id(ownerId).name("itemOwner").email("itemowner@test.test").build();
        Item item = Item.builder().id(1L).name("test").owner(itemOwner).build();
        Booking bookingForUpdate = Booking.builder().id(1L).booker(booker).item(item)
                .status(BookingStatus.APPROVED).build();
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(bookingForUpdate));

        assertThrows(NotAvailableException.class, () -> bookingService.update(bookingId, ownerId, approved));
        Mockito.verify(bookingRepository, Mockito.never()).save(any());
    }

    @Test
    void updateTest_whenBookingIsApproving_thenReturnedApprovedBooking() {
        Long bookingId = 1L;
        Long ownerId = 2L;
        Long bookerId = 1L;
        Boolean approved = true;
        User booker = User.builder().id(bookerId).name("booker").email("booker@test.test").build();
        User itemOwner = User.builder().id(ownerId).name("itemOwner").email("itemowner@test.test").build();
        Item item = Item.builder().id(1L).name("test").owner(itemOwner).build();
        Booking booking = Booking.builder().id(1L).booker(booker).item(item)
                .status(BookingStatus.WAITING).build();
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto bookingDto = bookingService.update(bookingId, ownerId, approved);

        assertEquals(BookingStatus.APPROVED, bookingDto.getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1)).save(any());
    }

    @Test
    void updateTest_whenBookingIsRejecting_thenReturnedRejectingBooking() {
        Long bookingId = 1L;
        Long ownerId = 2L;
        Long bookerId = 1L;
        Boolean approved = false;
        User booker = User.builder().id(bookerId).name("booker").email("booker@test.test").build();
        User itemOwner = User.builder().id(ownerId).name("itemOwner").email("itemowner@test.test").build();
        Item item = Item.builder().id(1L).name("test").owner(itemOwner).build();
        Booking booking = Booking.builder().id(1L).booker(booker).item(item)
                .status(BookingStatus.WAITING).build();
        Mockito.when(bookingRepository.findById(bookingId)).thenReturn(Optional.of(booking));
        Mockito.when(bookingRepository.save(any())).thenReturn(booking);

        BookingDto bookingDto = bookingService.update(bookingId, ownerId, approved);

        assertEquals(BookingStatus.REJECTED, bookingDto.getStatus());
        Mockito.verify(bookingRepository, Mockito.times(1)).save(any());
    }
}