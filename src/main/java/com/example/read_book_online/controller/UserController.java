package com.example.read_book_online.controller;


import com.example.read_book_online.dto.request.ChangePasswordRequest;
import com.example.read_book_online.dto.request.UserRequest;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.dto.response.UserResponse;
import com.example.read_book_online.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@NoArgsConstructor
@AllArgsConstructor
@RestController
@RequestMapping("api/v1/user")
public class UserController {
    @Autowired
    private UserService userService;

    @Operation(summary = "Nguoi dung xem profile ban than", description = "API cho phep user xem profile cua minh")
    @GetMapping("/me")
    public ResponseEntity<ResponseData<UserResponse>> getMe(){
        ResponseData<UserResponse> result = userService.getMe();
        return ResponseEntity.status(result.getStatus()).body(result);
    }
    @Operation(summary = "update user info", description = "API cho nguoi dung cap nhat thong tin")
    @PutMapping
    public ResponseEntity<ResponseData<UserResponse>> updateInfo(@RequestBody @Valid UserRequest userRequest){
        return ResponseEntity.ok(userService.updateInfo(userRequest));
    }

    @GetMapping("")
    public ResponseEntity<ResponseData<Page<UserResponse>>> getUser(@RequestParam(defaultValue = "0" )int page, @RequestParam(defaultValue = "10") int size) {
        ResponseData<Page<UserResponse>> result = userService.getUsers(page,size);
        return ResponseEntity.status(result.getStatus()).body(result);
    }
    @Operation(summary = "change the password", description = "API de nguoi dung doi mat khau")
    @PutMapping("/change-password")
    public ResponseEntity<ResponseData<String>> changePassword(@RequestBody @Valid ChangePasswordRequest changePasswordRequest){{
        return ResponseEntity.ok(userService.changePassword(changePasswordRequest));}
    }
}
