package ru.practicum.shareit.comment.mapper;

import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

public interface CommentMapper {
    Comment toComment(CommentDto commentDto, Item item, User author, LocalDateTime created);
    CommentDto toCommentDto(Comment comment);
}
