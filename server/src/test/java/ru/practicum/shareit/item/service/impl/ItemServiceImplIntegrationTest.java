package ru.practicum.shareit.item.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.service.ItemService;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.persistence.EntityManager;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
@ActiveProfiles("integrationtest")
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class ItemServiceImplIntegrationTest {
    private final EntityManager entityManager;
    private final ItemService itemService;
    private final ItemMapper itemMapper;
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
        ItemRequest request = ItemRequest.builder().description("1").requestor(users.get(0))
                        .created(LocalDateTime.now()).build();
        entityManager.persist(request);
        ItemDto itemDtoToCreate = makeItemDto("item1", "item1 description", true);
        itemDtoToCreate.setRequestId(request.getId());

        ItemDto itemDto = itemService.create(users.get(0).getId(), itemDtoToCreate);

        assertNotNull(itemDto);
        assertNotNull(itemDto.getId());
        assertEquals(itemDtoToCreate.getName(), itemDto.getName());
        assertEquals(itemDtoToCreate.getDescription(), itemDto.getDescription());
        assertEquals(itemDtoToCreate.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void updateDescriptionTest() {
        ItemDto itemDtoToCreate = makeItemDto("item1", "item1 description", true);
        ItemDto createdItemDto = itemService.create(users.get(0).getId(), itemDtoToCreate);
        ItemDto itemDtoToUpdate = makeItemDto(null, "item1 description update", null);

        ItemDto itemDto = itemService.update(createdItemDto.getId(), users.get(0).getId(), itemDtoToUpdate);

        assertNotNull(itemDto);
        assertEquals(createdItemDto.getId(), itemDto.getId());
        assertEquals(createdItemDto.getName(), itemDto.getName());
        assertEquals("item1 description update", itemDto.getDescription());
        assertEquals(createdItemDto.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void updateNameTest() {
        ItemDto itemDtoToCreate = makeItemDto("item1", "item1 description", true);
        ItemDto createdItemDto = itemService.create(users.get(0).getId(), itemDtoToCreate);
        ItemDto itemDtoToUpdate = makeItemDto("itemUpdate", null, null);

        ItemDto itemDto = itemService.update(createdItemDto.getId(), users.get(0).getId(), itemDtoToUpdate);

        assertNotNull(itemDto);
        assertEquals(createdItemDto.getId(), itemDto.getId());
        assertEquals("itemUpdate", itemDto.getName());
        assertEquals(createdItemDto.getDescription(), itemDto.getDescription());
        assertEquals(createdItemDto.getAvailable(), itemDto.getAvailable());
    }

    @Test
    void getItemByIdTest() {
        ItemDto itemDtoToCreate = makeItemDto("item1", "item1 description", true);
        ItemDto createdItemDto = itemService.create(users.get(0).getId(), itemDtoToCreate);
        Booking lastBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(itemMapper.toItem(createdItemDto, users.get(0)))
                .booker(users.get(1))
                .status(BookingStatus.APPROVED).build();
        entityManager.persist(lastBooking);
        Booking nextBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(itemMapper.toItem(createdItemDto, users.get(0)))
                .booker(users.get(1))
                .status(BookingStatus.APPROVED).build();
        entityManager.persist(nextBooking);
        CommentDto commentDtoToCreate = CommentDto.builder().text("test comment").build();
        CommentDto commentDto = itemService.createComment(users.get(1).getId(), createdItemDto.getId(),
                commentDtoToCreate);

        ItemDto itemDto = itemService.getItemById(users.get(0).getId(), createdItemDto.getId());

        assertNotNull(itemDto);
        assertEquals(createdItemDto.getId(), itemDto.getId());
        assertEquals(createdItemDto.getName(), itemDto.getName());
        assertEquals(createdItemDto.getDescription(), itemDto.getDescription());
        assertEquals(createdItemDto.getAvailable(), itemDto.getAvailable());
        assertNotNull(itemDto.getLastBooking());
        assertNotNull(itemDto.getNextBooking());
        assertFalse(itemDto.getComments().isEmpty());
        assertEquals(commentDto, itemDto.getComments().get(0));
    }

    @Test
    void getItemByIdWithoutLastBookingTest() {
        ItemDto itemDtoToCreate = makeItemDto("item1", "item1 description", true);
        ItemDto createdItemDto = itemService.create(users.get(0).getId(), itemDtoToCreate);
        Booking nextBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(itemMapper.toItem(createdItemDto, users.get(0)))
                .booker(users.get(1))
                .status(BookingStatus.APPROVED).build();
        entityManager.persist(nextBooking);

        ItemDto itemDto = itemService.getItemById(users.get(0).getId(), createdItemDto.getId());

        assertNotNull(itemDto);
        assertEquals(createdItemDto.getId(), itemDto.getId());
        assertEquals(createdItemDto.getName(), itemDto.getName());
        assertEquals(createdItemDto.getDescription(), itemDto.getDescription());
        assertEquals(createdItemDto.getAvailable(), itemDto.getAvailable());
        assertNull(itemDto.getLastBooking());
        assertNotNull(itemDto.getNextBooking());
        assertTrue(itemDto.getComments().isEmpty());
    }

    @Test
    void deleteTest() {
        ItemDto itemDtoToCreate = makeItemDto("item1", "item1 description", true);
        ItemDto createdItemDto = itemService.create(users.get(0).getId(), itemDtoToCreate);

        itemService.delete(createdItemDto.getId(), users.get(0).getId());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(users.get(0).getId(),
                createdItemDto.getId()));
    }

    @Test
    void getAllTest() {
        ItemDto itemDtoToCreate = makeItemDto("item1", "item1 description", true);
        ItemDto createdItemDto = itemService.create(users.get(0).getId(), itemDtoToCreate);

        List<ItemDto> itemDtos = itemService.getAll(0, 2);

        assertFalse(itemDtos.isEmpty());
        assertEquals(1, itemDtos.size());
        assertEquals(createdItemDto.getId(), itemDtos.get(0).getId());
    }

    @Test
    void getItemsByUserIdTest() {
        ItemDto itemDtoToCreate = makeItemDto("item1", "item1 description", true);
        ItemDto createdItemDto = itemService.create(users.get(0).getId(), itemDtoToCreate);
        Booking lastBooking = Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(itemMapper.toItem(createdItemDto, users.get(0)))
                .booker(users.get(1))
                .status(BookingStatus.APPROVED).build();
        entityManager.persist(lastBooking);
        Booking nextBooking = Booking.builder()
                .start(LocalDateTime.now().plusDays(1))
                .end(LocalDateTime.now().plusDays(2))
                .item(itemMapper.toItem(createdItemDto, users.get(0)))
                .booker(users.get(1))
                .status(BookingStatus.APPROVED).build();
        entityManager.persist(nextBooking);

        ItemDto itemDtoToCreate2 = makeItemDto("item2", "item2 description", true);
        ItemDto createdItemDto2 = itemService.create(users.get(0).getId(), itemDtoToCreate2);
        Booking lastBooking2 = Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(itemMapper.toItem(createdItemDto2, users.get(0)))
                .booker(users.get(1))
                .status(BookingStatus.APPROVED).build();
        entityManager.persist(lastBooking2);
        Booking nextBooking2 = Booking.builder()
                .start(LocalDateTime.now().plusDays(2))
                .end(LocalDateTime.now().plusDays(3))
                .item(itemMapper.toItem(createdItemDto2, users.get(0)))
                .booker(users.get(1))
                .status(BookingStatus.APPROVED).build();
        entityManager.persist(nextBooking2);

        List<ItemDto> itemDtos = itemService.getItemsByUserId(users.get(0).getId(), 0, 2);

        assertFalse(itemDtos.isEmpty());
        assertEquals(2, itemDtos.size());
        assertEquals(createdItemDto.getId(), itemDtos.get(0).getId());
        assertNotNull(itemDtos.get(0).getLastBooking());
        assertNotNull(itemDtos.get(0).getNextBooking());
        assertEquals(createdItemDto2.getId(), itemDtos.get(1).getId());
        assertNotNull(itemDtos.get(1).getLastBooking());
        assertNotNull(itemDtos.get(1).getNextBooking());
    }

    @Test
    void searchTest() {
        ItemDto itemDtoToCreate = makeItemDto("item1", "item1 description", true);
        ItemDto createdItemDto = itemService.create(users.get(0).getId(), itemDtoToCreate);

        List<ItemDto> itemDtos = itemService.search("item1", 0, 2);

        assertFalse(itemDtos.isEmpty());
        assertEquals(1, itemDtos.size());
        assertEquals(createdItemDto.getId(), itemDtos.get(0).getId());
    }

    @Test
    void createCommentTest() {
        ItemDto itemDtoToCreate = makeItemDto("item1", "item1 description", true);
        ItemDto createdItemDto = itemService.create(users.get(0).getId(), itemDtoToCreate);
        Booking booking = Booking.builder()
                .start(LocalDateTime.now().minusDays(2))
                .end(LocalDateTime.now().minusDays(1))
                .item(itemMapper.toItem(createdItemDto, users.get(0)))
                .booker(users.get(1))
                .status(BookingStatus.APPROVED).build();
        entityManager.persist(booking);
        CommentDto commentDtoToCreate = CommentDto.builder().text("test comment").build();

        CommentDto commentDto = itemService.createComment(users.get(1).getId(), createdItemDto.getId(),
                commentDtoToCreate);

        assertNotNull(commentDto);
        assertNotNull(commentDto.getId());
        assertEquals(commentDtoToCreate.getText(), commentDto.getText());
    }

    private ItemDto makeItemDto(String name, String description, Boolean available) {
        return ItemDto.builder()
                .name(name)
                .description(description)
                .available(available)
                .build();
    }
}