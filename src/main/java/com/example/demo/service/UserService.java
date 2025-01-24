package com.example.demo.service;

import com.example.demo.model.User;
import com.example.demo.repository.UserRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.util.concurrent.TimeUnit;

@Service
public class UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserService.class);  // Logger 설정

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private StringRedisTemplate redisTemplate;

    // 사용자 아이디로 사용자 조회 (캐시 우선 조회)
    public User getUserBySignupId(String signupId) {

        String cachedSignupId = redisTemplate.opsForValue().get("login:signupId:" + signupId);
        if (cachedSignupId != null) {
      
            logger.info("Redis에서 캐시된 사용자 로그인 처리");
            return userRepository.findBySignupId(signupId).orElse(null); 
        }
        return null;
    }

    // 사용자 등록 (비밀번호 암호화 포함)
    public User registerUser(User user) {
        if (userRepository.existsBySignupId(user.getSignupId())) {
            throw new IllegalArgumentException("이미 존재하는 아이디입니다.");
        }

        // 비밀번호 암호화
        String hashedPassword = BCrypt.hashpw(user.getSignupPassword(), BCrypt.gensalt());
        user.setSignupPassword(hashedPassword);

        return userRepository.save(user);
    }

    // 사용자 인증 (로그인)
    public boolean authenticateUser(String signupId, String password) {
        long startTime = System.currentTimeMillis(); 

        // Redis에서 로그인 정보 확인
        String cachedSignupId = redisTemplate.opsForValue().get("login:signupId:" + signupId);
        if (cachedSignupId != null) {
            
            logger.info("Redis에서 로그인 처리");
            long endTime = System.currentTimeMillis();  
            long elapsedTime = endTime - startTime; 
            logger.info("로그인 처리 시간 (Redis 사용): {} ms", elapsedTime);
            return true;
        }

        // Redis에 정보가 없으면 DB에서 사용자 조회
        User user = userRepository.findBySignupId(signupId).orElse(null);
        boolean isAuthenticated = false;

        if (user != null && BCrypt.checkpw(password, user.getSignupPassword())) {
            // 로그인 성공 시 Redis에 세션 정보 저장
            redisTemplate.opsForValue().set(
                "login:signupId:" + signupId, 
                signupId,                     
                1, TimeUnit.HOURS            
            );
            isAuthenticated = true;
        }

        long endTime = System.currentTimeMillis();  
        long elapsedTime = endTime - startTime;  

        // 로그 출력
        logger.info("로그인 처리 시간 (DB 조회 후 Redis 저장): {} ms", elapsedTime);

        return isAuthenticated;
    }
}
