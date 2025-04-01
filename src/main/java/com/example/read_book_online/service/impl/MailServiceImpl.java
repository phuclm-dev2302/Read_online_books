package com.example.read_book_online.service.impl;

import com.example.read_book_online.service.MaiiService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;


@Slf4j
@Service
@RequiredArgsConstructor
public class MailServiceImpl implements MaiiService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.from}")
    private String emailFrom;

    @Value("${endpoint.confirmUser}")
    private String apiConfirmUser;

    @Override
    @KafkaListener(topics = "confirm-account-topic", groupId = "confirm-account-group")
    public void sendConfirmLinkByKafka(String message) throws MessagingException, UnsupportedEncodingException {
        log.info("Processing Kafka message for account confirmation: {}", message);

        String[] arr = message.split(",");
        String emailTo = arr[0].substring(arr[0].indexOf('=') + 1);
        String userId = arr[1].substring(arr[1].indexOf('=') + 1);
        String otpCode = arr[2].substring(arr[2].indexOf('=') + 1);

        // Construct the confirmation link
        String linkConfirm = String.format("%s/%s?otpCode=%s", apiConfirmUser, userId, otpCode);
        log.info("Generated confirmation link: {}", linkConfirm);

        // Set up email content and properties
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        Map<String, Object> properties = new HashMap<>();
        properties.put("linkConfirm", linkConfirm);
        properties.put("otpCode", otpCode);
        context.setVariables(properties);

        helper.setFrom(emailFrom, "Lê Minh Phúc CN22G");
        helper.setTo(emailTo);
        helper.setSubject("Your OTP Code for Account Confirmation");
        String html = templateEngine.process("confirm-email.html", context);
        helper.setText(html, true);

        // Send email and log the result
        mailSender.send(mimeMessage);
        log.info("Confirmation email sent to user at email={}, with OTP code={}", emailTo, otpCode);
    }

    @Override
    @KafkaListener(topics = "forgot-account-topic", groupId = "Confirm-OTP-resPassword")
    public void sendConfirmResPassByKafka(String message) throws MessagingException, UnsupportedEncodingException {
        log.info("Processing Kafka message for account confirmation: {}", message);

        String[] arr = message.split(",");
        String emailTo = arr[0].substring(arr[0].indexOf('=') + 1);
        String userId = arr[1].substring(arr[1].indexOf('=') + 1);
        String otpCode = arr[2].substring(arr[2].indexOf('=') + 1);


        // Set up email content and properties
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, StandardCharsets.UTF_8.name());
        Context context = new Context();
        Map<String, Object> properties = new HashMap<>();
        properties.put("otpCode", otpCode);
        context.setVariables(properties);

        helper.setFrom(emailFrom, "Lê Minh Phúc CN22G");
        helper.setTo(emailTo);
        helper.setSubject("Your OTP Code is: ");
        String html = templateEngine.process("forgot-email.html", context);
        helper.setText(html, true);

        // Send email and log the result
        mailSender.send(mimeMessage);
        log.info("Confirmation email sent to user at email={}, with OTP code={}", emailTo, otpCode);
    }
}




























