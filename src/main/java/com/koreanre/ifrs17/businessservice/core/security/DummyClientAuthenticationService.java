package com.koreanre.ifrs17.businessservice.core.security;

import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.exception.AuthenticationException;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * ClientAuthenticationService 의 Skeleton 구현체.
 *
 * <p>기능정의서 v2.7 - 3단계 (2) 호출 Client 검증.
 * BS_CLIENT 의 PK 인 client_id 에 X-Client-ID 와 같은 데이터가 있는지 확인하고,
 * 있으면 active_yn 이 'Y' 인지 확인한다. 미등록과 비활성 모두 BS-AUTH-001(401) 이며,
 * 응답 오류코드로는 구분되지 않고 오류코드명과 감사 로그로 식별한다.</p>
 *
 * <p>X-Client-ID 미입력 여부(3단계 (1), BS-VAL-001)는 RequestContextResolver 가 담당하므로
 * 본 구현체는 등록 여부와 사용 여부만 판정한다.</p>
 *
 * <p><b>[Mock]</b> BS_CLIENT 테이블 대신 In-Memory 허용목록을 사용한다.
 * DB 연동 시 BsClientMapper 조회로 교체한다.</p>
 */
@Service
public class DummyClientAuthenticationService implements ClientAuthenticationService {

    /** [Mock] BS_CLIENT (PK client_id) - client_id -> active_yn. */
    private final Map<String, String> bsClient = new LinkedHashMap<String, String>();

    public DummyClientAuthenticationService() {
        bsClient.put("MCP-IFRS17-01", "Y");
        bsClient.put("TEST-CLIENT", "Y");
        bsClient.put("CONSOLE-CLIENT", "Y");
        // [테스트용] 비활성 Client - active_yn = 'N' 이면 인증 차단 [핵심 로직]
        bsClient.put("MCP-BLOCKED-01", "N");
    }

    @Override
    public void authenticate(ServiceContext context) {
        String clientId = context.getClientId();

        // (2)-1 BS_CLIENT 등록 여부
        String activeYn = bsClient.get(clientId);
        if (activeYn == null) {
            log(context, "DENY(미등록 Client)");
            throw new AuthenticationException("등록되지 않은 Client 입니다. clientId=" + clientId);
        }

        // (2)-2 active_yn = 'Y' 여부
        if (!"Y".equals(activeYn)) {
            log(context, "DENY(미사용 Client)");
            throw new AuthenticationException("사용 중지된 Client 입니다. clientId=" + clientId);
        }

        log(context, "AUTHENTICATED");
    }

    private void log(ServiceContext context, String result) {
        System.out.println("[BSL-AUTHN] requestId=" + context.getRequestId()
                + ", clientId=" + context.getClientId()
                + ", userId=" + context.getUserId()
                + ", authType=" + context.getAuthType()
                + ", result=" + result);
    }
}
