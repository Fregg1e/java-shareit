package ru.practicum.shareit.user.service.impl;

import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.service.UserService;

import javax.persistence.EntityManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
@Transactional
@SpringBootTest(properties = {"spring.datasource.url=jdbc:h2:mem:shareit",
        "spring.datasource.username=test",
        "spring.datasource.password=test"},
        webEnvironment = SpringBootTest.WebEnvironment.NONE)
@RequiredArgsConstructor(onConstructor_ = @Autowired)
class UserServiceImplIntegrationTest {
    private final EntityManager em;
    private final UserService userService;

    @Test
    void createTest() {
        UserDto userDto = makeUserDto("test", "test@test.test");

        UserDto createdUserDto = userService.create(userDto);

        assertNotNull(createdUserDto.getId());
        assertEquals(createdUserDto.getName(), userDto.getName());
        assertEquals(createdUserDto.getEmail(), userDto.getEmail());
    }

    @Test
    void updateTest() {
        UserDto userDto = makeUserDto("test", "test@test.test");
        UserDto userDtoForUpdate = makeUserDto("testUpdate", null);

        UserDto createdUserDto = userService.create(userDto);
        UserDto updatedUserDto = userService.update(createdUserDto.getId(), userDtoForUpdate);

        assertEquals(createdUserDto.getId(), updatedUserDto.getId());
        assertEquals(userDtoForUpdate.getName(), updatedUserDto.getName());
        assertEquals(createdUserDto.getEmail(), updatedUserDto.getEmail());
    }

    @Test
    void getAllTest() {
        List<UserDto> usersDto = List.of(
                makeUserDto("first", "first@test.test"),
                makeUserDto("second", "second@test.test"),
                makeUserDto("third", "third@test.test")
        );
        List<UserDto> createdUsersDto = new ArrayList<>();
        for (UserDto userDto : usersDto) {
            createdUsersDto.add(userService.create(userDto));
        }

        List<UserDto> allUserDto = userService.getAll();

        assertEquals(3, allUserDto.size());
        assertEquals(createdUsersDto.get(0), allUserDto.get(0));
        assertEquals(createdUsersDto.get(1), allUserDto.get(1));
        assertEquals(createdUsersDto.get(2), allUserDto.get(2));
    }

    @Test
    void getByIdTest() {
        UserDto userDto = makeUserDto("test", "test@test.test");
        UserDto createdUserDto = userService.create(userDto);

        UserDto userDtoById = userService.getById(createdUserDto.getId());

        assertNotNull(userDtoById);
        assertEquals(createdUserDto, userDtoById);
    }

    @Test
    void deleteByIdTest() {
        UserDto userDto = makeUserDto("test", "test@test.test");
        UserDto createdUserDto = userService.create(userDto);

        userService.deleteById(createdUserDto.getId());
        List<UserDto> usersDto = userService.getAll();

        assertTrue(usersDto.isEmpty());
    }

    private UserDto makeUserDto(String name, String email) {
        return UserDto.builder()
                .name(name)
                .email(email)
                .build();
    }
}