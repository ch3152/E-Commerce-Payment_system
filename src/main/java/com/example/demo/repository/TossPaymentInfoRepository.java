package com.example.demo.repository;

import com.example.demo.model.TossPaymentInfo;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TossPaymentInfoRepository extends JpaRepository<TossPaymentInfo, Long> {
    // 추가적인 쿼리 메서드를 작성할 수 있습니다.
}
