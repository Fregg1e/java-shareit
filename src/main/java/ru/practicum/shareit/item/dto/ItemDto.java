package ru.practicum.shareit.item.dto;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@Builder
public class ItemDto {
    @EqualsAndHashCode.Exclude
    private Long id;
    @NotBlank(message = "Название не может быть пустым.")
    private String name;
    @Size(max = 200, message = "Описание не может быть больше 200 символов.")
    private String description;
    @EqualsAndHashCode.Exclude
    private Boolean available;
    private User owner;
    private ItemRequest request;
}
