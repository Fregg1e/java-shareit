package ru.practicum.shareit.request.dto;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.item.dto.ItemDto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class ItemRequestDto {
    private Long id;
    @NotBlank(message = "Произошло исключение! Описание не может быть пустым.")
    @Size(max = 200, message = "Произошло исключение! Описание не может быть больше 200 символов.")
    private String description;
    private LocalDateTime created;
    private List<ItemDto> items;
}
