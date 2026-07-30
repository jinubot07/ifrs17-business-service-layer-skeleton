package com.koreanre.ifrs17.businessservice.core.context;

import com.koreanre.ifrs17.businessservice.api.dto.request.StandardRequest;

import javax.servlet.http.HttpServletRequest;

/**
 * 요청 Context 생성 컴포넌트 (설계서 4.2 - RequestContextResolver).
 *
 * <p>필수 인터페이스: {@code resolve(HttpServletRequest)}</p>
 *
 * <p>설계서 4.3 표준 처리 순서의 (1) HTTP Header 및 Request Body 수신,
 * (2) Request ID 생성 또는 검증, (4) SSO 사용자 Context 추출 단계를 담당한다.</p>
 */
public interface RequestContextResolver {

    /**
     * HTTP 요청 Header 로부터 요청 단위 Context 를 생성한다.
     *
     * @param httpRequest   HTTP 요청
     * @param serviceId     호출 대상 Service ID (URI Path Variable)
     * @param request       표준 Request Body
     * @return 요청 단위 Context
     */
    ServiceContext resolve(HttpServletRequest httpRequest, String serviceId, StandardRequest request);
}
