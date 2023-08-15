package ru.practicum.shareit.validator;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

public class UserDtoValidator {
    public static void validateUserDto(UserDto userDto) {
        validateEmail(userDto);
        validateName(userDto);
    }

    public static void validateName(UserDto userDto) {
        String name = userDto.getName();
        if (name.isEmpty() || name.isBlank()) {
            throw new ValidationException("Произошло исключение! Имя не может быть пустым.");
        }
    }

    public static void validateEmail(UserDto userDto) {
        String email = userDto.getEmail();
        if (email == null || email.isEmpty() || email.isBlank() || !email.contains("@")) {
            throw new ValidationException("Произошло исключение! Неправильный email.");
        }
    }
}
