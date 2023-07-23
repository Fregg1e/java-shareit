package ru.practicum.shareit.item.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.item.storage.ItemStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InMemoryItemStorage implements ItemStorage {
    private final Map<Long, Item> items;
    private Long id = 1L;

    @Override
    public List<Item> getAll() {
        return new ArrayList<>(items.values());
    }

    @Override
    public Item getItemById(Long itemId) {
        if (!items.containsKey(itemId)) {
            log.error("Произошло исключение! Вещь с ID = {} не существует.", itemId);
            throw new NotFoundException(String.format("Вещь с ID = %d не существует.", itemId));
        }
        return items.get(itemId);
    }

    @Override
    public List<Item> getItemsByUserId(Long userId) {
        return items.values().stream()
                .filter(item -> item.getOwner().getId().equals(userId))
                .collect(Collectors.toList());
    }

    @Override
    public List<Item> search(String text) {
        return items.values().stream()
                .filter(item ->
                    (item.getName().toLowerCase().contains(text.toLowerCase())
                            || item.getDescription().toLowerCase().contains(text.toLowerCase()))
                            && item.getAvailable().equals(true)
                )
                .collect(Collectors.toList());
    }

    @Override
    public Item create(Item item) {
        item.setId(getId());
        items.put(item.getId(), item);
        return item;
    }

    @Override
    public Item update(Long itemId, Item item) {
        if (!items.containsKey(itemId)) {
            log.error("Произошло исключение! Вещь с ID = {} не существует.", itemId);
            throw new NotFoundException(String.format("Вещь с ID = %d не существует.", itemId));
        }
        return item;
    }

    @Override
    public void delete(Long itemId, Long userId) {
        items.remove(itemId);
    }

    public Long getItemOwnerId(Long itemId) {
        if (!items.containsKey(itemId)) {
            log.error("Произошло исключение! Вещь с ID = {} не существует.", itemId);
            throw new NotFoundException(String.format("Вещь с ID = %d не существует.", itemId));
        }
        return items.get(itemId).getOwner().getId();
    }

    private Long getId() {
        return id++;
    }
}
