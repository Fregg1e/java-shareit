package ru.practicum.shareit.comment.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import ru.practicum.shareit.comment.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CommentRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private CommentRepository repository;
    private List<User> users;
    private List<Item> items;
    private List<Comment> comments;

    @BeforeEach
    void beforeEach() {
        users = List.of(
                User.builder().name("test1").email("test1@email.test").build(),
                User.builder().name("test2").email("test2@email.test").build(),
                User.builder().name("test3").email("test3@email.test").build()
        );
        for (User user : users) {
            em.persist(user);
        }
        items = List.of(
                Item.builder().name("1").description("1").available(true).owner(users.get(0)).build(),
                Item.builder().name("1").description("2").available(true).owner(users.get(1)).build()
        );
        for (Item item : items) {
            em.persist(item);
        }
        comments = List.of(
                Comment.builder().text("1").item(items.get(0)).author(users.get(1))
                        .created(LocalDateTime.now().minusDays(1)).build(),
                Comment.builder().text("1").item(items.get(0)).author(users.get(2))
                        .created(LocalDateTime.now().minusDays(2)).build(),
                Comment.builder().text("1").item(items.get(1)).author(users.get(0))
                        .created(LocalDateTime.now().minusDays(1)).build()
        );
        for (Comment comment : comments) {
            em.persist(comment);
        }
    }

    @Test
    void findByItemIdTest() {
        List<Comment> commentsByItemId = repository.findByItemId(items.get(0).getId());

        assertEquals(2, commentsByItemId.size());
        assertEquals(comments.get(0), commentsByItemId.get(0));
        assertEquals(comments.get(1), commentsByItemId.get(1));
    }
}