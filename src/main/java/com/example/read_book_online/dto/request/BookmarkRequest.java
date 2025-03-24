package com.example.read_book_online.dto.request;

import lombok.Data;

@Data
public class BookmarkRequest {
    private Long bookId;
    private Long page;
}
