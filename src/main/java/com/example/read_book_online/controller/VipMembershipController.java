package com.example.read_book_online.controller;

import com.example.read_book_online.dto.request.RenewalRequest;
import com.example.read_book_online.dto.request.VipMembershipRequest;
import com.example.read_book_online.dto.response.ResponseData;
import com.example.read_book_online.dto.response.VipMembershipResponse;
import com.example.read_book_online.service.VipMembershipService;
import jakarta.validation.Valid;
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

    @PostMapping
    public ResponseEntity<ResponseData<VipMembershipResponse>> registerVip(@RequestParam int time) {
        return ResponseEntity.ok(vipMembershipService.registerVip(time));
    }
    @PutMapping
    public ResponseEntity<ResponseData<VipMembershipResponse>> renewalVip(@RequestBody @Valid RenewalRequest renewalRequest) {
        return ResponseEntity.ok(vipMembershipService.renewalVip(renewalRequest));
    }
    @GetMapping
    public ResponseEntity<ResponseData<VipMembershipResponse>> getVip() {
        return ResponseEntity.ok(vipMembershipService.getVip());
    }

}
