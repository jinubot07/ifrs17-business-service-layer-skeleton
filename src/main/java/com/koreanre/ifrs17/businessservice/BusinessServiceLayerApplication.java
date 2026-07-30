package com.koreanre.ifrs17.businessservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * IFRS17 Business Service Layer - Skeleton Application.
 *
 * <p>설계서 IFRS17-BSL-SDD-001 v1.3 의 4장(공통 처리 Framework) / 5장(REST API 표준)을
 * Java Interface + Dummy 구현체로만 구성한 "통신 껍데기"이다.</p>
 *
 * <p>본 Skeleton 은 DB(SqlMap/MyBatis)에 접근하지 않으며, 모든 하위 레이어는
 * 하드코딩된 Mock 데이터를 반환한다.</p>
 */
@SpringBootApplication
public class BusinessServiceLayerApplication {

    public static void main(String[] args) {
        SpringApplication.run(BusinessServiceLayerApplication.class, args);
    }
}
