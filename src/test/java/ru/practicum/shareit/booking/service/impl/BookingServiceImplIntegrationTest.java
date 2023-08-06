package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.model.BookingState;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:shareit",
        "spring.datasource.username=test",
        "spring.datasource.password=test"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class BookingServiceImplIntegrationTest {
    private final EntityManager entityManager;
    private final BookingService bookingService;
    private List<User> users;

    @BeforeEach
    void beforeEach() {
        users = List.of(
                User.builder().name("test1").email("test1@email.test").build(),
                User.builder().name("test2").email("test2@email.test").build(),
                User.builder().name("test3").email("test3@email.test").build()
        );
        for (User user : users) {
            entityManager.persist(user);
        }
    }

    @Test
    void createTest() {
        Item item = Item.builder()
                .name("testItem")
                .description("testItem description")
                .available(true)
                .owner(users.get(0))
                .build();
        entityManager.persist(item);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDtoToCreate = makeBookingDto(start,
                end, item.getId());

        BookingDto bookingDto = bookingService.create(users.get(1).getId(), bookingDtoToCreate);

        assertNotNull(bookingDto);
        assertNotNull(bookingDto.getId());
        assertEquals(start, bookingDto.getStart());
        assertEquals(end, bookingDto.getEnd());
        assertEquals(item.getId(), bookingDto.getItem().getId());
        assertEquals(BookingStatus.WAITING, bookingDto.getStatus());
    }

    @Test
    void updateTest() {
        Item item = Item.builder()
                .name("testItem")
                .description("testItem description")
                .available(true)
                .owner(users.get(0))
                .build();
        entityManager.persist(item);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDtoToCreate = makeBookingDto(start,
                end, item.getId());
        BookingDto bookingDto = bookingService.create(users.get(1).getId(), bookingDtoToCreate);

        BookingDto updatedBookingDto = bookingService.update(bookingDto.getId(), users.get(0).getId(), true);

        assertEquals(BookingStatus.APPROVED, updatedBookingDto.getStatus());
    }

    @Test
    void getBookingByIdTest() {
        Item item = Item.builder()
                .name("testItem")
                .description("testItem description")
                .available(true)
                .owner(users.get(0))
                .build();
        entityManager.persist(item);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDtoToCreate = makeBookingDto(start,
                end, item.getId());
        BookingDto createdBookingDto = bookingService.create(users.get(1).getId(), bookingDtoToCreate);

        BookingDto bookingDto = bookingService.getBookingById(users.get(1).getId(), createdBookingDto.getId());

        assertEquals(bookingDto, createdBookingDto);
    }

    @Test
    void getBookingsByUserIdTest() {
        Item item = Item.builder()
                .name("testItem")
                .description("testItem description")
                .available(true)
                .owner(users.get(0))
                .build();
        entityManager.persist(item);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDtoToCreate = makeBookingDto(start,
                end, item.getId());
        BookingDto createdBookingDto = bookingService.create(users.get(1).getId(), bookingDtoToCreate);

        List<BookingDto> bookingDtos = bookingService.getBookingsByUserId(users.get(1).getId(),
                BookingState.WAITING, 0, 2);

        assertEquals(createdBookingDto, bookingDtos.get(0));
    }

    @Test
    void getBookingsByOwnerIdTest() {
        Item item = Item.builder()
                .name("testItem")
                .description("testItem description")
                .available(true)
                .owner(users.get(0))
                .build();
        entityManager.persist(item);
        LocalDateTime start = LocalDateTime.now().plusDays(1);
        LocalDateTime end = LocalDateTime.now().plusDays(2);
        BookingDto bookingDtoToCreate = makeBookingDto(start,
                end, item.getId());
        BookingDto createdBookingDto = bookingService.create(users.get(1).getId(), bookingDtoToCreate);

        List<BookingDto> bookingDtos = bookingService.getBookingsByOwnerId(users.get(0).getId(),
                BookingState.WAITING, 0, 2);

        assertEquals(createdBookingDto, bookingDtos.get(0));
    }

    private BookingDto makeBookingDto(LocalDateTime start, LocalDateTime end, Long itemId) {
        return BookingDto.builder()
                .start(start)
                .end(end)
                .itemId(itemId)
                .build();
    }
}