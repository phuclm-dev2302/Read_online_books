package com.example.read_book_online.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "fcm_token")
public class FcmToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "userId", nullable = false) // Sửa lại "id" thành "userId"
    private User user;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String token;

    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "last_active")
    private LocalDateTime lastActive;
}
