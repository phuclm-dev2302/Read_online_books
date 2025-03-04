package com.example.read_book_online.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class OtpRequest {

    @NotBlank(message = "otp code must be not blank !")
    private String otpCode;
}
