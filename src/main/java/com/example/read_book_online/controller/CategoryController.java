package com.example.read_book_online.controller;

import com.example.read_book_online.dto.response.CategoryResponse;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.service.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@RestController
@RequestMapping("api/v1/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    @Operation(summary = "get categories", description = "get categories for all book")
    @GetMapping
    public ResponseEntity<ResponseData<List<CategoryResponse>>> getCategories() {
        return ResponseEntity.ok(categoryService.getCategories());
    }
}
