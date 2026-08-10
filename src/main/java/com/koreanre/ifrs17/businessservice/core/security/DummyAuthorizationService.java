package com.koreanre.ifrs17.businessservice.core.security;

import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.exception.AuthorizationException;
import com.koreanre.ifrs17.businessservice.core.metadata.ServiceMetadata;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/**
 * AuthorizationService 의 Skeleton 구현체.
 *
 * <p>별첨E 표준처리순서정의서 v3.0 - 6단계 서비스/역할 권한 확인.</p>
 * <ul>
 *   <li>(1) BS_CLIENT_SERVICE (PK client_id + service_id) 등록 여부와 active_yn = 'Y' 판정</li>
 *   <li>(2) BS_SERVICE_ROLE (PK service_id + role_code) 의 필요 권한 코드와
 *       기존 IFRS17 권한정보(외부 권한 API)의 사용자 화면권한(Y/N)을 AND 비교</li>
 * </ul>
 *
 * <p>두 경우 모두 BS-AUTH-003(403) 이며 오류코드명으로 원인을 구분한다.
 * BS_SERVICE_ROLE 에는 active_yn 컬럼이 없으므로 상태값은 판정하지 않는다(설계서 6.2).</p>
 *
 * <p><b>[Mock]</b> BS_CLIENT_SERVICE 는 In-Memory 허용목록, 외부 권한 API 는 항상 'Y' 를
 * 반환하는 Stub 이다. DB · 권한 API 연동 시 각각 Mapper 조회와 Adapter 호출로 교체한다.</p>
 */
@Service
public class DummyAuthorizationService implements AuthorizationService {

    /** [Mock] BS_CLIENT_SERVICE - "clientId:serviceId" -> active_yn. */
    private final Map<String, String> bsClientService = new LinkedHashMap<String, String>();

    public DummyAuthorizationService() {
        String[] clients = {"MCP-IFRS17-01", "TEST-CLIENT", "CONSOLE-CLIENT"};
        String[] services = {
                "IFRS17.CLOSING.STATUS", "IFRS17.JOURNAL.STATUS", "IFRS17.EXPENSE.STATUS",
                "IFRS17.STATEMENT.STATUS", "IFRS17.CSM.STATUS", "IFRS17.SAMPLE.DISABLED"
        };
        for (String client : clients) {
            for (String service : services) {
                bsClientService.put(client + ":" + service, "Y");
            }
        }
    }

    @Override
    public void authorize(ServiceContext context, ServiceMetadata metadata) {
        String serviceId = metadata.getServiceId();

        // (1) BS_CLIENT_SERVICE 등록 여부 + active_yn = 'Y' [핵심 로직]
        String allowYn = bsClientService.get(context.getClientId() + ":" + serviceId);
        if (allowYn == null) {
            log(context, metadata, "DENY(Client 호출 허용 미등록)");
            throw new AuthorizationException("Client 호출이 허용되지 않은 서비스입니다. clientId="
                    + context.getClientId() + ", serviceId=" + serviceId);
        }
        if (!"Y".equals(allowYn)) {
            log(context, metadata, "DENY(Client 호출 차단)");
            throw new AuthorizationException("Client 호출이 차단된 서비스입니다. clientId="
                    + context.getClientId() + ", serviceId=" + serviceId);
        }

        // (2) BS_SERVICE_ROLE 의 필요 권한 코드 조회
        Set<String> requiredRoles = metadata.getRequiredRoles();
        if (requiredRoles == null || requiredRoles.isEmpty()) {
            log(context, metadata, "DENY(서비스 필요권한 미정의)");
            throw new AuthorizationException("서비스에 필요 권한이 정의되지 않았습니다. serviceId=" + serviceId);
        }

        // (2) 외부 권한 API 의 사용자 화면권한(Y/N) 과 AND 비교. 모두 'Y' 여야 통과한다.
        for (String roleCode : requiredRoles) {
            if (!"Y".equals(screenPermissionOf(context.getUserId(), roleCode))) {
                log(context, metadata, "DENY(사용자 화면권한 없음: " + roleCode + ")");
                throw new AuthorizationException("사용자 화면권한이 없습니다. roleCode=" + roleCode);
            }
        }

        log(context, metadata, "ALLOW");
    }

    /**
     * [Mock] 기존 IFRS17 권한 API 의 사용자 화면권한 조회 Stub.
     *
     * <p>Phase 1 REST API 는 조회만 수행하므로 액션은 inqy 로 고정된다.
     * 실구현 시 권한 API 연동 Adapter 호출로 교체한다(설계서 6.2).</p>
     *
     * @return 'Y' 또는 'N'
     */
    private String screenPermissionOf(String userId, String roleCode) {
        return "Y";
    }

    private void log(ServiceContext context, ServiceMetadata metadata, String result) {
        System.out.println("[BSL-AUTHZ] requestId=" + context.getRequestId()
                + ", clientId=" + context.getClientId()
                + ", serviceId=" + metadata.getServiceId()
                + ", userId=" + context.getUserId()
                + ", requiredRoles=" + metadata.getRequiredRoles()
                + ", result=" + result);
    }
}
