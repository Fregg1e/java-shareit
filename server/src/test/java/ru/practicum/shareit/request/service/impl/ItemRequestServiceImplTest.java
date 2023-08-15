package ru.practicum.shareit.request.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.mapper.impl.ItemMapperImpl;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.mapper.impl.ItemRequestMapperImpl;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
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
class ItemRequestServiceImplTest {
    @Mock
    ItemRequestRepository itemRequestRepository;
    @Mock
    UserRepository userRepository;
    @Mock
    ItemRepository itemRepository;
    @Spy
    ItemRequestMapper itemRequestMapper = new ItemRequestMapperImpl();
    @Spy
    ItemMapper itemMapper = new ItemMapperImpl();
    @InjectMocks
    ItemRequestServiceImpl itemRequestService;

    @Test
    void createTest_whenUserExist_thenCreateRequest() {
        Long userId = 1L;
        LocalDateTime created = LocalDateTime.now();
        User user = User.builder().id(1L).name("test").email("test@test.test").build();
        ItemRequestDto itemRequestDtoToCreate = ItemRequestDto.builder().description("test description").build();
        ItemRequest itemRequestToCreate = ItemRequest.builder().description("test description")
                .created(created).build();
        ItemRequest createdItemRequest = ItemRequest.builder().id(1L).description("test description")
                .created(created).requestor(user).build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.save(any())).thenReturn(createdItemRequest);

        ItemRequestDto createdItemRequestDto = itemRequestService.create(userId, itemRequestDtoToCreate);

        assertEquals(1L, createdItemRequestDto.getId());
        assertEquals(itemRequestDtoToCreate.getDescription(), createdItemRequestDto.getDescription());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(itemRequestRepository, Mockito.times(1)).save(any());
    }

    @Test
    void createTest_whenUserNotExist_thenNotFoundException() {
        Long userId = 1L;
        ItemRequestDto itemRequestDtoToCreate = ItemRequestDto.builder().description("test description").build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.create(userId, itemRequestDtoToCreate));
        Mockito.verify(itemRequestRepository, Mockito.never()).save(any());
    }

    @Test
    void getRequestsByUserIdTest_whenUserFoundedAndRequestsRepositoryEmpty_thenReturnedEmptyList() {
        Long userId = 1L;
        User user = User.builder().id(1L).name("test").email("test@test.test").build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId))
                .thenReturn(Collections.emptyList());

        List<ItemRequestDto> itemRequestsDto = itemRequestService.getRequestsByUserId(userId);

        assertTrue(itemRequestsDto.isEmpty());
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findByRequestorIdOrderByCreatedDesc(userId);
        Mockito.verify(itemRepository, Mockito.never()).findByRequestId(anyLong());
    }

    @Test
    void getRequestsByUserIdTest_whenRequestsFound_thenReturnedRequestsList() {
        Long userId = 1L;
        User user = User.builder().id(1L).name("test").email("test@test.test").build();
        ItemRequest firstItemRequest = ItemRequest.builder().id(1L).build();
        ItemRequest secondItemRequest = ItemRequest.builder().id(2L).build();
        List<ItemRequest> itemRequests = List.of(firstItemRequest, secondItemRequest);
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findByRequestorIdOrderByCreatedDesc(userId))
                .thenReturn(itemRequests);
        Mockito.when(itemRepository.findByRequestId(anyLong())).thenReturn(Collections.emptyList());

        List<ItemRequestDto> itemRequestsDto = itemRequestService.getRequestsByUserId(userId);

        assertEquals(2, itemRequestsDto.size());
        assertEquals(1, itemRequestsDto.get(0).getId());
        assertEquals(2, itemRequestsDto.get(1).getId());
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findByRequestorIdOrderByCreatedDesc(userId);
        Mockito.verify(itemRepository, Mockito.times(2)).findByRequestId(anyLong());
    }

    @Test
    void getRequestsByUserIdTest_whenUserNotExist_thenNotFoundException() {
        Long userId = 1L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestsByUserId(userId));
        Mockito.verify(itemRequestRepository, Mockito.never()).findByRequestorIdOrderByCreatedDesc(any());
        Mockito.verify(itemRepository, Mockito.never()).findByRequestId(anyLong());
    }

    @Test
    void getAllRequestsTest_whenUserFoundedAndFromIs0AndSizeIs3_thenReturnedListSize3() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 2;
        ItemRequest firstItemRequest = ItemRequest.builder().id(1L).build();
        ItemRequest secondItemRequest = ItemRequest.builder().id(2L).build();
        ItemRequest thirdItemRequest = ItemRequest.builder().id(3L).build();
        List<ItemRequest> itemRequests = List.of(firstItemRequest, secondItemRequest, thirdItemRequest);
        Mockito.when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(anyLong(),
                any())).thenReturn(itemRequests);
        Mockito.when(itemRepository.findByRequestId(anyLong())).thenReturn(Collections.emptyList());

        List<ItemRequestDto> itemRequestsDto = itemRequestService.getAllRequests(userId, from, size);

        assertEquals(3, itemRequestsDto.size());
        assertEquals(1, itemRequestsDto.get(0).getId());
        assertEquals(2, itemRequestsDto.get(1).getId());
        assertEquals(3, itemRequestsDto.get(2).getId());
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findByRequestorIdNotOrderByCreatedDesc(anyLong(), any());
        Mockito.verify(itemRepository, Mockito.times(3)).findByRequestId(anyLong());
    }

    @Test
    void getAllRequestsTest_whenRepositoryIsEmptyAndFromIs0AndSizeIs3_thenReturnedEmptyList() {
        Long userId = 1L;
        Integer from = 0;
        Integer size = 2;
        Mockito.when(itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(anyLong(),
                any())).thenReturn(Collections.emptyList());

        List<ItemRequestDto> itemRequestsDto = itemRequestService.getAllRequests(userId, from, size);

        assertTrue(itemRequestsDto.isEmpty());
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findByRequestorIdNotOrderByCreatedDesc(anyLong(), any());
        Mockito.verify(itemRepository, Mockito.never()).findByRequestId(anyLong());
    }

    @Test
    void getRequestByIdTest_whenUserAndRequestIsExist_thenReturnedRequest() {
        Long userId = 1L;
        Long requestId = 1L;
        User user = User.builder().id(1L).name("test").email("test@test.test").build();
        ItemRequest itemRequest = ItemRequest.builder().id(1L).build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.of(itemRequest));
        Mockito.when(itemRepository.findByRequestId(anyLong())).thenReturn(Collections.emptyList());

        ItemRequestDto itemRequestDto = itemRequestService.getRequestById(userId, requestId);

        assertEquals(1, itemRequestDto.getId());
        Mockito.verify(itemRequestRepository, Mockito.times(1))
                .findById(requestId);
        Mockito.verify(itemRepository, Mockito.times(1)).findByRequestId(anyLong());
    }

    @Test
    void getRequestByIdTest_whenUserNotExist_thenNotFoundException() {
        Long userId = 1L;
        Long requestId = 1L;
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(userId, requestId));
        Mockito.verify(itemRequestRepository, Mockito.never()).findById(any());
        Mockito.verify(itemRepository, Mockito.never()).findByRequestId(anyLong());
    }

    @Test
    void getRequestByIdTest_whenRequestNotExist_thenNotFoundException() {
        Long userId = 1L;
        Long requestId = 1L;
        User user = User.builder().id(1L).name("test").email("test@test.test").build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.empty());
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(requestId))
                .thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> itemRequestService.getRequestById(userId, requestId));
        Mockito.verify(itemRepository, Mockito.never()).findByRequestId(anyLong());
    }
}