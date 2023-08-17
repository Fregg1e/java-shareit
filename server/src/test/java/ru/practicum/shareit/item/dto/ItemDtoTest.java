package ru.practicum.shareit.item.dto;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.io.IOException;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemDtoTest {
    @Autowired
    private JacksonTester<ItemDto> jacksonTester;

    @Test
    void testItemDto() throws IOException {
        ItemDto itemDto = ItemDto.builder().id(1L).name("1").description("1").available(true).build();

        JsonContent<ItemDto> jsonContent = jacksonTester.write(itemDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.name").isEqualTo("1");
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("1");
        assertThat(jsonContent).extractingJsonPathBooleanValue("$.available").isEqualTo(true);
    }
}