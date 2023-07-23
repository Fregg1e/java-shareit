package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.comment.dto.CommentDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Data
@Builder
public class ItemDto {
    private Long id;
    @NotBlank(message = "Произошло исключение! Название не может быть пустым.")
    private String name;
    @NotBlank(message = "Произошло исключение! Описание не может быть пустым.")
    @Size(max = 200, message = "Произошло исключение! Описание не может быть больше 200 символов.")
    private String description;
    @NotNull(message = "Произошло исключение! Доступность равняется null.")
    private Boolean available;
    private BookingDto lastBooking;
    private BookingDto nextBooking;
    private List<CommentDto> comments;
}
