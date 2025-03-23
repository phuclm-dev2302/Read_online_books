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

    @ManyToMany
    @JoinTable(
            name = "book_category", // Tên bảng trung gian
            joinColumns = @JoinColumn(name = "book_id"), // Cột liên kết với Book
            inverseJoinColumns = @JoinColumn(name = "category_id") // Cột liên kết với Category
    )
    private List<Category> categories;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false)
    private Author author;

    private String title;

    private String pdfFilePath;

    private Boolean isVip;

    @OneToMany(mappedBy = "book", cascade = CascadeType.ALL)
    private List<BookInteraction> interactions; // Danh sách like & view của từng user
}
