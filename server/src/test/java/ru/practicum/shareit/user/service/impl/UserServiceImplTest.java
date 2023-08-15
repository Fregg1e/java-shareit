package ru.practicum.shareit.user.service.impl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import ru.practicum.shareit.exception.model.AlreadyExistException;
import ru.practicum.shareit.exception.model.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.mapper.impl.UserMapperImpl;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @Mock
    private UserRepository userRepository;
    @Spy
    private UserMapper userMapper = new UserMapperImpl();
    @InjectMocks
    private UserServiceImpl userService;

    @Test
    void getAllTest_whenRepositoryEmpty_thenReturnedEmpty() {
        Mockito.when(userRepository.findAll()).thenReturn(Collections.emptyList());

        List<UserDto> users = userService.getAll();

        assertTrue(users.isEmpty());
    }

    @Test
    void getAllTest_whenRepositoryNotEmpty_thenReturnedListWithUserDto() {
        List<User> users = List.of(new User(), new User());
        Mockito.when(userRepository.findAll()).thenReturn(users);

        List<UserDto> usersDto = userService.getAll();

        assertEquals(2, usersDto.size());
    }

    @Test
    void getByIdTest_whenUserFound_thenReturnedUserDto() {
        User user = User.builder().id(1L).email("test@test.test").name("test").build();
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.of(user));

        UserDto userDto = userService.getById(1L);

        assertEquals(user.getId(), userDto.getId());
        assertEquals(user.getEmail(), userDto.getEmail());
        assertEquals(user.getName(), userDto.getName());
    }

    @Test
    void getByIdTest_whenUserNotFound_thenNotFoundException() {
        Mockito.when(userRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> userService.getById(1L));
    }

    @Test
    void createTest_whenUserValidAndEmailNotAlreadyExists_thenSaveUser() {
        UserDto userDtoToSave = UserDto.builder().name("test").email("test@test.test").build();
        User userToSave = User.builder().name("test").email("test@test.test").build();
        User savedUser = User.builder().id(1L).name("test").email("test@test.test").build();
        Mockito.when(userRepository.save(userToSave)).thenReturn(savedUser);

        UserDto savedUserDto = userService.create(userDtoToSave);

        assertEquals(1L, savedUserDto.getId());
        assertEquals("test", savedUserDto.getName());
        assertEquals("test@test.test", savedUserDto.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).save(userToSave);
    }

    @Test
    void createTest_whenUserEmailAlreadyExist_thenAlreadyExistException() {
        UserDto userDtoToSave = UserDto.builder().name("test").email("test@test.test").build();
        User userToSave = User.builder().name("test").email("test@test.test").build();
        Mockito.when(userRepository.save(userToSave)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(AlreadyExistException.class, () -> userService.create(userDtoToSave));
        Mockito.verify(userRepository, Mockito.times(1)).save(userToSave);
    }

    @Test
    void updateTest_whenUpdateEmail_thenUserUpdateWithNewEmail() {
        Long userId = 1L;
        UserDto userDtoToUpdate = UserDto.builder().email("test-update@test.test").build();
        User userToUpdate = User.builder().email("test-update@test.test").build();
        User userBeforeUpdate = User.builder().id(1L).name("test").email("test@test.test").build();
        User updatedUser = User.builder().id(1L).name("test").email("test-update@test.test").build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(userBeforeUpdate));
        Mockito.when(userRepository.save(userToUpdate)).thenReturn(updatedUser);

        UserDto updatedUserDto = userService.update(userId, userDtoToUpdate);

        assertEquals(userId, updatedUserDto.getId());
        assertEquals(userBeforeUpdate.getName(), updatedUserDto.getName());
        assertEquals(userDtoToUpdate.getEmail(), updatedUserDto.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(userRepository, Mockito.times(1)).save(userToUpdate);
    }

    @Test
    void updateTest_whenUpdateName_thenUserUpdateWithNewName() {
        Long userId = 1L;
        UserDto userDtoToUpdate = UserDto.builder().name("test_update").build();
        User userToUpdate = User.builder().id(1L).name("test").email("test@test.test").build();
        User updatedUser = User.builder().id(1L).name("test_update").email("test@test.test").build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(userToUpdate));
        Mockito.when(userRepository.save(userToUpdate)).thenReturn(updatedUser);

        UserDto updatedUserDto = userService.update(userId, userDtoToUpdate);

        assertEquals(userId, updatedUserDto.getId());
        assertEquals(userDtoToUpdate.getName(), updatedUserDto.getName());
        assertEquals(userToUpdate.getEmail(), updatedUserDto.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(userRepository, Mockito.times(1)).save(userToUpdate);
    }

    @Test
    void updateTest_whenUpdateNameAndEmail_thenUserUpdateWithNewNameAndNewEmail() {
        Long userId = 1L;
        UserDto userDtoToUpdate = UserDto.builder().name("test_update").email("test-update@test.test").build();
        User userToUpdate = User.builder().id(1L).name("test").email("test@test.test").build();
        User updatedUser = User.builder().id(1L).name("test_update").email("test-update@test.test").build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(userToUpdate));
        Mockito.when(userRepository.save(userToUpdate)).thenReturn(updatedUser);

        UserDto updatedUserDto = userService.update(userId, userDtoToUpdate);

        assertEquals(userId, updatedUserDto.getId());
        assertEquals(userDtoToUpdate.getName(), updatedUserDto.getName());
        assertEquals(userToUpdate.getEmail(), updatedUserDto.getEmail());
        Mockito.verify(userRepository, Mockito.times(1)).findById(userId);
        Mockito.verify(userRepository, Mockito.times(1)).save(userToUpdate);
    }

    @Test
    void updateTest_whenUpdateExistEmail_thenAlreadyExistException() {
        Long userId = 1L;
        UserDto userDtoToUpdate = UserDto.builder().name("test_update").email("exist@mail.test").build();
        User userToUpdate = User.builder().id(1L).name("test").email("test@test.test").build();
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(userToUpdate));
        Mockito.when(userRepository.save(userToUpdate)).thenThrow(DataIntegrityViolationException.class);

        assertThrows(AlreadyExistException.class, () -> userService.update(userId, userDtoToUpdate));
        Mockito.verify(userRepository, Mockito.times(1)).save(any());
    }

    @Test
    void updateTest_whenUserNotExist_thenNotFoundException() {
        Long userId = 1L;
        UserDto userDtoToUpdate = UserDto.builder().name("test_update").build();
        Mockito.when(userRepository.findById(userId)).thenThrow(NotFoundException.class);

        assertThrows(NotFoundException.class, () -> userService.update(userId, userDtoToUpdate));
        Mockito.verify(userRepository, Mockito.never()).save(any());
    }

    @Test
    void deleteTest_whenDeleteById_thenDeleteByIdExecuted() {
        Long userId = 1L;

        userService.deleteById(userId);

        Mockito.verify(userRepository, Mockito.times(1)).deleteById(userId);
    }
}