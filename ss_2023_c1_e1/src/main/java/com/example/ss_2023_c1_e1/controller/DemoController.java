package com.example.ss_2023_c1_e1.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DemoController {
    
    @GetMapping("/hello")
    public String getHello() {
        return "Hello!";
    }
}
