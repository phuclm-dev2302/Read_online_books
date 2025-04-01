package com.example.read_book_online.config;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class RequestInterceptor implements HandlerInterceptor {

    private static final String START_TIME = "startTime";

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        try {
            LocalDateTime startTime = LocalDateTime.now();
            request.setAttribute(START_TIME, startTime);
            System.out.println("1 - preHandle() : Before sending request to the Controller");
            System.out.println("Method Type: " + request.getMethod());
            System.out.println("Request URL: " + request.getRequestURI());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        try {
            System.out.println("2 - postHandle() : After the Controller serves the request (before returning back response to the client)");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // This method will be called after the request and response are completed
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        try {
            LocalDateTime startTime = (LocalDateTime) request.getAttribute(START_TIME);
            if (startTime != null) {
                Duration duration = Duration.between(startTime, LocalDateTime.now());
                System.out.println("3 - afterCompletion() : Time taken for request processing: " + duration.toMillis() + " ms");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
