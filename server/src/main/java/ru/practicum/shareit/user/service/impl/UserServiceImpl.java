package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.model.AlreadyExistException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;
import ru.practicum.shareit.user.service.UserService;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public List<UserDto> getAll() {
        return userRepository.findAll().stream()
                .map(userMapper::toUserDto)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public UserDto getById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с ID = %d не существует.", id)));
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto create(UserDto userDto) {
        User user = userMapper.toUser(userDto);
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistException("Пользователь с таким email уже существует!");
        }
        log.debug("Создан пользователь: {}", user);
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public UserDto update(Long id, UserDto userDto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(String.format("Пользователя с ID = %d не существует.", id)));
        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
        if (userDto.getName() != null) {
            user.setName(userDto.getName());
        }
        try {
            user = userRepository.save(user);
        } catch (DataIntegrityViolationException e) {
            throw new AlreadyExistException("Пользователь с таким email уже существует!");
        }
        log.debug("Пользователь с id={} обновлен.", user.getId());
        return userMapper.toUserDto(user);
    }

    @Override
    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
        log.debug("Пользователь с id={} удален.", id);
    }
}
