package com.example.read_book_online.repository;

import com.example.read_book_online.entity.Author;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
@Repository
public interface AuthorRepository extends JpaRepository<Author, Long> {
    List<Author> findByAuthorNameIn(List<String> authorNames);
}
