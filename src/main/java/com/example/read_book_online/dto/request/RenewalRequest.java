package com.example.read_book_online.dto.request;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDate;

@Data
public class RenewalRequest {
    @NotNull(message = "End date is required")
    @JsonFormat(pattern = "dd-MM-yyyy")
    //Đảm bảo endDate phải là ngày trong tương lai.
    @Future(message = "End date must be in the future")
    private LocalDate endDate;
}
