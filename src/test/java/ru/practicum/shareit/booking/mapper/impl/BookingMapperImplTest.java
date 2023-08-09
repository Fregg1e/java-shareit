package ru.practicum.shareit.booking.mapper.impl;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.impl.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.impl.UserMapperImpl;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class BookingMapperImplTest {
    private final ItemMapper itemMapper = new ItemMapperImpl();
    private final UserMapper userMapper = new UserMapperImpl();
    private final BookingMapper bookingMapper = new BookingMapperImpl(itemMapper, userMapper);

    @Test
    void toBookingDtoTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Item item = Item.builder().id(1L).name("test").description("test description").available(true).build();
        User user = User.builder().id(1L).email("test@test.test").name("test").build();
        Booking booking = Booking.builder().id(1L).start(start)
                .end(end).item(item).booker(user).build();

        BookingDto bookingDto = bookingMapper.toBookingDto(booking);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertEquals(booking.getItem().getId(), bookingDto.getItem().getId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBooker().getId());
    }

    @Test
    void toBookingTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDto = BookingDto.builder().start(start).end(end).build();

        Booking booking = bookingMapper.toBooking(bookingDto);

        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
    }

    @Test
    void toBookingForItemDtoTest() {
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        Item item = Item.builder().id(1L).name("test").description("test description").available(true).build();
        User user = User.builder().id(1L).email("test@test.test").name("test").build();
        Booking booking = Booking.builder().id(1L).start(start)
                .end(end).item(item).booker(user).build();

        BookingDto bookingDto = bookingMapper.toBookingForItemDto(booking);

        assertEquals(booking.getId(), bookingDto.getId());
        assertEquals(booking.getStart(), bookingDto.getStart());
        assertEquals(booking.getEnd(), bookingDto.getEnd());
        assertNull(bookingDto.getItem());
        assertNull(bookingDto.getBooker());
        assertNull(bookingDto.getItemId());
        assertEquals(booking.getBooker().getId(), bookingDto.getBookerId());
    }
}