package ru.practicum.shareit.item.storage;

import ru.practicum.shareit.item.model.Item;

import java.util.List;

public interface ItemStorage {
    List<Item> getAll();

    Item getItemById(Long itemId);

    List<Item> getItemsByUserId(Long userId);

    List<Item> search(String text);

    Item create(Item item);

    Item update(Long itemId, Item item);

    void delete(Long itemId, Long userId);

    Long getItemOwnerId(Long itemId);
}
