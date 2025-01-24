package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class SignupController {

    @Autowired
    private UserService userService;

    // 회원가입 폼 보여주기
    @GetMapping("/signup")
    public String showSignupForm(Model model) {
        model.addAttribute("user", new User());
        return "signup"; 
    }

    // 회원가입 처리
    @PostMapping("/signup")
    public String signupUser(User user, String confirmPassword, BindingResult result, Model model) {
        // 비밀번호 확인
        if (!user.getSignupPassword().equals(confirmPassword)) {
            result.rejectValue("signupPassword", "error.user", "비밀번호가 일치하지 않습니다.");
            return "signup";
        }

        if (result.hasErrors()) {
            return "signup";  
        }

        try {
            userService.registerUser(user);  // 회원가입 서비스 호출
            model.addAttribute("message", "회원가입 성공!");
            return "index";
        } catch (Exception e) {
            model.addAttribute("message", "회원가입 실패: " + e.getMessage());
            return "signup"; 
        }
    }
}
