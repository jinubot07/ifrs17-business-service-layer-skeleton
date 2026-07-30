package com.koreanre.ifrs17.businessservice.core.security;

import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.metadata.ServiceMetadata;
import org.springframework.stereotype.Service;

/**
 * AuthorizationService 의 Skeleton 구현체.
 *
 * <p><b>[Mock]</b> 권한 검사는 항상 통과(Always True)한다.
 * 실제 구현 시 BS_SERVICE_ROLE 과 기존 IFRS17 권한정보를 함께 사용해야 한다(설계서 6.2).</p>
 */
@Service
public class DummyAuthorizationService implements AuthorizationService {

    @Override
    public void authorize(ServiceContext context, ServiceMetadata metadata) {
        // [Mock] Always True. 실제 구현 시:
        //   1) BS_SERVICE_ROLE 조회 -> metadata.getRequiredRoles()
        //   2) context.getRoles() 와 교집합 확인
        //   3) 불일치 시 AuthorizationException(BS-AUTH-003)
        System.out.println("[BSL-AUTHZ] requestId=" + context.getRequestId()
                + ", serviceId=" + metadata.getServiceId()
                + ", userRoles=" + context.getRoles()
                + ", requiredRoles=" + metadata.getRequiredRoles()
                + ", result=ALLOW(Mock)");
    }
}
