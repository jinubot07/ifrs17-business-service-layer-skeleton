package com.koreanre.ifrs17.businessservice.core.security;

import org.springframework.stereotype.Component;

/**
 * MaskingPolicy 의 Skeleton 구현체.
 *
 * <p><b>[Mock]</b> 파일럿 5종은 상태/건수 중심 조회로 민감정보가 없어 결과를 그대로 반환한다.
 * 실제 구현 시 주민번호·계좌번호·연락처 필드에 대해 정책 기반 마스킹을 적용한다(설계서 6.4).</p>
 */
@Component
public class DefaultMaskingPolicy implements MaskingPolicy {

    @Override
    public <T> T mask(T result, String policy) {
        // [Mock] pass-through
        return result;
    }
}
