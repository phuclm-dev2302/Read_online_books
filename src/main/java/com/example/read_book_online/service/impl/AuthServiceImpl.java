package com.example.read_book_online.service.impl;


import com.example.read_book_online.dto.request.SignInRequest;
import com.example.read_book_online.dto.request.SignUpRequest;
import com.example.read_book_online.dto.response.AuthResponse;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.dto.response.ResponseError;
import com.example.read_book_online.entity.Role;
import com.example.read_book_online.entity.User;
import com.example.read_book_online.enums.StatusEnum;
import com.example.read_book_online.jwtconfig.JwtProvider;
import com.example.read_book_online.repository.RoleRepository;
import com.example.read_book_online.repository.UserRepository;
import com.example.read_book_online.service.AuthService;
import com.example.read_book_online.service.BlacklistTokenService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Random;

@Slf4j
@Service
public class AuthServiceImpl implements AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private JwtProvider jwtProvider;
    @Autowired
    private BlacklistTokenService  blacklistTokenService;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    @Override
    public ResponseData<AuthResponse> login(SignInRequest form) {
        User user = userRepository.findByEmail(form.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + form.getEmail()));

        if (!user.getStatus().equals(StatusEnum.ACTIVE)) {
            throw new IllegalArgumentException("Account is not active");
        }

        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(form.getEmail(), form.getPassword())
            );
        } catch (AuthenticationException e) {
            log.error("Authentication failed for email: {} with exception: {}", form.getEmail(), e.getMessage());
            throw new IllegalArgumentException("Invalid email or password");
        }
        String accessToken = jwtProvider.generateToken(authentication);
        log.info("User {} logged in successfully with ", user.getEmail());

        return new ResponseData<>(200,"login success", AuthResponse.from(user,accessToken));
    }


    @Override
    public ResponseData<String> register(SignUpRequest form) {
        if (userRepository.existsByEmail(form.getEmail())) {
            return new ResponseError<>(400, "Email address already in use");
        }
        Role role = roleRepository.findByName("ROLE_USER").orElse(null);
        if (role == null) {
            log.error("Role not found");
            return new ResponseError<>(400, "Role not found");
        }
        String otpCode = String.format("%06d", new Random().nextInt(99999));
        User user = User.builder()
                .username(form.getUsername())
                .email(form.getEmail())
                .password(passwordEncoder.encode(form.getPassword()))
                .role(role)
                .otp(otpCode)
                .dob(form.getDob())
                .phoneNumber(form.getPhoneNumber())
                .status(StatusEnum.NONACTIVE)
                .build();
        userRepository.save(user);

        kafkaTemplate.send("confirm-account-topic", String.format("email=%s,id=%s,otpCode=%s", user.getEmail(), user.getUserId(), otpCode));
        log.info("User {} registered successfully with ID {}, pls check email to confirm OTP. Thanks!", user.getEmail(), user.getUserId());
        return new ResponseData<>(200, "Success register new user. Please check your email for confirmation", "Id: " + user.getUserId());
    }


    @Override
    public ResponseData<String> confirmUser(long userId, String otpCode) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Check if the OTP matches
        if (!otpCode.equals(user.getOtp())) {
            log.error("OTP does not match for userId={}", userId);
            throw new IllegalArgumentException("OTP is incorrect");
        }

        user.setStatus(StatusEnum.ACTIVE);
        user.setSetCreatedDate(LocalDate.now());
        userRepository.save(user);
        return new ResponseData<>(200, "confirm successfully");
    }

    @Override
    public ResponseData<String> logout(HttpServletRequest request, HttpServletResponse response) {
        String token = jwtProvider.getJwtFromRequest(request);

        // Kiểm tra
        if (token != null && jwtProvider.validateAccessToken(token)) {
            Date expiryDateFromToken = jwtProvider.getExpiryDateFromToken(token);

            // Chuyển đổi từ java.util.Date sang java.time.LocalDateTime
            LocalDateTime expiryDate = expiryDateFromToken.toInstant()
                    .atZone(ZoneId.systemDefault())
                    .toLocalDateTime();

            blacklistTokenService.addTokenToBlacklist(token, expiryDate);

            // Xóa cookie hoặc bất kỳ thông tin đăng nhập liên quan
            response.setHeader("Set-Cookie", "JSESSIONID=; HttpOnly; Path=/; Max-Age=0; Secure; SameSite=Strict");

            log.info("User logged out successfully with token: {}", token);
            return new ResponseData<>(200, "Đăng xuất thành công", null);
        }

        log.error("Logout failed: Invalid or expired token for request: {}", request.getRequestURI());
        return new ResponseError<>(400, "Token không hợp lệ hoặc đã hết hạn");
    }

}
