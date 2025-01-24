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
        // Redis에서 먼저 조회
        String cachedSignupId = redisTemplate.opsForValue().get("login:signupId:" + signupId);
        if (cachedSignupId != null) {
            // 캐시에서 값이 있으면 Redis에서 바로 사용자 정보를 반환
            logger.info("Redis에서 캐시된 사용자 로그인 처리");
            return userRepository.findBySignupId(signupId).orElse(null);  // Redis 값이 있어도 DB 조회는 한 번만 이루어짐
        }
        return null;  // Redis에 없으면 null 반환
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
        long startTime = System.currentTimeMillis();  // 로그인 시간 측정 시작

        // Redis에서 로그인 정보 확인
        String cachedSignupId = redisTemplate.opsForValue().get("login:signupId:" + signupId);
        if (cachedSignupId != null) {
            // Redis에 로그인 정보가 있으면 DB 조회 없이 인증 처리
            logger.info("Redis에서 로그인 처리");
            long endTime = System.currentTimeMillis();  // Redis에서 인증이 완료된 후 시간 측정
            long elapsedTime = endTime - startTime;  // 로그인 시간 계산
            logger.info("로그인 처리 시간 (Redis 사용): {} ms", elapsedTime);
            return true;
        }

        // Redis에 정보가 없으면 DB에서 사용자 조회
        User user = userRepository.findBySignupId(signupId).orElse(null);
        boolean isAuthenticated = false;

        if (user != null && BCrypt.checkpw(password, user.getSignupPassword())) {
            // 로그인 성공 시 Redis에 세션 정보 저장
            redisTemplate.opsForValue().set(
                "login:signupId:" + signupId,  // Redis에 저장할 키
                signupId,                      // 저장할 값
                1, TimeUnit.HOURS              // TTL: 1시간 동안 유효
            );
            isAuthenticated = true;
        }

        long endTime = System.currentTimeMillis();  // 로그인 시간 측정 끝
        long elapsedTime = endTime - startTime;  // 로그인 시간 계산

        // 로그 출력
        logger.info("로그인 처리 시간 (DB 조회 후 Redis 저장): {} ms", elapsedTime);

        return isAuthenticated;
    }
}
