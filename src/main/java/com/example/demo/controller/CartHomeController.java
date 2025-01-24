package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class CartHomeController {

    // 장바구니 페이지 반환
    @GetMapping("/cart")
    public String cartPage() {
        return "cart"; 
    }
}
