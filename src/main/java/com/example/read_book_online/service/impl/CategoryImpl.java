package com.example.read_book_online.service.impl;

import com.example.read_book_online.config.exception.CategoryNotFoundException;
import com.example.read_book_online.dto.response.CategoryResponse;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.entity.Category;
import com.example.read_book_online.repository.CategoryRepository;
import com.example.read_book_online.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Service
public class CategoryImpl implements CategoryService {
    private final CategoryRepository categoryRepository;

    public CategoryImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }

    @Override

    public List<Long> getCategoryIdsAsList(String categoryIds) {
        if (categoryIds == null || categoryIds.isEmpty()) {
            System.out.println("⚠️ categoryIds is null or empty!");
            return new ArrayList<>();
        }

        List<Long> parsedIds = Arrays.stream(categoryIds.split(","))
                .map(Long::parseLong)
                .toList();

        System.out.println("✅ Parsed Category IDs: " + parsedIds);
        return parsedIds;
    }

    @Override
    public ResponseData<List<CategoryResponse>> getCategories() {
        List<Category> categories = categoryRepository.findAll();

        if (categories.isEmpty()) {
            throw new CategoryNotFoundException("Không tìm thấy danh mục nào!");
        }

        List<CategoryResponse> categoryResponses = categories.stream()
                .map(CategoryResponse::fromCategory)
                .toList();

        return new ResponseData<>(200, "get categories successful", categoryResponses);
    }

}
