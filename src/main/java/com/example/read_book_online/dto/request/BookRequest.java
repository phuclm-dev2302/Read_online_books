package com.example.read_book_online.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class BookRequest {
    private String title;
    private Long authorId;
    private Long categoryId;
    private MultipartFile pdfFile;
    private Boolean isVip;
}
