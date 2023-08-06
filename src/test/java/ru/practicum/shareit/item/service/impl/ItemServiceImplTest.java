package ru.practicum.shareit.item.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.booking.mapper.impl.BookingMapperImpl;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.booking.repository.BookingRepository;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.mapper.impl.CommentMapperImpl;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.comment.repository.CommentRepository;
import ru.practicum.shareit.exception.model.AccessException;
import ru.practicum.shareit.exception.model.NotAvailableException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.impl.ItemMapperImpl;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.impl.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;
    @Spy
    private ItemMapper itemMapper = new ItemMapperImpl();
    private final UserMapper userMapper = new UserMapperImpl();
    @Spy
    private BookingMapper bookingMapper = new BookingMapperImpl(itemMapper, userMapper);
    @Spy
    private CommentMapper commentMapper = new CommentMapperImpl();
    @InjectMocks
    private ItemServiceImpl itemService;

    @Test
    void getAllTest_whenRepositoryIsEmpty_thenReturnedEmptyList() {
        Integer from = 0;
        Integer size = 2;
        Mockito.when(itemRepository.findAll(any(Pageable.class))).thenReturn(Page.empty());

        List<ItemDto> itemsDto = itemService.getAll(from, size);

        assertTrue(itemsDto.isEmpty());
        Mockito.verify(itemRepository, Mockito.times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getAllTest_whenFromIs0AndSizeIs2_thenReturnedListSize2() {
        Integer from = 0;
        Integer size = 2;
        Item firstItem = Item.builder().id(1L).build();
        Item secondItem = Item.builder().id(2L).build();
        List<Item> items = List.of(firstItem, secondItem);
        Mockito.when(itemRepository.findAll(any(Pageable.class))).thenReturn(new PageImpl<>(items));

        List<ItemDto> itemsDto = itemService.getAll(from, size);

        assertFalse(itemsDto.isEmpty());
        assertEquals(2, itemsDto.size());
        assertEquals(firstItem.getId(), itemsDto.get(0).getId());
        assertEquals(secondItem.getId(), itemsDto.get(1).getId());
        Mockito.verify(itemRepository, Mockito.times(1)).findAll(any(Pageable.class));
    }

    @Test
    void getItemByIdTest_whenItemNotExist_thenNotFoundException() {
        Long userId = 1L;
        Long itemId = 1L;
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemById(userId, itemId));
        Mockito.verify(commentRepository, Mockito.never()).findByItemId(anyLong());
        Mockito.verify(bookingRepository, Mockito.never())
                .findFirstByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findFirstByItemIdAndStatusAndStartIsAfterOrderByStartAsc(anyLong(), any(), any());
    }

    @Test
    void getItemByIdTest_whenItemExistAndUserIsOwner_thenReturnedItemDtoWithBooking() {
        Long userId = 1L;
        Long itemId = 1L;
        User user = User.builder().id(userId).name("name").email("test@test.test").build();
        User lastBooker = User.builder().id(2L).name("lastBooker").email("lastbooker@test.test").build();
        User nextBooker = User.builder().id(3L).name("nextBooker").email("nextbooker@test.test").build();
        Item item = Item.builder().id(itemId).name("testItem").description("test description")
                .owner(user).build();
        List<Comment> comments = List.of(Comment.builder().id(1L).text("test text").author(lastBooker).build());
        Booking lastBooking = Booking.builder().id(1L).item(item).status(BookingStatus.APPROVED)
                .booker(lastBooker).build();
        Booking nextBooking = Booking.builder().id(2L).item(item).status(BookingStatus.APPROVED)
                .booker(nextBooker).build();
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findByItemId(item.getId())).thenReturn(comments);
        Mockito.when(bookingRepository
                .findFirstByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(anyLong(), any(), any()))
                .thenReturn(lastBooking);
        Mockito.when(bookingRepository
                .findFirstByItemIdAndStatusAndStartIsAfterOrderByStartAsc(anyLong(), any(), any()))
                .thenReturn(nextBooking);

        ItemDto itemDto = itemService.getItemById(userId, itemId);

        assertEquals(itemId, itemDto.getId());
        assertEquals(lastBooking.getId(), itemDto.getLastBooking().getId());
        assertEquals(nextBooking.getId(), itemDto.getNextBooking().getId());
        Mockito.verify(commentRepository, Mockito.times(1)).findByItemId(anyLong());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findFirstByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findFirstByItemIdAndStatusAndStartIsAfterOrderByStartAsc(anyLong(), any(), any());
    }

    @Test
    void getItemByIdTest_whenItemExistAndUserIsNotOwner_thenReturnedItemDto() {
        Long userId = 1L;
        Long itemId = 1L;
        User user = User.builder().id(3L).name("name").email("test@test.test").build();
        Item item = Item.builder().id(itemId).name("testItem").description("test description")
                .owner(user).build();
        List<Comment> comments = List.of(Comment.builder().id(1L).text("test text")
                .author(User.builder().id(1L).build()).build());
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findByItemId(item.getId())).thenReturn(comments);

        ItemDto itemDto = itemService.getItemById(userId, itemId);

        assertEquals(itemId, itemDto.getId());
        Mockito.verify(commentRepository, Mockito.times(1)).findByItemId(anyLong());
        Mockito.verify(bookingRepository, Mockito.never())
                .findFirstByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findFirstByItemIdAndStatusAndStartIsAfterOrderByStartAsc(anyLong(), any(), any());
    }

    @Test
    void getItemsByUserId_whenUserNotExist_thenNotFoundException() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 2;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.getItemsByUserId(userId, from, size));
        Mockito.verify(itemRepository, Mockito.never()).findByOwnerId(anyLong(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findFirstByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findFirstByItemIdAndStatusAndStartIsAfterOrderByStartAsc(anyLong(), any(), any());
    }

    @Test
    void getItemsByUserId_whenItemRepositoryIsEmpty_thenEmptyList() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 2;
        User user = User.builder().id(3L).name("name").email("test@test.test").build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findByOwnerId(anyLong(), any())).thenReturn(Page.empty());

        List<ItemDto> itemsDto = itemService.getItemsByUserId(userId, from, size);

        assertTrue(itemsDto.isEmpty());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(itemRepository, Mockito.times(1)).findByOwnerId(anyLong(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findFirstByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.never())
                .findFirstByItemIdAndStatusAndStartIsAfterOrderByStartAsc(anyLong(), any(), any());
    }

    @Test
    void getItemsByUserId_whenFromIs0AndSizeIs2_thenListSize1() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 2;
        User user = User.builder().id(userId).name("name").email("test@test.test").build();
        User firstLastBooker = User.builder().id(2L).name("lastBooker").email("lastbooker@test.test").build();
        User firstNextBooker = User.builder().id(3L).name("nextBooker").email("nextbooker@test.test").build();
        Item item = Item.builder().id(1L).name("testItem").description("test description")
                .owner(user).build();
        List<Item> items = List.of(item);
        Booking lastBooking = Booking.builder().id(1L).item(item).status(BookingStatus.APPROVED)
                .booker(firstLastBooker).build();
        Booking nextBooking = Booking.builder().id(2L).item(item).status(BookingStatus.APPROVED)
                .booker(firstNextBooker).build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findByOwnerId(anyLong(), any())).thenReturn(new PageImpl<>(items));
        Mockito.when(bookingRepository
                        .findFirstByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(anyLong(), any(), any()))
                .thenReturn(lastBooking);
        Mockito.when(bookingRepository
                        .findFirstByItemIdAndStatusAndStartIsAfterOrderByStartAsc(anyLong(), any(), any()))
                .thenReturn(nextBooking);

        List<ItemDto> itemsDto = itemService.getItemsByUserId(userId, from, size);

        assertFalse(itemsDto.isEmpty());
        assertEquals(1, itemsDto.size());
        assertEquals(item.getId(), itemsDto.get(0).getId());
        assertEquals(lastBooking.getId(), itemsDto.get(0).getLastBooking().getId());
        assertEquals(nextBooking.getId(), itemsDto.get(0).getNextBooking().getId());
        Mockito.verify(itemRepository, Mockito.times(1)).findByOwnerId(anyLong(), any());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findFirstByItemIdAndStatusAndStartIsBeforeOrderByEndDesc(anyLong(), any(), any());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findFirstByItemIdAndStatusAndStartIsAfterOrderByStartAsc(anyLong(), any(), any());
    }

    @Test
    void searchTest_whenTextBlank_thenReturnedEmptyList() {
        String text = "";
        Integer from = 0;
        Integer size = 2;

        List<ItemDto> itemsDto = itemService.search(text, from, size);

        assertTrue(itemsDto.isEmpty());
        Mockito.verify(itemRepository, Mockito.never()).search(any(), any());
    }

    @Test
    void searchTest_whenTextNotBlankAndRepositoryEmpty_thenReturnedEmptyList() {
        String text = "text";
        Integer from = 0;
        Integer size = 2;
        Mockito.when(itemRepository.search(any(), any())).thenReturn(Page.empty());

        List<ItemDto> itemsDto = itemService.search(text, from, size);

        assertTrue(itemsDto.isEmpty());
        Mockito.verify(itemRepository, Mockito.times(1)).search(any(), any());
    }

    @Test
    void searchTest_whenTextNotBlankAndMatch_thenReturnedList() {
        String text = "text";
        Integer from = 0;
        Integer size = 2;
        User user = User.builder().id(1L).name("name").email("test@test.test").build();
        Item item = Item.builder().id(1L).name("testItem").description("test description")
                .owner(user).build();
        List<Item> items = List.of(item);
        Mockito.when(itemRepository.search(any(), any())).thenReturn(new PageImpl<>(items));

        List<ItemDto> itemsDto = itemService.search(text, from, size);

        assertFalse(itemsDto.isEmpty());
        assertEquals(item.getId(), itemsDto.get(0).getId());
        Mockito.verify(itemRepository, Mockito.times(1)).search(any(), any());
    }

    @Test
    void createTest_whenUserNotExist_thenNotFoundException() {
        Long userId = 1L;
        ItemDto itemDtoToCreate = ItemDto.builder().name("testItem").description("test description").build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(userId, itemDtoToCreate));
        Mockito.verify(itemRepository, Mockito.never()).save(any());
        Mockito.verify(itemRequestRepository, Mockito.never()).findById(anyLong());
    }

    @Test
    void createTest_whenItemRequestNotExist_thenNotFoundException() {
        Long userId = 1L;
        User user = User.builder().id(userId).name("name").email("test@test.test").build();
        ItemDto itemDtoToCreate = ItemDto.builder().name("testItem")
                .description("test description").requestId(1L).build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.create(userId, itemDtoToCreate));
        Mockito.verify(itemRepository, Mockito.never()).save(any());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(anyLong());
    }

    @Test
    void createTest_whenUserExistAndItemRequestExist_thenReturnedItemDto() {
        Long userId = 1L;
        User user = User.builder().id(userId).name("name").email("test@test.test").build();
        ItemRequest itemRequest = ItemRequest.builder().id(1L).description("test description")
                .created(LocalDateTime.now()).requestor(User.builder().id(2L).build()).build();
        ItemDto itemDtoToCreate = ItemDto.builder().name("testItem")
                .description("test description").requestId(itemRequest.getId()).build();
        Item savedItem = Item.builder().id(1L).name("testItem")
                .description("test description").request(itemRequest).build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(anyLong())).thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.save(any())).thenReturn(savedItem);

        ItemDto itemDto = itemService.create(userId, itemDtoToCreate);

        assertEquals(itemDtoToCreate.getName(), itemDto.getName());
        assertEquals(itemDtoToCreate.getDescription(), itemDto.getDescription());
        assertEquals(itemDtoToCreate.getRequestId(), itemDto.getRequestId());
        Mockito.verify(itemRepository, Mockito.times(1)).save(any());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(itemRequestRepository, Mockito.times(1)).findById(anyLong());
    }

    @Test
    void updateTest_whenItemAllFieldAreNull_thenValidationException() {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDto itemDtoToUpdate = ItemDto.builder().build();

        assertThrows(ValidationException.class, () -> itemService.update(itemId, userId, itemDtoToUpdate));
        Mockito.verify(userRepository, Mockito.never()).findById(anyLong());
        Mockito.verify(itemRepository, Mockito.never()).findById(anyLong());
        Mockito.verify(itemRepository, Mockito.never()).save(any());
    }

    @Test
    void updateTest_whenUserNotExist_thenNotFoundException() {
        Long itemId = 1L;
        Long userId = 1L;
        ItemDto itemDtoToUpdate = ItemDto.builder().name("test").description("test description").build();
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(itemId, userId, itemDtoToUpdate));
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(itemRepository, Mockito.never()).findById(anyLong());
        Mockito.verify(itemRepository, Mockito.never()).save(any());
    }

    @Test
    void updateTest_whenItemNotExist_thenNotFoundException() {
        Long itemId = 1L;
        Long userId = 1L;
        User user = User.builder().id(userId).name("name").email("test@test.test").build();
        ItemDto itemDtoToUpdate = ItemDto.builder().name("test").description("test description").build();
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.update(itemId, userId, itemDtoToUpdate));
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(itemRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(itemRepository, Mockito.never()).save(any());
    }

    @Test
    void updateTest_whenUserNotOwner_thenAccessException() {
        Long itemId = 1L;
        Long userId = 1L;
        User user = User.builder().id(userId).name("name").email("test@test.test").build();
        User owner = User.builder().id(2L).name("name1").email("test1@test.test").build();
        ItemDto itemDtoToUpdate = ItemDto.builder().name("test").description("test description").build();
        Item item = Item.builder().id(itemId).name("test2").description("test description2").owner(owner).build();
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(AccessException.class, () -> itemService.update(itemId, userId, itemDtoToUpdate));
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(itemRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(itemRepository, Mockito.never()).save(any());
    }

    @Test
    void updateTest_whenAllValidAndExist_thenUpdatedItemDto() {
        Long itemId = 1L;
        Long userId = 1L;
        User user = User.builder().id(userId).name("name").email("test@test.test").build();
        ItemDto itemDtoToUpdate = ItemDto.builder().name("test").description("test description").build();
        Item item = Item.builder().id(itemId).name("test2").description("test description2").owner(user).build();
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(itemRepository.save(any())).thenReturn(item);

        ItemDto itemDtoUpdated = itemService.update(itemId, userId, itemDtoToUpdate);

        assertEquals(itemDtoToUpdate.getName(), itemDtoUpdated.getName());
        assertEquals(itemDtoToUpdate.getDescription(), itemDtoUpdated.getDescription());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(itemRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(itemRepository, Mockito.times(1)).save(any());
    }

    @Test
    void deleteTest_whenItemNotExist_thenNotFoundException() {
        Long itemId = 1L;
        Long userId = 1L;
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.delete(itemId, userId));
        Mockito.verify(itemRepository, Mockito.never()).deleteById(anyLong());
    }

    @Test
    void deleteTest_whenUserNotOwner_thenAccessException() {
        Long itemId = 1L;
        Long userId = 1L;
        User owner = User.builder().id(2L).name("name1").email("test1@test.test").build();
        Item item = Item.builder().id(itemId).name("test2").description("test description2").owner(owner).build();
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        assertThrows(AccessException.class, () -> itemService.delete(itemId, userId));
        Mockito.verify(itemRepository, Mockito.never()).deleteById(anyLong());
    }

    @Test
    void deleteTest_whenItemExistAndUserIsOwner_thenItemDeleted() {
        Long itemId = 1L;
        Long userId = 1L;
        User owner = User.builder().id(userId).name("name1").email("test1@test.test").build();
        Item item = Item.builder().id(itemId).name("test2").description("test description2").owner(owner).build();
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));

        itemService.delete(itemId, userId);

        Mockito.verify(itemRepository, Mockito.times(1)).deleteById(anyLong());
    }

    @Test
    void createCommentTest_whenTextIsBlank_thenValidationException() {
        Long itemId = 1L;
        Long userId = 1L;
        CommentDto commentDtoToCreate = CommentDto.builder().text("").build();

        assertThrows(ValidationException.class, () -> itemService.createComment(userId, itemId, commentDtoToCreate));
        Mockito.verify(itemRepository, Mockito.never()).findById(anyLong());
        Mockito.verify(userRepository, Mockito.never()).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndItemIdAndStatusAndEndIsBefore(anyLong(), anyLong(), any(), any());
        Mockito.verify(commentRepository, Mockito.never()).save(any());
    }

    @Test
    void createCommentTest_whenItemNotExist_thenNotFoundException() {
        Long itemId = 1L;
        Long userId = 1L;
        CommentDto commentDtoToCreate = CommentDto.builder().text("comment test").build();
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createComment(userId, itemId, commentDtoToCreate));
        Mockito.verify(itemRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(userRepository, Mockito.never()).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndItemIdAndStatusAndEndIsBefore(anyLong(), anyLong(), any(), any());
        Mockito.verify(commentRepository, Mockito.never()).save(any());
    }

    @Test
    void createCommentTest_whenUserNotExist_thenNotFoundException() {
        Long itemId = 1L;
        Long userId = 1L;
        User owner = User.builder().id(2L).name("name1").email("test1@test.test").build();
        CommentDto commentDtoToCreate = CommentDto.builder().text("comment test").build();
        Item item = Item.builder().id(itemId).name("test2").description("test description2").owner(owner).build();
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemService.createComment(userId, itemId, commentDtoToCreate));
        Mockito.verify(itemRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.never())
                .findByBookerIdAndItemIdAndStatusAndEndIsBefore(anyLong(), anyLong(), any(), any());
        Mockito.verify(commentRepository, Mockito.never()).save(any());
    }

    @Test
    void createCommentTest_whenBookingNotExist_thenNotAvailableException() {
        Long itemId = 1L;
        Long userId = 1L;
        User user = User.builder().id(userId).name("name").email("test@test.test").build();
        User owner = User.builder().id(2L).name("name1").email("test1@test.test").build();
        CommentDto commentDtoToCreate = CommentDto.builder().text("comment test").build();
        Item item = Item.builder().id(itemId).name("test2").description("test description2").owner(owner).build();
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository
                .findByBookerIdAndItemIdAndStatusAndEndIsBefore(anyLong(), any(), any(), any()))
                .thenReturn(Collections.emptyList());

        assertThrows(NotAvailableException.class, () -> itemService.createComment(userId, itemId, commentDtoToCreate));
        Mockito.verify(itemRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndItemIdAndStatusAndEndIsBefore(anyLong(), anyLong(), any(), any());
        Mockito.verify(commentRepository, Mockito.never()).save(any());
    }

    @Test
    void createCommentTest_whenAllValidAndExist_thenReturnedCommentDto() {
        Long itemId = 1L;
        Long userId = 1L;
        User user = User.builder().id(userId).name("name").email("test@test.test").build();
        User owner = User.builder().id(2L).name("name1").email("test1@test.test").build();
        CommentDto commentDtoToCreate = CommentDto.builder().text("comment test").build();
        Comment savedComment = Comment.builder().id(1L).text("comment test").author(user).build();
        Item item = Item.builder().id(itemId).name("test2").description("test description2").owner(owner).build();
        Booking booking = Booking.builder().id(1L).item(item).status(BookingStatus.APPROVED)
                .booker(user).build();
        Mockito.when(itemRepository.findById(anyLong())).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));
        Mockito.when(bookingRepository
                        .findByBookerIdAndItemIdAndStatusAndEndIsBefore(anyLong(), any(), any(), any()))
                .thenReturn(List.of(booking));
        Mockito.when(commentRepository.save(any())).thenReturn(savedComment);

        CommentDto commentDto = itemService.createComment(userId, itemId, commentDtoToCreate);

        assertEquals(commentDtoToCreate.getText(), commentDto.getText());
        Mockito.verify(itemRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(userRepository, Mockito.times(1)).findById(anyLong());
        Mockito.verify(bookingRepository, Mockito.times(1))
                .findByBookerIdAndItemIdAndStatusAndEndIsBefore(anyLong(), anyLong(), any(), any());
        Mockito.verify(commentRepository, Mockito.times(1)).save(any());
    }
}