package ru.practicum.shareit.request.model;

import lombok.Builder;
import lombok.Data;
import ru.practicum.shareit.user.model.User;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Builder
@Entity
@Table(name = "requests")
public class ItemRequest {
    @Id
    @Column(name = "request_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "description", length = 200, nullable = false)
    private String description;
    @ManyToOne(fetch = FetchType.LAZY)
    private User requestor;
    @Column(name = "created")
    private LocalDateTime created;
}
