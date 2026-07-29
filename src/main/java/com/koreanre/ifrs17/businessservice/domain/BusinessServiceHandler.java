package com.koreanre.ifrs17.businessservice.domain;

import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;

/**
 * 도메인 Business Service 표준 인터페이스 (설계서 4.4 표준 인터페이스 예시).
 *
 * <p>모든 Business Service 구현체는 본 인터페이스를 구현하고 Spring Bean 으로 등록되어야 하며,
 * 관리 Console 의 서비스 명세 등록 시 Bean 존재 여부와 Interface 구현 여부가 검증된다(설계서 8.3 / 8.4).</p>
 *
 * @param <REQ> 서비스별 요청 DTO
 * @param <RES> 서비스별 응답 DTO
 */
public interface BusinessServiceHandler<REQ, RES> {

    /** Catalog 에 등록된 Service ID (예: IFRS17.CLOSING.STATUS). */
    String serviceId();

    /** 표준 Request 의 parameters 를 바인딩할 요청 DTO 타입. */
    Class<REQ> requestType();

    /** 입력 Schema 및 업무 파라미터 검증 (설계서 4.3 - 7단계). */
    void validate(ServiceContext context, REQ request);

    /** 업무 파라미터 기반 추가 권한 확인 (설계서 6.1 - 5단계). */
    void authorize(ServiceContext context, REQ request);

    /** 업무 처리. Legacy Adapter 를 통해 기존 Service 를 호출한다 (설계서 4.3 - 9·10단계). */
    RES process(ServiceContext context, REQ request);
}
