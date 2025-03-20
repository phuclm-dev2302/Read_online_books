package com.example.read_book_online.config;

import com.example.read_book_online.dto.response.AuthResponse;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.entity.Role;
import com.example.read_book_online.entity.User;
import com.example.read_book_online.enums.StatusEnum;
import com.example.read_book_online.jwtconfig.JwtProvider;
import com.example.read_book_online.repository.RoleRepository;
import com.example.read_book_online.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final JwtProvider jwtProvider;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken authenticationToken) {
            OAuth2User oauth2User = authenticationToken.getPrincipal();

            // Lấy thông tin từ Google (hoặc provider khác)
            Map<String, Object> attributes = oauth2User.getAttributes();
            String email = (String) attributes.get("email");
            String name = (String) attributes.get("name");

            // Kiểm tra user có tồn tại trong database không
            User user = userRepository.findByEmail(email).orElse(null);
            if (user == null) {
                log.info("Tạo tài khoản mới cho người dùng OAuth2: {}", email);

                // Lấy role mặc định
                Role role = roleRepository.findByName("ROLE_USER")
                        .orElseThrow(() -> new RuntimeException("Role not found"));

                user = User.builder()
                        .email(email)
                        .username(name)
                        .password(passwordEncoder.encode(UUID.randomUUID().toString().substring(0, 8))) // Mật khẩu random
                        .role(role)
                        .status(StatusEnum.ACTIVE)
                        .build();
                userRepository.save(user);
            } else {
                log.info("Người dùng {} đã tồn tại, không cần tạo mới.", email);
            }

            // Tạo Authentication cho user
            Authentication userAuth = new UsernamePasswordAuthenticationToken(
                    user.getEmail(),
                    null,
                    Collections.singletonList(new SimpleGrantedAuthority(user.getRole().getName()))
            );

            // Tạo JWT token
            String token = jwtProvider.generateToken(userAuth);

            // Tạo response trả về client
            AuthResponse authResponse = AuthResponse.from(user, token);
            ResponseData<AuthResponse> responseData = new ResponseData<>(200, "Đăng nhập thành công!", authResponse);

            // Gửi response dưới dạng JSON
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseData));
        }
    }
}
