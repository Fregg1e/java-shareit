package ru.practicum.shareit.user.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class UserDtoTest {
    @Autowired
    private JacksonTester<UserDto> jacksonTester;

    @Test
    void testUserDto() throws IOException {
        UserDto userDto = UserDto.builder()
                .id(1L)
                .email("test@test.test")
                .name("test")
                .build();

        JsonContent<UserDto> jsonContent = jacksonTester.write(userDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.email").isEqualTo("test@test.test");
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("test");
    }
}