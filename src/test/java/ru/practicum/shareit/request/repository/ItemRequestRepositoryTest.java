package ru.practicum.shareit.request.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRequestRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRequestRepository repository;
    private List<User> users;
    private List<ItemRequest> requests;

    @BeforeEach
    void beforeEach() {
        users = List.of(
                User.builder().name("test1").email("test1@email.test").build(),
                User.builder().name("test2").email("test2@email.test").build()
        );
        for (User user : users) {
            em.persist(user);
        }
        requests = List.of(
                ItemRequest.builder().description("1").requestor(users.get(0))
                        .created(LocalDateTime.now()).build(),
                ItemRequest.builder().description("2").requestor(users.get(1))
                        .created(LocalDateTime.now().minusDays(3)).build(),
                ItemRequest.builder().description("3").requestor(users.get(1))
                        .created(LocalDateTime.now().minusDays(1)).build()
        );
        for (ItemRequest itemRequest : requests) {
            em.persist(itemRequest);
        }
    }

    @Test
    void findByRequestorIdOrderByCreatedDesc() {
        List<ItemRequest> itemRequests = repository.findByRequestorIdOrderByCreatedDesc(users.get(1).getId());

        assertEquals(2, itemRequests.size());
        assertEquals(requests.get(2), itemRequests.get(0));
        assertEquals(requests.get(1), itemRequests.get(1));
    }

    @Test
    void findByRequestorIdNotOrderByCreatedDesc() {
        List<ItemRequest> itemRequests = repository.findByRequestorIdNotOrderByCreatedDesc(users.get(0).getId(),
                PageRequest.of(0, 3)).getContent();

        assertEquals(2, itemRequests.size());
        assertEquals(requests.get(2), itemRequests.get(0));
        assertEquals(requests.get(1), itemRequests.get(1));
    }
}