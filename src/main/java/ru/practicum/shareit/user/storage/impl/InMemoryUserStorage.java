package ru.practicum.shareit.user.storage.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.exception.model.AlreadyExistException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.storage.UserStorage;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Slf4j
@Repository
@RequiredArgsConstructor
public class InMemoryUserStorage implements UserStorage {
    private final Map<Long, User> users;
    private Long id = 1L;

    @Override
    public List<User> getAll() {
        return new ArrayList<>(users.values());
    }

    @Override
    public User getById(Long id) {
        if (!users.containsKey(id)) {
            log.error("Произошло исключение! Пользователя с ID = {} не существует.", id);
            throw new NotFoundException(String.format("Пользователя с ID = %d не существует.", id));
        }
        return users.get(id);
    }

    @Override
    public User create(User user) {
        if (users.containsValue(user)) {
            log.error("Произошло исключение! Пользователь с Email = {} уже существует.", user.getEmail());
            throw new AlreadyExistException(String.format("Пользователь с Email = %s уже существует.",
                                            user.getEmail()));
        }
        user.setId(getId());
        users.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(Long id, User user) {
        if (users.containsValue(user) && !users.get(id).equals(user)) {
            log.error("Произошло исключение! Пользователь с Email = {} уже существует.", user.getEmail());
            throw new AlreadyExistException(String.format("Пользователь с Email = %s уже существует.",
                    user.getEmail()));
        }
        users.put(id, user);
        return user;
    }

    @Override
    public void deleteById(Long id) {
        users.remove(id);
    }

    private Long getId() {
        return id++;
    }
}
