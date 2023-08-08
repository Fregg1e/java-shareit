package ru.practicum.shareit.comment.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class CommentDtoTest {
    @Autowired
    private JacksonTester<CommentDto> jacksonTester;

    @SneakyThrows
    @Test
    void testCommentDto() {
        LocalDateTime created = LocalDateTime.now();
        CommentDto commentDto = CommentDto.builder().id(1L).text("text").authorName("test").created(created).build();

        JsonContent<CommentDto> jsonContent = jacksonTester.write(commentDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.text").isEqualTo("text");
        assertThat(jsonContent).extractingJsonPathStringValue("$.authorName").isEqualTo("test");
        assertThat(jsonContent).extractingJsonPathStringValue("$.created")
                .isEqualTo(created.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SS")));
    }
}