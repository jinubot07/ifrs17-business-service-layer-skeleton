package com.koreanre.ifrs17.businessservice.core.security;

import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.metadata.ServiceMetadata;

/**
 * 서비스/역할 권한 확인 (설계서 4.3 - 6단계, 6.2 권한 원칙).
 */
public interface AuthorizationService {

    /**
     * 사용자 역할과 서비스 허용 역할(BS_SERVICE_ROLE)을 대조하여 호출 허용 여부를 판정한다.
     *
     * @throws com.koreanre.ifrs17.businessservice.core.exception.AuthorizationException BS-AUTH-003
     */
    void authorize(ServiceContext context, ServiceMetadata metadata);
}
