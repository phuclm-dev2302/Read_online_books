package com.example.read_book_online.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bookId;

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    private String title;

    private String pdfFilePath;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BookInteraction> interactions; // Danh sách like & view của từng user
}
