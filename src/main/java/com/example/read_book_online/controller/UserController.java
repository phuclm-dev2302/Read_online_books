package com.example.read_book_online.controller;


import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.dto.response.UserResponse;
import com.example.read_book_online.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    @GetMapping("")
    public ResponseEntity< ResponseData<Page<UserResponse>>> getUser(@RequestParam(defaultValue = "0" )int page, @RequestParam(defaultValue = "10") int size) {
        ResponseData<Page<UserResponse>> result = userService.getUsers(page,size);
        return ResponseEntity.status(result.getStatus()).body(result);
    }

}
