package com.example.read_book_online.dto.request;
import lombok.Data;

import java.util.List;
@Data
public class MulticastNotificationRequest {
    private List<String> tokens;
    private String title;
    private String body;
}
