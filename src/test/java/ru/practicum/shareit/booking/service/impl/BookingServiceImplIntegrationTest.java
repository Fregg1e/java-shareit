package ru.practicum.shareit.booking.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.service.BookingService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;

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
    void create() {
    }

    @Test
    void update() {
    }

    @Test
    void getBookingById() {
    }

    @Test
    void getBookingsByUserId() {
    }

    @Test
    void getBookingsByOwnerId() {
    }


}