package com.koreanre.ifrs17.businessservice.core.security;

import org.springframework.stereotype.Component;

/**
 * MaskingPolicy 의 Skeleton 구현체.
 *
 * <p>기능정의서 v2.7 - 11단계 (2) 결과 DTO 마스킹.
 * 5단계에서 조회한 ServiceMetadata.sensitivePolicy(부록 B sensitive_policy)를 정책 코드로 받아
 * 결과 DTO 의 민감정보 필드를 마스킹한다.</p>
 *
 * <p><b>[Mock]</b> 파일럿 5종은 상태·건수 중심 조회로 민감정보 필드가 없어 pass-through 이다.
 * 계약자 정보 조회 등 민감 항목을 가진 서비스가 추가되면 필드 단위 마스킹 구현이 필수이다(설계서 6.4).</p>
 */
@Component
public class DefaultMaskingPolicy implements MaskingPolicy {

    /** 부록 B sensitive_policy - 기본 마스킹 정책. */
    public static final String POLICY_DEFAULT_MASKING = "DEFAULT_MASKING";
    /** 부록 B sensitive_policy - 마스킹 미적용. */
    public static final String POLICY_NONE = "NONE";

    @Override
    public <T> T mask(T result, String policy) {
        if (result == null || POLICY_NONE.equals(policy)) {
            return result;
        }
        if (POLICY_DEFAULT_MASKING.equals(policy)) {
            return maskDefault(result);
        }
        // 미정의 정책 코드는 안전하게 기본 정책으로 처리한다.
        System.out.println("[BSL-MASKING] 미정의 마스킹 정책이라 기본 정책을 적용한다. policy=" + policy);
        return maskDefault(result);
    }

    /**
     * DEFAULT_MASKING 적용.
     *
     * <p><b>[Mock]</b> 파일럿 5종에는 마스킹 대상 필드가 없어 결과를 그대로 반환한다.
     * 실구현 시 주민번호·계좌번호·연락처 필드를 찾아 자리수 기반으로 치환한다.</p>
     */
    private <T> T maskDefault(T result) {
        return result;
    }
}
