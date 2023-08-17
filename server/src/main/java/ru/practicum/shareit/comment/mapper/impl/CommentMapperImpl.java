package ru.practicum.shareit.comment.mapper.impl;

import org.springframework.stereotype.Component;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

@Component
public class CommentMapperImpl implements CommentMapper {
    @Override
    public Comment toComment(CommentDto commentDto, Item item, User author, LocalDateTime created) {
        return Comment.builder()
                .id(commentDto.getId())
                .text(commentDto.getText())
                .item(item)
                .author(author)
                .created(created)
                .build();
    }

    @Override
    public CommentDto toCommentDto(Comment comment) {
        return CommentDto.builder()
                .id(comment.getId())
                .text(comment.getText())
                .authorName(comment.getAuthor().getName())
                .created(comment.getCreated())
                .build();
    }
}
