package com.koreanre.ifrs17.businessservice.core.security;

/**
 * 민감정보 마스킹 (설계서 4.2 - MaskingPolicy, 6.4 로그 보존 및 마스킹).
 *
 * <p>필수 인터페이스: {@code mask(result, policy)}</p>
 */
public interface MaskingPolicy {

    /**
     * 서비스별 마스킹 정책에 따라 결과를 마스킹한다.
     *
     * @param result 업무 처리 결과
     * @param policy 마스킹 정책 코드 (부록 B sensitive_policy, 예: DEFAULT_MASKING)
     * @return 마스킹된 결과
     */
    <T> T mask(T result, String policy);
}
