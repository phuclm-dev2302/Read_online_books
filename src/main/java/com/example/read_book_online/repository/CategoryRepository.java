package com.example.read_book_online.repository;

import com.example.read_book_online.entity.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByCategoryNameIn(List<String> categoryNames);
}
