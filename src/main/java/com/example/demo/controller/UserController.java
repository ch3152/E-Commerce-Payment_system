package com.example.demo.controller;

import com.example.demo.model.User;
import com.example.demo.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
    }

    // 로그인 요청 처리
    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody User user, HttpSession session) {
        boolean isAuthenticated = userService.authenticateUser(user.getSignupId(), user.getSignupPassword());
        if (isAuthenticated) {
            session.setAttribute("signupId", user.getSignupId()); // 로그인 성공 시 세션에 signupId 저장
            return ResponseEntity.ok("로그인 성공");
        }
        return ResponseEntity.status(401).body("아이디 또는 비밀번호가 일치하지 않습니다.");
    }

    // 회원가입 요청 처리
    @PostMapping("/signup")
    public ResponseEntity<String> signup(@RequestBody User user) {
        // 아이디가 이미 존재하는지 체크
        if (userService.registerUser(user) == null) {
            return ResponseEntity.status(400).body("이미 존재하는 아이디입니다.");
        }
        return ResponseEntity.ok("회원가입 성공");
    }

    // 로그인된 사용자 정보 가져오기 (세션에서 signupId 사용)
    @GetMapping("/getUser")
    public ResponseEntity<String> getUser(HttpSession session) {
        String signupId = (String) session.getAttribute("signupId");
        if (signupId != null) {
            User user = userService.getUserBySignupId(signupId);
            if (user != null) {
                return ResponseEntity.ok(user.getName()); // 사용자 이름 반환
            }
        }
        return ResponseEntity.ok("");  // 로그인된 사용자 없을 때 빈 문자열 반환
    }
}
