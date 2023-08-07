package ru.practicum.shareit.item.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ItemRepositoryTest {
    @Autowired
    private TestEntityManager em;
    @Autowired
    private ItemRepository repository;
    private List<User> users;
    private List<Item> items;
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
        items = List.of(
                Item.builder().name("1").description("1").available(true).owner(users.get(0)).build(),
                Item.builder().name("1").description("2").available(true).owner(users.get(1))
                        .request(requests.get(0)).build(),
                Item.builder().name("2").description("1").available(true).owner(users.get(1)).build()
        );
        for (Item item : items) {
            em.persist(item);
        }
    }

    @Test
    void findByOwnerIdTest() {
        List<Item> itemByOwner = repository.findByOwnerId(users.get(1).getId(),
                PageRequest.of(0, 3)).getContent();

        assertEquals(2, itemByOwner.size());
        assertEquals(items.get(1), itemByOwner.get(0));
        assertEquals(items.get(2), itemByOwner.get(1));
    }

    @Test
    void searchTest() {
        List<Item> itemBySearch = repository.search("2",
                PageRequest.of(0, 3)).getContent();

        assertEquals(2, itemBySearch.size());
        assertEquals(items.get(1), itemBySearch.get(0));
        assertEquals(items.get(2), itemBySearch.get(1));
    }

    @Test
    void findByRequestIdTest() {
        List<Item> itemByRequest = repository.findByRequestId(requests.get(0).getId());

        assertEquals(1, itemByRequest.size());
        assertEquals(items.get(1), itemByRequest.get(0));
    }
}