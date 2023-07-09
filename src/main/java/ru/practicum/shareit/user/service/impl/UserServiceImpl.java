package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.service.UserService;
import ru.practicum.shareit.user.storage.UserStorage;
import ru.practicum.shareit.validator.UserDtoValidator;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserStorage userStorage;
    private final UserMapper userMapper;

    @Override
    public List<UserDto> getAll() {
        return userStorage.getAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    public UserDto getById(Long id) {
        return userMapper.toUserDto(userStorage.getById(id));
    }

    @Override
    public UserDto create(UserDto userDto) {
        UserDtoValidator.validateUserDto(userDto);
        User user = userMapper.toUser(userDto);
        log.debug("Создан пользователь: {}", user);
        return userMapper.toUserDto(userStorage.create(user));
    }

    @Override
    public UserDto update(Long id, UserDto userDto) {
        UserDtoValidator.validateAllFieldNotNull(userDto);
        UserDto updatebleUserDto = getById(id);
        if (userDto.getName() != null) {
            UserDtoValidator.validateName(userDto);
            updatebleUserDto.setName(userDto.getName());
        }
        if (userDto.getEmail() != null) {
            UserDtoValidator.validateEmail(userDto);
            updatebleUserDto.setEmail(userDto.getEmail());
        }
        log.debug("Пользователь с id={} обновлен.", updatebleUserDto.getId());
        return userMapper.toUserDto(userStorage.update(id, userMapper.toUser(updatebleUserDto)));
    }

    @Override
    public void deleteById(Long id) {
        userStorage.deleteById(id);
        log.debug("Пользователь с id={} удален.", id);
    }
}
