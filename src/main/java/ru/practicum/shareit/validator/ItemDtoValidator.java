package ru.practicum.shareit.validator;

import ru.practicum.shareit.exception.model.ValidationException;
import ru.practicum.shareit.item.dto.ItemDto;

public class ItemDtoValidator {

    public static void validateName(ItemDto itemDto) {
        String name = itemDto.getName();
        if (name.isEmpty() || name.isBlank()) {
            throw new ValidationException("Произошло исключение! Название не может быть пустым.");
        }
    }

    public static void validateDescription(ItemDto itemDto) {
        String description = itemDto.getDescription();
        if (description == null || description.isEmpty() || description.isBlank() || description.length() > 200) {
            throw new ValidationException("Произошло исключение! "
                    + "Описание не может быть пустым или больше 200 символов.");
        }
    }

    public static void validateAvailable(ItemDto itemDto) {
        Boolean available = itemDto.getAvailable();
        if (available == null) {
            throw new ValidationException("Произошло исключение! Доступность равняется null.");
        }
    }

    public static void validateAllFieldNotNull(ItemDto itemDto) {
        if (itemDto.getName() == null
                && itemDto.getDescription() == null
                && itemDto.getAvailable() == null) {
            throw new ValidationException("Произошло исключение! Все поля пустые.");
        }
    }
}
