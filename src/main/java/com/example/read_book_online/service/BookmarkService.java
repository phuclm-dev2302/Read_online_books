package com.example.read_book_online.service;

import com.example.read_book_online.dto.request.BookmarkRequest;
import com.example.read_book_online.dto.response.BookmarkResponse;
import com.example.read_book_online.dto.response.ResponseData;

import java.util.List;

public interface BookmarkService {
    ResponseData<BookmarkResponse> addBookmark(BookmarkRequest bookmarkRequest);
    ResponseData<String> deleteBookmark(Long bookmarkId);
    ResponseData<List<BookmarkResponse>> getMyBookmarks();
}
