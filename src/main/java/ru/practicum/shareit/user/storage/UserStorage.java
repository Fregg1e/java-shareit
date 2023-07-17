package ru.practicum.shareit.user.storage;

import ru.practicum.shareit.user.model.User;

import java.util.List;

public interface UserStorage {
    List<User> getAll();

    User getById(Long id);

    User create(User user);

    User update(User user);

    void deleteById(Long id);

    void checkEmailIsFree(Long userId, User user);
}
