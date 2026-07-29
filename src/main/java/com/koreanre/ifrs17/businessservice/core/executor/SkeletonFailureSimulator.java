package com.koreanre.ifrs17.businessservice.core.executor;

import com.koreanre.ifrs17.businessservice.core.exception.AuthenticationException;
import com.koreanre.ifrs17.businessservice.core.exception.AuthorizationException;
import com.koreanre.ifrs17.businessservice.core.exception.ErrorCode;
import com.koreanre.ifrs17.businessservice.core.exception.LegacyServiceException;
import com.koreanre.ifrs17.businessservice.core.exception.ServiceTimeoutException;
import com.koreanre.ifrs17.businessservice.core.exception.SystemException;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * <b>[Skeleton 전용]</b> 오류 시나리오 시뮬레이터.
 *
 * <p>DB·Legacy 연동 전 단계에서 설계서 11.2 필수 인수 시나리오(인증실패/권한없음/Legacy 오류/Timeout)의
 * 표준 Error Response 규격을 Postman 으로 검증할 수 있도록, 요청 파라미터
 * {@code __simulate} 값에 따라 표준 예외를 발생시킨다.</p>
 *
 * <p>지원 값: UNAUTHORIZED, FORBIDDEN, TIMEOUT, LEGACY_ERROR, SYSTEM_ERROR</p>
 *
 * <p><b>실제 구현 단계에서는 본 클래스를 반드시 제거한다.</b></p>
 */
@Component
public class SkeletonFailureSimulator {

    public static final String SIMULATE_KEY = "__simulate";

    /** 시뮬레이션 적용 시점. */
    public enum Phase {
        AUTHORIZE,
        PROCESS
    }

    public void simulate(Map<String, Object> parameters, Phase phase) {
        if (parameters == null) {
            return;
        }
        Object raw = parameters.get(SIMULATE_KEY);
        if (raw == null) {
            return;
        }
        String scenario = String.valueOf(raw).trim().toUpperCase();

        if (phase == Phase.AUTHORIZE) {
            if ("UNAUTHORIZED".equals(scenario)) {
                throw new AuthenticationException(ErrorCode.BS_AUTH_002, "인증 토큰이 만료되었습니다.(Skeleton 시뮬레이션)");
            }
            if ("FORBIDDEN".equals(scenario)) {
                throw new AuthorizationException("해당 서비스에 대한 권한이 없습니다.(Skeleton 시뮬레이션)");
            }
            return;
        }

        if ("TIMEOUT".equals(scenario)) {
            throw new ServiceTimeoutException("기간계 처리 시간이 초과되었습니다.(Skeleton 시뮬레이션)");
        }
        if ("LEGACY_ERROR".equals(scenario)) {
            throw new LegacyServiceException("기간계 서비스 호출 중 오류가 발생했습니다.(Skeleton 시뮬레이션)");
        }
        if ("SYSTEM_ERROR".equals(scenario)) {
            throw new SystemException("내부 시스템 오류(Skeleton 시뮬레이션)");
        }
    }
}
