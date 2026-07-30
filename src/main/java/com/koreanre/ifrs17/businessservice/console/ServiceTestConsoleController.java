package com.koreanre.ifrs17.businessservice.console;

import com.koreanre.ifrs17.businessservice.api.dto.request.StandardRequest;
import com.koreanre.ifrs17.businessservice.api.dto.response.StandardResponse;
import com.koreanre.ifrs17.businessservice.core.executor.BusinessServiceExecutor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;

/**
 * CON-02 서비스 Test (설계서 8.2 화면 구성 / 8.6 서비스 Test 처리 기준).
 *
 * <p>설계서 8.6: "Test 실행은 운영 호출과 동일한 Controller, Dispatcher, 권한검사, Handler,
 * 감사로그 경로를 사용한다." 따라서 본 Endpoint 는 자체 로직 없이
 * {@link BusinessServiceExecutor} 에 그대로 위임한다.</p>
 *
 * <p>응답에는 HTTP 상태, 표준 Response JSON, 오류코드·오류메시지, 처리시간(elapsedMs)이 포함되며
 * 이는 표준 Envelope 이 이미 제공한다(설계서 8.6).</p>
 *
 * <p><b>[Skeleton / Draft]</b> 화면(UI)은 범위가 아니며, Console URI 는 미확정 임시 규칙이다.
 * Phase 1 운영환경 Test 는 Read 서비스만 허용한다(설계서 8.6).</p>
 */
@RestController
@RequestMapping(value = "/api/business-service-console/v1/services",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ServiceTestConsoleController {

    private final BusinessServiceExecutor executor;

    public ServiceTestConsoleController(BusinessServiceExecutor executor) {
        this.executor = executor;
    }

    /** 서비스 Test 실행 - 운영 호출과 동일 경로로 위임한다. */
    @PostMapping(value = "/{serviceId}/test", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StandardResponse<?>> test(@PathVariable("serviceId") String serviceId,
                                                    @RequestBody StandardRequest request,
                                                    HttpServletRequest httpRequest) {
        System.out.println("[BSL-CONSOLE] 서비스 Test 실행: serviceId=" + serviceId
                + " (운영 호출과 동일 경로 - 감사로그 동일 기록)");
        return executor.execute(serviceId, request, httpRequest);
    }
}
