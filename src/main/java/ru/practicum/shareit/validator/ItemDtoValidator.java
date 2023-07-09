package ru.practicum.shareit.validator;

import lombok.extern.slf4j.Slf4j;
import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

@Slf4j
public class ItemDtoValidator {
    public static void validateItemDto(ItemDto itemDto) {
        validateName(itemDto);
        validateDescription(itemDto);
        validateAvailable(itemDto);
    }

    public static void validateName(ItemDto itemDto) {
        String name = itemDto.getName();
        if (name.isEmpty() || name.isBlank()) {
            logAndThrowException("Произошло исключение! Название не может быть пустым.");
        }
    }

    public static void validateDescription(ItemDto itemDto) {
        String description = itemDto.getDescription();
        if (description == null || description.isEmpty() || description.isBlank() || description.length() > 200) {
            logAndThrowException("Произошло исключение! Описание не может быть пустым или больше 200 символов.");
        }
    }

    public static void validateAvailable(ItemDto itemDto) {
        Boolean available = itemDto.getAvailable();
        if (available == null) {
            logAndThrowException("Произошло исключение! Доступность равняется null.");
        }
    }

    public static void validateAllFieldNotNull(ItemDto itemDto) {
        if (itemDto.getName() == null
                && itemDto.getDescription() == null
                && itemDto.getAvailable() == null) {
            logAndThrowException("Произошло исключение! Все поля пустые.");
        }
    }

    private static void logAndThrowException(String message) {
        log.error(message);
        throw new ValidationException(message);
    }
}
