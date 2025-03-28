package com.example.read_book_online.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class BookRequest {
    private String title;
    private MultipartFile image;
    private Long authorId;
    private String categoryIds;
    private MultipartFile pdfFile;
    private Boolean isVip;
}
