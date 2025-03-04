package com.example.read_book_online.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

import lombok.Data;

import java.time.LocalDate;


@Data
public class SignUpRequest {
    @NotBlank(message = "Username must not be blank")
    private String username;
    @JsonFormat(pattern = "dd-MM-yyyy")
    private LocalDate dob;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Email must not be blank")
    private String password;

    @Pattern(regexp = "^[0-9]{10,11}$", message = "Phone number must be 10 or 11 digits")
    private String phoneNumber;

    @NotBlank(message = "Email must not be blank")
    private String confirmPassword;

}