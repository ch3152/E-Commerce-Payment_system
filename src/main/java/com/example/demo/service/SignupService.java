package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SignupService {

    @Autowired
    private UserRepository userRepository;

    public void registerUser(User user) throws Exception {
        if (userRepository.existsBySignupId(user.getSignupId())) {
            throw new Exception("이미 존재하는 아이디입니다.");
        }
        userRepository.save(user);  // DB에 저장
    }
}
