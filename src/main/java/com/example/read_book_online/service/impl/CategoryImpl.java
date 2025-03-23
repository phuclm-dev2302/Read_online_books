package com.example.read_book_online.service.impl;

import com.example.read_book_online.service.CategoryService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
@Service
public class CategoryImpl implements CategoryService {
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
}
