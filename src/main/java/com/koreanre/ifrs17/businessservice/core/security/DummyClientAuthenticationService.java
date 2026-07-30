package com.koreanre.ifrs17.businessservice.core.security;

import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.exception.AuthenticationException;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

/**
 * ClientAuthenticationService 의 Skeleton 구현체.
 *
 * <p><b>[Mock]</b> Client 허용목록(BS_CLIENT) 및 SSO 토큰 검증 대신,
 * 필수 Header 인 X-Client-ID 존재 여부만 확인한다(설계서 5.2 필수 Header).</p>
 *
 * <p>실제 구현 시: BS_CLIENT / BS_CLIENT_SERVICE 조회 + SSO Token 검증 + 허용 IP 확인(설계서 3.3).</p>
 */
@Service
public class DummyClientAuthenticationService implements ClientAuthenticationService {

    @Override
    public void authenticate(ServiceContext context) {
        if (!StringUtils.hasText(context.getClientId())) {
            throw new AuthenticationException("필수 Header 인 X-Client-ID 가 없습니다.");
        }
        // [Mock] 항상 인증 성공. Client 허용목록/토큰 검증은 실제 구현 시 추가한다.
        System.out.println("[BSL-AUTHN] requestId=" + context.getRequestId()
                + ", clientId=" + context.getClientId()
                + ", userId=" + context.getUserId()
                + ", authType=" + context.getAuthType()
                + ", result=AUTHENTICATED(Mock)");
    }
}
