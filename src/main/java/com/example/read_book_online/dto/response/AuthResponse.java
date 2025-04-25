package com.example.read_book_online.dto.response;


import com.example.read_book_online.entity.User;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AuthResponse {
    private Long id;
    private String userName;
    private String token;
    private String refreshToken;

    public static AuthResponse from(User user, String token,String refreshToken) {
        return AuthResponse.builder()
                .id(user.getUserId())
                .userName(user.getUsername())
                .token(token)
                .refreshToken(refreshToken)
                .build();
    }
}
