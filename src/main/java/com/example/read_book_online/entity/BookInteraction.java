package com.example.read_book_online.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "book_interactions", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"user_id", "book_id"}) // 1 user chỉ like 1 lần
})
public class BookInteraction {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    private boolean liked; // True nếu đã like
    private int views;     // Số lượt xem của user với sách này
}
