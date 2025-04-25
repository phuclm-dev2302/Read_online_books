package com.example.read_book_online.controller;

import com.example.read_book_online.dto.request.ForgotRequest;
import com.example.read_book_online.dto.request.OtpRequest;
import com.example.read_book_online.dto.request.SignInRequest;
import com.example.read_book_online.dto.request.SignUpRequest;
import com.example.read_book_online.dto.response.AuthResponse;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("api/v1/auth")
@Tag(name = "Authentication Service", description = "APIs for user authentication and account management")
public class AuthController {

    @Autowired
    private AuthService authService;

    @Operation(summary = "User login", description = "Authenticate user and return JWT token")
    @PostMapping("/login")
    public ResponseEntity<ResponseData<AuthResponse>> signIn(@RequestBody SignInRequest signInForm) {
        ResponseData<AuthResponse> response = authService.login(signInForm);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @Operation(summary = "User registration", description = "Register a new user account")
    @PostMapping("/register")
    public ResponseEntity<ResponseData<String>> register(@Valid @RequestBody SignUpRequest form) {
        ResponseData<String> response = authService.register(form);
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/forgot")
    public ResponseEntity<ResponseData<String>> forgot(@RequestBody ForgotRequest forgotRequest) {
        ResponseData<String> response = authService.forgotPassword(forgotRequest.getEmail());
        return ResponseEntity.status(response.getStatus()).body(response);
    }

    @PostMapping("/confirm-forgot/{id}")
    public ResponseEntity<ResponseData<String>> confirmForgot(@PathVariable("id") Long id, @RequestBody OtpRequest otpRequest) {
        log.info("Confirm user, userId={}, otpCode={}", id, otpRequest.getOtpCode());
        return ResponseEntity.status(HttpStatus.OK).body(authService.confirmOtpRestPassword(id, otpRequest.getOtpCode()));
    }

    @Operation(summary = "Confirm user account", description = "Verify OTP and activate the user account")
    @PostMapping("/confirm/{userId}")
    public ResponseEntity<ResponseData<String>> confirm(@PathVariable Long userId, @RequestBody OtpRequest form) {
        log.info("Confirm user, userId={}, otpCode={}", userId, form.getOtpCode());

        ResponseData<String> response;
        try {
            response = authService.confirmUser(userId, form.getOtpCode());
        } catch (Exception e) {
            log.error("errorMessage={}", e.getMessage(), e.getCause());
            response = new ResponseData<>(400, "Confirm failed");
        }
        return ResponseEntity.status(response.getStatus()).body(response);
    }
    @Operation(summary = "logout the account", description = "logout the account and disable JWT")
    @PostMapping("/logout")
    public ResponseEntity<ResponseData<String>> logout(HttpServletRequest request, HttpServletResponse response) {
        return ResponseEntity.ok(authService.logout(request, response));
    }
    @Operation(summary = "refresh token", description = "refresh token for user")
    @GetMapping("/refresh")
    public ResponseEntity refreshToken(@RequestHeader("X-Refresh-Token") String refreshToken){
        return ResponseEntity.ok(authService.refreshToken(refreshToken));
    }
}