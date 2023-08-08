package ru.practicum.shareit.booking.dto;

import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.json.JsonTest;
import org.springframework.boot.test.json.JacksonTester;
import org.springframework.boot.test.json.JsonContent;
import ru.practicum.shareit.booking.model.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import static org.assertj.core.api.Assertions.assertThat;

@JsonTest
class BookingDtoTest {
    @Autowired
    private JacksonTester<BookingDto> jacksonTester;

    @SneakyThrows
    @Test
    void testBookingDto() {
        LocalDateTime start = LocalDateTime.now();
        LocalDateTime end = LocalDateTime.now().plusDays(1);
        ItemDto itemDto = ItemDto.builder().id(1L).name("1").description("1").available(true).requestId(1L).build();
        UserDto userDto = UserDto.builder().id(1L).email("test@test.test").name("test").build();
        BookingDto bookingDto = BookingDto.builder().id(1L).start(start).end(end).item(itemDto).booker(userDto)
                .status(BookingStatus.WAITING).build();

        JsonContent<BookingDto> jsonContent = jacksonTester.write(bookingDto);

        assertThat(jsonContent).extractingJsonPathNumberValue("$.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.start")
                .isEqualTo(start.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(jsonContent).extractingJsonPathStringValue("$.end")
                .isEqualTo(end.format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss")));
        assertThat(jsonContent).extractingJsonPathNumberValue("$.item.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathNumberValue("$.booker.id").isEqualTo(1);
        assertThat(jsonContent).extractingJsonPathStringValue("$.status")
                .isEqualTo(BookingStatus.WAITING.toString());
    }
}