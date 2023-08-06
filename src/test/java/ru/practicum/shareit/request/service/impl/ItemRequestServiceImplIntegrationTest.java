package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:shareit",
        "spring.datasource.username=test",
        "spring.datasource.password=test"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemRequestServiceImplIntegrationTest {
    private final EntityManager entityManager;
    private final ItemRequestService itemRequestService;
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
        ItemRequestDto itemRequestDto = makeItemRequestDto("test description");

        ItemRequestDto createdItemRequestDto = itemRequestService.create(users.get(0).getId(), itemRequestDto);

        assertNotNull(createdItemRequestDto.getId());
        assertEquals(itemRequestDto.getDescription(), createdItemRequestDto.getDescription());
        assertNotNull(createdItemRequestDto.getCreated());
    }

    @Test
    void getRequestsByUserIdTest() {
        List<ItemRequestDto> itemRequestsDtoForCreate = List.of(
            makeItemRequestDto("test1 description"),
            makeItemRequestDto("test2 description"),
            makeItemRequestDto("test3 description")
        );
        List<ItemRequestDto> createdItemRequestsDto = new ArrayList<>();
        long userId = users.get(0).getId();
        for (ItemRequestDto itemRequestDto : itemRequestsDtoForCreate) {
            createdItemRequestsDto.add(itemRequestService.create(userId, itemRequestDto));
            userId++;
        }

        List<ItemRequestDto> itemRequestsDto = itemRequestService.getRequestsByUserId(users.get(0).getId());

        assertFalse(itemRequestsDto.isEmpty());
        assertEquals(1, itemRequestsDto.size());
        assertEquals(createdItemRequestsDto.get(0).getId(), itemRequestsDto.get(0).getId());
        assertEquals(createdItemRequestsDto.get(0).getDescription(), itemRequestsDto.get(0).getDescription());
        assertEquals(createdItemRequestsDto.get(0).getCreated(), itemRequestsDto.get(0).getCreated());
    }

    @Test
    void getAllRequestsTest() {
        List<ItemRequestDto> itemRequestsDtoForCreate = List.of(
                makeItemRequestDto("test1 description"),
                makeItemRequestDto("test2 description"),
                makeItemRequestDto("test3 description")
        );
        List<ItemRequestDto> createdItemRequestsDto = new ArrayList<>();
        for (ItemRequestDto itemRequestDto : itemRequestsDtoForCreate) {
            createdItemRequestsDto.add(itemRequestService.create(users.get(1).getId(), itemRequestDto));
        }

        List<ItemRequestDto> itemRequestsDto = itemRequestService.getAllRequests(users.get(0).getId(), 2, 2);

        assertEquals(1, itemRequestsDto.size());
        assertEquals(createdItemRequestsDto.get(0).getId(), itemRequestsDto.get(0).getId());
        assertEquals(createdItemRequestsDto.get(0).getDescription(), itemRequestsDto.get(0).getDescription());
        assertEquals(createdItemRequestsDto.get(0).getCreated(), itemRequestsDto.get(0).getCreated());
    }

    @Test
    void getRequestByIdTest() {
        List<ItemRequestDto> itemRequestsDtoForCreate = List.of(
                makeItemRequestDto("test1 description"),
                makeItemRequestDto("test2 description"),
                makeItemRequestDto("test3 description")
        );
        List<ItemRequestDto> createdItemRequestsDto = new ArrayList<>();
        long userId = users.get(0).getId();
        for (ItemRequestDto itemRequestDto : itemRequestsDtoForCreate) {
            createdItemRequestsDto.add(itemRequestService.create(userId, itemRequestDto));
            userId++;
        }

        ItemRequestDto itemRequestDto = itemRequestService.getRequestById(users.get(2).getId(),
                createdItemRequestsDto.get(0).getId());

        assertEquals(createdItemRequestsDto.get(0).getDescription(), itemRequestDto.getDescription());
        assertEquals(createdItemRequestsDto.get(0).getCreated(), itemRequestDto.getCreated());
    }

    private ItemRequestDto makeItemRequestDto(String description) {
        return ItemRequestDto.builder().description(description).build();
    }
}