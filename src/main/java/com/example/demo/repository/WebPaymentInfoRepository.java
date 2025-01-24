package com.example.demo.repository;

import com.example.demo.model.WebPaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface WebPaymentInfoRepository extends JpaRepository<WebPaymentInfo, Long> {
    // 특정 사용자에 대한 결제 정보 조회
    List<WebPaymentInfo> findByUserId(Long userId);
}
