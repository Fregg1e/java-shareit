package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.AssertTrue;

@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
    private String email;

    @AssertTrue(message = "Произошло исключение! Все поля пустые.")
    private boolean isNameOrEmailExists() {
        return name != null || email != null;
    }
}
