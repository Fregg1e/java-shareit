package ru.practicum.shareit.user.model;

import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table(name = "users")
public class User {
    @Id
    @Column(name = "user_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Exclude
    private Long id;
    @Column(name = "user_name", nullable = false, length = 50)
    @EqualsAndHashCode.Exclude
    private String name;
    @Column(name = "email", nullable = false, length = 50, unique = true)
    private String email;
}
