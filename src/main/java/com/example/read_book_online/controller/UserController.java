package com.example.read_book_online.controller;


import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.dto.response.UserResponse;
import com.example.read_book_online.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@NoArgsConstructor
@AllArgsConstructor
@RestController
@RequestMapping("api/v1/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Operation(summary = "Nguoi dung xem profile ban than", description = "API cho phep user xem profile cua minh")
    @GetMapping("")
    public ResponseEntity<ResponseData<UserResponse>> getUser(){
        ResponseData<UserResponse> result = userService.getUser();
        return ResponseEntity.status(result.getStatus()).body(result);
    }

}
