package com.example.read_book_online.service;

import com.example.read_book_online.dto.response.CategoryResponse;
import com.example.read_book_online.dto.response.ResponseData;

import java.util.List;

public interface CategoryService {
    List<Long> getCategoryIdsAsList(String categoryId);
    ResponseData<List<CategoryResponse>> getCategories();
}
