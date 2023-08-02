package ru.practicum.shareit.item.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {
    Page<Item> findByOwnerId(Long ownerId, Pageable pageable);

    @Query("select it "
            + "from Item as it "
            + "where (lower(it.name) like concat('%', lower(?1), '%') "
            + "or lower(it.description) like concat('%', lower(?1), '%')) and it.available = true")
    Page<Item> search(String text, Pageable pageable);

    List<Item> findByRequestId(Long requestId);
}
