package ru.practicum.shareit.validator;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.exception.model.ValidationException;

public class CommentDtoValidator {
    public static void validateText(CommentDto commentDto) {
        String text = commentDto.getText();
        if (text == null || text.isEmpty() || text.isBlank() || text.length() > 500) {
            throw new ValidationException("Произошло исключение! Комментарий не может быть пустым "
                    + "или больше 200 символов.");
        }
    }
}
