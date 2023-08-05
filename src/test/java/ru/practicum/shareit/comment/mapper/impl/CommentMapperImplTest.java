package ru.practicum.shareit.comment.mapper.impl;

import org.junit.jupiter.api.Test;
import ru.practicum.shareit.comment.dto.CommentDto;
import ru.practicum.shareit.comment.mapper.CommentMapper;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class CommentMapperImplTest {
    private final CommentMapper commentMapper = new CommentMapperImpl();

    @Test
    void toCommentTest() {
        CommentDto commentDto = CommentDto.builder().id(1L).text("test text").build();
        Item item = Item.builder().id(1L).build();
        User author = User.builder().id(1L).build();
        LocalDateTime created = LocalDateTime.now();

        Comment comment = commentMapper.toComment(commentDto, item, author, created);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getItem().getId(), item.getId());
        assertEquals(comment.getAuthor().getId(), author.getId());
        assertEquals(comment.getCreated(), created);
    }

    @Test
    void toCommentDtoTest() {
        User author = User.builder().id(1L).name("test").build();
        LocalDateTime created = LocalDateTime.now();
        Comment comment = Comment.builder().id(1L).text("test text").author(author).created(created).build();

        CommentDto commentDto = commentMapper.toCommentDto(comment);

        assertEquals(comment.getId(), commentDto.getId());
        assertEquals(comment.getText(), commentDto.getText());
        assertEquals(comment.getAuthor().getName(), commentDto.getAuthorName());
        assertEquals(comment.getCreated(), commentDto.getCreated());
    }
}