package com.example.read_book_online.service.impl;

import com.example.read_book_online.config.exception.MomoException;
import com.example.read_book_online.service.MomoPaymentService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

@Service
public class MomoPaymentServiceImpl implements MomoPaymentService {

    @Value("${momo.partnerCode}")
    private String PARTNER_CODE;
    @Value("${momo.accessKey}")
    private String ACCESS_KEY;
    @Value("${momo.secretKey}")
    private String SECRET_KEY;
    @Value("${momo.ipnUrl}")
    private String IPN_URL;
    @Value("${momo.redirectUrl}")
    private String REDIRECT_URL;
    @Value("${momo.endpoint}")
    private String END_POINT_URL;

    private static final String REQUEST_TYPE = "captureWallet";
    private static final String LANG = "en";
    private final WebClient webClient;

    public MomoPaymentServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.build();
    }

    @Override
    public String createPaymentRequest(String amount) {
        String requestId = UUID.randomUUID().toString();
        String orderId = UUID.randomUUID().toString();

        try {
            Map<String, Object> requestBody = buildPaymentRequestBody(amount, requestId, orderId);
            String signature = generateSignature(requestBody);
            requestBody.put("signature", signature);

            return webClient.post()
                    .uri(END_POINT_URL)
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(response -> System.out.println("MoMo Response: " + response))
                    .blockOptional()
                    .orElseThrow(() -> new MomoException("Empty response from MoMo"));
        } catch (Exception e) {
            throw new MomoException("Payment creation failed: " + e.getMessage());
        }
    }

    @Override
    public String checkPaymentStatus(String orderId) {
        String requestId = UUID.randomUUID().toString();

        try {
            Map<String, Object> requestBody = buildStatusCheckBody(orderId, requestId);
            String signature = generateStatusSignature(requestBody);
            requestBody.put("signature", signature);

            return webClient.post()
                    .uri("https://test-payment.momo.vn/v2/gateway/api/query")
                    .contentType(MediaType.APPLICATION_JSON)
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnNext(response -> System.out.println("MoMo Status Response: " + response))
                    .blockOptional()
                    .orElseThrow(() -> new MomoException("Empty status response from MoMo"));
        } catch (Exception e) {
            throw new MomoException("Status check failed: " + e.getMessage());
        }
    }

    private Map<String, Object> buildPaymentRequestBody(String amount, String requestId, String orderId) {
        Map<String, Object> body = new HashMap<>();
        body.put("partnerCode", PARTNER_CODE);
        body.put("accessKey", ACCESS_KEY);
        body.put("requestId", requestId);
        body.put("amount", amount);
        body.put("orderId", orderId);
        body.put("orderInfo", "Libro");
        body.put("redirectUrl", REDIRECT_URL);
        body.put("ipnUrl", IPN_URL);
        body.put("extraData", "");
        body.put("requestType", REQUEST_TYPE);
        body.put("lang", LANG);
        return body;
    }

    private Map<String, Object> buildStatusCheckBody(String orderId, String requestId) {
        Map<String, Object> body = new HashMap<>();
        body.put("partnerCode", PARTNER_CODE);
        body.put("accessKey", ACCESS_KEY);
        body.put("requestId", requestId);
        body.put("orderId", orderId);
        body.put("lang", LANG);
        return body;
    }

    private String generateSignature(Map<String, Object> data) {
        String raw = String.format("accessKey=%s&amount=%s&extraData=%s&ipnUrl=%s&orderId=%s&orderInfo=%s&partnerCode=%s&redirectUrl=%s&requestId=%s&requestType=%s",
                ACCESS_KEY, data.get("amount"), data.get("extraData"), IPN_URL,
                data.get("orderId"), data.get("orderInfo"), PARTNER_CODE, REDIRECT_URL,
                data.get("requestId"), REQUEST_TYPE);
        return signHmacSHA256(raw);
    }

    private String generateStatusSignature(Map<String, Object> data) {
        String raw = String.format("accessKey=%s&orderId=%s&partnerCode=%s&requestId=%s",
                ACCESS_KEY, data.get("orderId"), PARTNER_CODE, data.get("requestId"));
        return signHmacSHA256(raw);
    }

    private String signHmacSHA256(String data) {
        try {
            Mac hmacSHA256 = Mac.getInstance("HmacSHA256");
            SecretKeySpec secretKey = new SecretKeySpec(SECRET_KEY.getBytes(StandardCharsets.UTF_8), "HmacSHA256");
            hmacSHA256.init(secretKey);
            byte[] hash = hmacSHA256.doFinal(data.getBytes(StandardCharsets.UTF_8));
            return bytesToHex(hash);
        } catch (Exception e) {
            throw new MomoException("Signature generation failed: " + e.getMessage());
        }
    }

    private static String bytesToHex(byte[] bytes) {
        StringBuilder hexString = new StringBuilder();
        for (byte b : bytes) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) hexString.append('0');
            hexString.append(hex);
        }
        return hexString.toString();
    }
}