package com.example.read_book_online.controller;

import com.example.read_book_online.service.MomoPaymentService;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("api/v1")
public class MomoCallbackController {

    @Autowired
    private MomoPaymentService momoPaymentService;

    @PostMapping("/ipn")
    public ResponseEntity<Map<String, Object>> momoIPN(@RequestBody String body) {
        JSONObject json = new JSONObject(body);
        System.out.println("MoMo IPN Response: " + json.toString());

        String receivedSignature = json.optString("signature");
        String partnerCode = json.optString("partnerCode");
        String orderId = json.optString("orderId");
        String amount = json.optString("amount");
        int resultCode = json.optInt("resultCode", -1);
        String message = json.optString("message");
        String transId = json.optString("transId");

        if (resultCode == 0) {
            System.out.println("Transaction successful. Order ID: " + orderId);
        } else {
            System.out.println("Transaction failed. Order ID: " + orderId + " with resultCode: " + resultCode + ", message: " + message);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("message", "IPN received successfully");

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/redirect")
    public ResponseEntity<String> momoRedirect(@RequestParam Map<String, String> params) {
        System.out.println("MoMo Redirect Params: " + params.toString());

        String resultCodeStr = params.getOrDefault("resultCode", "-1");
        int resultCode;
        try {
            resultCode = Integer.parseInt(resultCodeStr);
        } catch (NumberFormatException e) {
            resultCode = -1;
        }
        String orderId = params.getOrDefault("orderId", "unknown");

        if (resultCode == 0) {
            return ResponseEntity.ok("Thanh toán thành công! Chi tiết: " + params.toString());
        } else {
            String message = params.getOrDefault("message", "Unknown error");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body("Thanh toán thất bại! Order ID: " + orderId + ", message: " + message + ", chi tiết: " + params.toString());
        }
    }
}