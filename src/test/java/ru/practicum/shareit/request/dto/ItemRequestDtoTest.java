package ru.practicum.shareit.request.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.item.dto.ItemDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class ItemRequestDtoTest {
    @Autowired
    private JacksonTester<ItemRequestDto> jacksonTester;

    @SneakyThrows
    @Test
    void testItemRequestDto() {
        LocalDateTime created = LocalDateTime.now();
        List<ItemDto> items = List.of(
                ItemDto.builder().id(1L).name("1").description("1").available(true).requestId(1L).build(),
                ItemDto.builder().id(2L).name("2").description("2").available(true).requestId(1L).build(),
                ItemDto.builder().id(3L).name("3").description("3").available(true).requestId(1L).build()
        );
        ItemRequestDto itemRequestDto = ItemRequestDto.builder().id(1L).description("1")
                .created(created).items(items).build();

        JsonContent<ItemRequestDto> jsonContent = jacksonTester.write(itemRequestDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.description").isEqualTo("1");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(created.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSSS")));
        assertThat(jsonContent).extractingJsonPathNumberValue("$.items.[0].id").isEqualTo(1);
    }
}