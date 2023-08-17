package ru.practicum.shareit.user.mapper.impl;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;

import static org.junit.jupiter.api.Assertions.*;

class UserMapperImplTest {
    private final UserMapper userMapper = new UserMapperImpl();

    @Test
    void toUserDtoTest() {
        User user = User.builder().id(1L).name("test").email("test@email.test").build();

        UserDto userDto = userMapper.toUserDto(user);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }

    @Test
    void toUserTest() {
        UserDto userDto = UserDto.builder().id(1L).name("test").email("test@email.test").build();

        User user = userMapper.toUser(userDto);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getName(), userDto.getName());
        assertEquals(user.getEmail(), userDto.getEmail());
    }
}