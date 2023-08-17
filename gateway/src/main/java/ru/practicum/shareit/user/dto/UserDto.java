package ru.practicum.shareit.user.dto;

import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;

@Data
@Builder
public class UserDto {
    private Long id;
    private String name;
    @Email(regexp = "^(?=.{1,64}@)[A-Za-z0-9\\+_-]+(\\.[A-Za-z0-9\\+_-]+)*@"
            + "[^-][A-Za-z0-9\\+-]+(\\.[A-Za-z0-9\\+-]+)*(\\.[A-Za-z]{2,})$")
    private String email;

    @AssertTrue(message = "Произошло исключение! Все поля пустые.")
    private boolean isNameOrEmailExists() {
        return name != null || email != null;
    }
}
