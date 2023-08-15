package ru.practicum.shareit.user;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.validator.UserDtoValidator;

import javax.validation.Valid;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
public class UserController {
    private final UserClient client;

    @GetMapping
    public ResponseEntity<Object> getAll() {
        return client.getAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> getById(@PathVariable("id") Long id) {
        return client.getById(id);
    }

    @PostMapping
    public ResponseEntity<Object> create(@RequestBody UserDto userDto) {
        UserDtoValidator.validateUserDto(userDto);
        return client.create(userDto);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable("id") Long id, @Valid @RequestBody UserDto userDto) {
        if (userDto.getEmail() != null) {
            UserDtoValidator.validateEmail(userDto);
        }
        if (userDto.getName() != null) {
            UserDtoValidator.validateName(userDto);
        }
        return client.update(id, userDto);
    }

    @DeleteMapping("/{id}")
    public void deleteById(@PathVariable("id") Long id) {
        client.deleteById(id);
    }
}
