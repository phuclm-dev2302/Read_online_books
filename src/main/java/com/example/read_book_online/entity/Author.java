package com.example.read_book_online.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Builder
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Data
@Table(name = "authors")
public class Author {
    @Id
    @GeneratedValue( strategy = GenerationType.IDENTITY)
    private Long authorId;

    private String authorName;

    @OneToMany( mappedBy = "author", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Book> book;
}
