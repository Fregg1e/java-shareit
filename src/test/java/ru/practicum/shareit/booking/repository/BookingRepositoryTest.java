package ru.practicum.shareit.booking.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class BookingRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private BookingRepository repository;
    private List<User> users;
    private List<Item> items;

    @BeforeEach
    void beforeEach() {
        users = List.of(
                User.builder().name("test1").email("test1@email.test").build(),
                User.builder().name("test2").email("test2@email.test").build(),
                User.builder().name("test3").email("test3@email.test").build()
        );
        for (User user : users) {
            em.persist(user);
        }
        items = List.of(
                Item.builder().name("1").description("1").available(true).owner(users.get(0)).build(),
                Item.builder().name("1").description("2").available(true).owner(users.get(1)).build(),
                Item.builder().name("2").description("1").available(true).owner(users.get(1)).build()
        );
        for (Item item : items) {
            em.persist(item);
        }
    }

    @Test
    void findByBookerIdOrderByStartDescTest() {
        List<Booking> bookings = List.of(
                Booking.builder().start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2))
                        .item(items.get(0)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(1)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(0)).booker(users.get(1)).status(BookingStatus.WAITING).build()
        );
        for (Booking booking : bookings) {
            em.persist(booking);
        }

        List<Booking> bookingsByBookerId = repository.findByBookerIdOrderByStartDesc(users.get(2).getId(),
                PageRequest.of(0, 3)).getContent();

        assertEquals(2, bookingsByBookerId.size());
        assertEquals(bookings.get(1), bookingsByBookerId.get(0));
        assertEquals(bookings.get(0), bookingsByBookerId.get(1));
    }

    @Test
    void findByItemOwnerIdOrderByStartDescTest() {
        List<Booking> bookings = List.of(
                Booking.builder().start(LocalDateTime.now().plusDays(1)).end(LocalDateTime.now().plusDays(2))
                        .item(items.get(0)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(1)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(0)).booker(users.get(1)).status(BookingStatus.WAITING).build()
        );
        for (Booking booking : bookings) {
            em.persist(booking);
        }

        List<Booking> bookingsByOwnerId = repository.findByItemOwnerIdOrderByStartDesc(users.get(0).getId(),
                PageRequest.of(0, 3)).getContent();

        assertEquals(2, bookingsByOwnerId.size());
        assertEquals(bookings.get(2), bookingsByOwnerId.get(0));
        assertEquals(bookings.get(0), bookingsByOwnerId.get(1));
    }

    @Test
    void findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDescTest() {
        List<Booking> bookings = List.of(
                Booking.builder().start(LocalDateTime.now().minusDays(1)).end(LocalDateTime.now().plusDays(2))
                        .item(items.get(0)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(1)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(0)).booker(users.get(1)).status(BookingStatus.WAITING).build()
        );
        for (Booking booking : bookings) {
            em.persist(booking);
        }

        List<Booking> bookingsResponse = repository
                .findByBookerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(users.get(2).getId(),
                        LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 3)).getContent();

        assertEquals(1, bookingsResponse.size());
        assertEquals(bookings.get(0), bookingsResponse.get(0));
    }

    @Test
    void findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDescTest() {
        List<Booking> bookings = List.of(
                Booking.builder().start(LocalDateTime.now().minusDays(1)).end(LocalDateTime.now().plusDays(2))
                        .item(items.get(0)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(1)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(0)).booker(users.get(1)).status(BookingStatus.WAITING).build()
        );
        for (Booking booking : bookings) {
            em.persist(booking);
        }

        List<Booking> bookingsResponse = repository
                .findByItemOwnerIdAndStartIsBeforeAndEndIsAfterOrderByStartDesc(users.get(0).getId(),
                        LocalDateTime.now(), LocalDateTime.now(), PageRequest.of(0, 3)).getContent();

        assertEquals(2, bookingsResponse.size());
        assertEquals(bookings.get(0), bookingsResponse.get(0));
        assertEquals(bookings.get(2), bookingsResponse.get(1));
    }

    @Test
    void findByBookerIdAndEndIsBeforeOrderByStartDescTest() {
        List<Booking> bookings = List.of(
                Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().minusDays(1))
                        .item(items.get(0)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(1)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(0)).booker(users.get(1)).status(BookingStatus.WAITING).build()
        );
        for (Booking booking : bookings) {
            em.persist(booking);
        }

        List<Booking> bookingsResponse = repository
                .findByBookerIdAndEndIsBeforeOrderByStartDesc(users.get(2).getId(),
                        LocalDateTime.now(), PageRequest.of(0, 3)).getContent();

        assertEquals(1, bookingsResponse.size());
        assertEquals(bookings.get(0), bookingsResponse.get(0));
    }

    @Test
    void findByItemOwnerIdAndEndIsBeforeOrderByStartDescTest() {
        List<Booking> bookings = List.of(
                Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().minusDays(1))
                        .item(items.get(0)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(1)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(0)).booker(users.get(1)).status(BookingStatus.WAITING).build()
        );
        for (Booking booking : bookings) {
            em.persist(booking);
        }

        List<Booking> bookingsResponse = repository
                .findByItemOwnerIdAndEndIsBeforeOrderByStartDesc(users.get(0).getId(),
                        LocalDateTime.now(), PageRequest.of(0, 3)).getContent();

        assertEquals(1, bookingsResponse.size());
        assertEquals(bookings.get(0), bookingsResponse.get(0));
    }

    @Test
    void findByBookerIdAndStartIsAfterOrderByStartDescTest() {
        List<Booking> bookings = List.of(
                Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().minusDays(1))
                        .item(items.get(0)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(1)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(0)).booker(users.get(1)).status(BookingStatus.WAITING).build()
        );
        for (Booking booking : bookings) {
            em.persist(booking);
        }

        List<Booking> bookingsResponse = repository
                .findByBookerIdAndStartIsAfterOrderByStartDesc(users.get(2).getId(),
                        LocalDateTime.now(), PageRequest.of(0, 3)).getContent();

        assertEquals(1, bookingsResponse.size());
        assertEquals(bookings.get(1), bookingsResponse.get(0));
    }

    @Test
    void findByItemOwnerIdAndStartIsAfterOrderByStartDescTest() {
        List<Booking> bookings = List.of(
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(1))
                        .item(items.get(0)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(1)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(0)).booker(users.get(1)).status(BookingStatus.WAITING).build()
        );
        for (Booking booking : bookings) {
            em.persist(booking);
        }

        List<Booking> bookingsResponse = repository
                .findByItemOwnerIdAndStartIsAfterOrderByStartDesc(users.get(0).getId(),
                        LocalDateTime.now(), PageRequest.of(0, 3)).getContent();

        assertEquals(1, bookingsResponse.size());
        assertEquals(bookings.get(0), bookingsResponse.get(0));
    }

    @Test
    void findByBookerIdAndStatusOrderByStartDescTest() {
        List<Booking> bookings = List.of(
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(1))
                        .item(items.get(0)).booker(users.get(2)).status(BookingStatus.REJECTED).build(),
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(1)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(0)).booker(users.get(1)).status(BookingStatus.WAITING).build()
        );
        for (Booking booking : bookings) {
            em.persist(booking);
        }

        List<Booking> bookingsResponse = repository
                .findByBookerIdAndStatusOrderByStartDesc(users.get(2).getId(),
                        BookingStatus.REJECTED, PageRequest.of(0, 3)).getContent();

        assertEquals(1, bookingsResponse.size());
        assertEquals(bookings.get(0), bookingsResponse.get(0));
    }

    @Test
    void findByItemOwnerIdAndStatusOrderByStartDescTest() {
        List<Booking> bookings = List.of(
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(1))
                        .item(items.get(0)).booker(users.get(2)).status(BookingStatus.REJECTED).build(),
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(1)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(0)).booker(users.get(1)).status(BookingStatus.WAITING).build()
        );
        for (Booking booking : bookings) {
            em.persist(booking);
        }

        List<Booking> bookingsResponse = repository
                .findByItemOwnerIdAndStatusOrderByStartDesc(users.get(0).getId(),
                        BookingStatus.REJECTED, PageRequest.of(0, 3)).getContent();

        assertEquals(1, bookingsResponse.size());
        assertEquals(bookings.get(0), bookingsResponse.get(0));
    }

    @Test
    void findFirstByItemIdAndStatusAndStartIsBeforeOrderByEndDescTest() {
        List<Booking> bookings = List.of(
                Booking.builder().start(LocalDateTime.now().minusDays(1)).end(LocalDateTime.now().minusHours(1))
                        .item(items.get(0)).booker(users.get(2)).status(BookingStatus.APPROVED).build(),
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(1)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(0)).booker(users.get(1)).status(BookingStatus.APPROVED).build()
        );
        for (Booking booking : bookings) {
            em.persist(booking);
        }

        Booking bookingResponse = repository
                .findFirstByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(items.get(0).getId(),
                        BookingStatus.APPROVED, LocalDateTime.now());

        assertEquals(bookings.get(2), bookingResponse);
    }

    @Test
    void findFirstByItemIdAndStatusAndStartIsAfterOrderByStartAscTest() {
        List<Booking> bookings = List.of(
                Booking.builder().start(LocalDateTime.now().plusMinutes(10)).end(LocalDateTime.now().plusHours(1))
                        .item(items.get(0)).booker(users.get(2)).status(BookingStatus.APPROVED).build(),
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(1)).booker(users.get(2)).status(BookingStatus.WAITING).build(),
                Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(0)).booker(users.get(1)).status(BookingStatus.APPROVED).build()
        );
        for (Booking booking : bookings) {
            em.persist(booking);
        }

        Booking bookingResponse = repository
                .findFirstByItemIdAndStatusAndStartIsAfterOrderByStartAsc(items.get(0).getId(),
                        BookingStatus.APPROVED, LocalDateTime.now());

        assertEquals(bookings.get(0), bookingResponse);
    }

    @Test
    void findByBookerIdAndItemIdAndStatusAndEndIsBeforeTest() {
        List<Booking> bookings = List.of(
                Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().minusDays(1))
                        .item(items.get(0)).booker(users.get(2)).status(BookingStatus.APPROVED).build(),
                Booking.builder().start(LocalDateTime.now().plusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(1)).booker(users.get(2)).status(BookingStatus.APPROVED).build(),
                Booking.builder().start(LocalDateTime.now().minusDays(2)).end(LocalDateTime.now().plusDays(3))
                        .item(items.get(0)).booker(users.get(1)).status(BookingStatus.APPROVED).build()
        );
        for (Booking booking : bookings) {
            em.persist(booking);
        }

        List<Booking> bookingsResponse = repository
                .findByBookerIdAndItemIdAndStatusAndEndIsBefore(users.get(2).getId(), items.get(0).getId(),
                        BookingStatus.APPROVED, LocalDateTime.now());

        assertEquals(1, bookingsResponse.size());
        assertEquals(bookings.get(0), bookingsResponse.get(0));
    }
}