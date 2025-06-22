package com.kampai.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.util.List;

@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    // Eventとの関連（1ユーザーが複数イベントを作成可能）
    @OneToMany(mappedBy = "createdByUser", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Event> events;
}
