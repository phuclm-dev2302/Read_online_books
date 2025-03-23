package com.example.read_book_online.dto.request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Data
public class BookRequest {
    private String title;
    private Long authorId;
    private String categoryIds;
    private MultipartFile pdfFile;
    private Boolean isVip;
}
