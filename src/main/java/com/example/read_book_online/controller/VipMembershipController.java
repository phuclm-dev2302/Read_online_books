package com.example.read_book_online.controller;

import com.example.read_book_online.dto.request.MomoRequest;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.dto.response.VipMembershipResponse;
import com.example.read_book_online.service.VipMembershipService;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequestMapping("api/v1/vip")
@RequiredArgsConstructor
public class VipMembershipController {
    private final VipMembershipService vipMembershipService;
    @Operation(summary = "dang ki vip", description = "API cho nguoi dung dang ki vip theo so thang")
    @PostMapping
    public ResponseEntity<ResponseData<String>> registerVip(@RequestParam int time, @RequestBody MomoRequest paymentRequest) {
        return ResponseEntity.ok(vipMembershipService.registerVip(time, paymentRequest.getAmount()));
    }
    @PutMapping@Operation(summary = "API de gia han vip", description = "API cho phep nguoi dung gia han vip theo so thang")
    public ResponseEntity<ResponseData<VipMembershipResponse>> renewalVip(@RequestParam int time) {
        return ResponseEntity.ok(vipMembershipService.renewalVip(time));
    }
    @Operation(summary = "API de nguoi dung xem tai khoan vip cua ho", description = "API de xem tai khoan Vip")
    @GetMapping
    public ResponseEntity<ResponseData<VipMembershipResponse>> getVip() {
        return ResponseEntity.ok(vipMembershipService.getVip());
    }

}
