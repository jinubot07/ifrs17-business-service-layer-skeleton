package com.koreanre.ifrs17.businessservice.core.executor;

import com.koreanre.ifrs17.businessservice.api.dto.request.StandardRequest;
import com.koreanre.ifrs17.businessservice.api.dto.response.StandardResponse;
import org.springframework.http.ResponseEntity;

import javax.servlet.http.HttpServletRequest;

/**
 * 공통 처리 템플릿 (설계서 4.2 - BusinessServiceExecutor).
 *
 * <p>필수 인터페이스: {@code execute(context, handler)} — 본 Skeleton 에서는 설계서 10.1
 * Reference Controller 형태에 맞추어 {@code execute(serviceId, request, httpRequest)} 로 노출하고,
 * Context 생성과 Handler 조회는 Executor 내부에서 수행한다.</p>
 *
 * <p>설계서 4.3 표준 처리 순서 13단계 및 10.4 의사코드를 try-catch 템플릿으로 구현한다.</p>
 */
public interface BusinessServiceExecutor {

    ResponseEntity<StandardResponse<?>> execute(String serviceId, StandardRequest request,
                                                HttpServletRequest httpRequest);
}
