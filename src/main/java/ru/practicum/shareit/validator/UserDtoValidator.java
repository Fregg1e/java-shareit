package ru.practicum.shareit.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

@Slf4j
public class UserDtoValidator {
    public static void validateUserDto(UserDto userDto) {
        validateEmail(userDto);
        validateName(userDto);
    }

    public static void validateName(UserDto userDto) {
        String name = userDto.getName();
        if (name.isEmpty() || name.isBlank()) {
            logAndThrowException("Произошло исключение! Имя не может быть пустым.");
        }
    }

    public static void validateEmail(UserDto userDto) {
        String email = userDto.getEmail();
        if (email == null || email.isEmpty() || email.isBlank() || !email.contains("@")) {
            logAndThrowException("Произошло исключение! Неправильный email.");
        }
    }

    public static void validateAllFieldNotNull(UserDto userDto) {
        if (userDto.getName() == null && userDto.getEmail() == null) {
            logAndThrowException("\"Произошло исключение! Все поля пустые.");
        }
    }

    private static void logAndThrowException(String message) {
        log.error(message);
        throw new ValidationException(message);
    }
}
