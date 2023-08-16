package ru.practicum.shareit.validator;

import ru.practicum.shareit.exception.ValidationException;
import ru.practicum.shareit.user.dto.UserDto;

import java.util.regex.Pattern;

public class UserDtoValidator {
    public static final Pattern VALID_EMAIL_ADDRESS_REGEX =
            Pattern.compile("^(?=.{1,64}@)[A-Za-z0-9\\+_-]+(\\.[A-Za-z0-9\\+_-]+)*@"
                    + "[^-][A-Za-z0-9\\+-]+(\\.[A-Za-z0-9\\+-]+)*(\\.[A-Za-z]{2,})$", Pattern.CASE_INSENSITIVE);

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
        if (email == null || email.isEmpty() || email.isBlank()
                || !VALID_EMAIL_ADDRESS_REGEX.matcher(email).matches()) {
            throw new ValidationException("Произошло исключение! Неправильный email.");
        }
    }
}
