package com.koreanre.ifrs17.businessservice.core.security;

import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;

/**
 * 호출 Client 및 사용자 인증 (설계서 4.3 - 3·4단계, 6.1 인증과 권한 흐름).
 */
public interface ClientAuthenticationService {

    /**
     * 호출 Client(X-Client-ID) 및 사용자 SSO Context 유효성을 검증한다.
     *
     * @throws com.koreanre.ifrs17.businessservice.core.exception.AuthenticationException BS-AUTH-001 / BS-AUTH-002
     */
    void authenticate(ServiceContext context);
}
