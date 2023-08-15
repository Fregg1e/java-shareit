package ru.practicum.shareit.booking.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.booking.dto.BookingStatus;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.user.dto.UserDto;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Future;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Data
@Builder
public class BookingDto {
    private Long id;
    @NotNull(message = "Start равен null!")
    @Future(message = "Start раньше текущего времени!")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime start;
    @NotNull(message = "End равен null!")
    @Future(message = "End раньше текущего времени!")
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
    private LocalDateTime end;
    private Long itemId;
    private ItemDto item;
    private Long bookerId;
    private UserDto booker;
    private BookingStatus status;

    @AssertTrue(message = "End должно быть после Start!")
    private boolean isEndAfterStart() {
        return start == null || end == null || end.isAfter(start);
    }
}
