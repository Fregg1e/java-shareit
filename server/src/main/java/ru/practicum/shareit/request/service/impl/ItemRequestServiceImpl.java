package ru.practicum.shareit.request.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.item.repository.ItemRepository;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.mapper.ItemRequestMapper;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.request.repository.ItemRequestRepository;
import ru.practicum.shareit.request.service.ItemRequestService;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.utils.OffsetPageRequest;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ItemRequestServiceImpl implements ItemRequestService {
    private final ItemRequestRepository itemRequestRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final ItemRequestMapper itemRequestMapper;
    private final ItemMapper itemMapper;

    @Override
    @Transactional
    public ItemRequestDto create(Long userId, ItemRequestDto itemRequestDto) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с ID = %d "
                        + "не существует.", userId)));
        ItemRequest itemRequest = itemRequestMapper.toItemRequest(itemRequestDto);
        itemRequest.setCreated(LocalDateTime.now());
        itemRequest.setRequestor(user);
        return itemRequestMapper.toItemRequestDto(itemRequestRepository.save(itemRequest));
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getRequestsByUserId(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с ID = %d "
                        + "не существует.", userId)));
        return itemRequestRepository.findByRequestorIdOrderByCreatedDesc(user.getId()).stream()
                .map(itemRequestMapper::toItemRequestDto).peek(this::setItemForRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ItemRequestDto> getAllRequests(Long userId, Integer from, Integer size) {
        return itemRequestRepository.findByRequestorIdNotOrderByCreatedDesc(userId,
                        new OffsetPageRequest(from, size)).stream()
                .map(itemRequestMapper::toItemRequestDto).peek(this::setItemForRequestDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public ItemRequestDto getRequestById(Long userId, Long requestId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с ID = %d "
                        + "не существует.", userId)));
        ItemRequest itemRequest = itemRequestRepository.findById(requestId)
                .orElseThrow(() -> new NotFoundException(String.format("Запроса с ID = %d "
                        + "не существует.", requestId)));
        ItemRequestDto itemRequestDto = itemRequestMapper.toItemRequestDto(itemRequest);
        setItemForRequestDto(itemRequestDto);
        return itemRequestDto;
    }

    private void setItemForRequestDto(ItemRequestDto requestDto) {
        requestDto.setItems(itemRepository.findByRequestId(requestDto.getId()).stream()
                .map(itemMapper::toItemDto)
                .collect(Collectors.toList()));
    }
}
