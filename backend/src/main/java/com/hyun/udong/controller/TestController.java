package com.hyun.udong.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestController {

    @GetMapping
    public Map<String, Object> testEndpoint() {
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Hello from backend!");
        response.put("timestamp", System.currentTimeMillis());
        return response;
    }
}