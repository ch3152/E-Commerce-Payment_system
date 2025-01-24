package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {

    // 루트 URL ("/")로 요청이 들어오면 index.html을 반환
    @GetMapping("/")
    public String index() {
        return "index";  // templates 폴더 안의 index.html을 의미
    }
}
