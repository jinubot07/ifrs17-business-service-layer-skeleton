package com.koreanre.ifrs17.businessservice.api.controller;

import com.koreanre.ifrs17.businessservice.api.dto.request.StandardRequest;
import com.koreanre.ifrs17.businessservice.api.dto.response.StandardResponse;
import com.koreanre.ifrs17.businessservice.core.context.RequestContextResolver;
import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.exception.ErrorCode;
import com.koreanre.ifrs17.businessservice.core.executor.BusinessServiceExecutor;
import com.koreanre.ifrs17.businessservice.core.metadata.ServiceMetadata;
import com.koreanre.ifrs17.businessservice.core.metadata.ServiceMetadataRepository;
import com.koreanre.ifrs17.businessservice.core.response.StandardResponseBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Business Service 공통 Endpoint (설계서 5.1 URI 규칙 / 10.1 Reference Controller).
 *
 * <pre>
 * POST /api/business-services/v1/{serviceId}:execute
 * GET  /api/business-services/v1/catalog
 * GET  /api/business-services/v1/catalog/{serviceId}
 * GET  /api/business-services/v1/calls/{requestId}
 * </pre>
 *
 * <p>Controller 는 HTTP 수신과 위임만 담당한다.
 * 업무 SQL/업무규칙 작성은 금지된다(설계서 10.5 구현 시 금지사항).</p>
 */
@RestController
@RequestMapping(value = "/api/business-services/v1", produces = MediaType.APPLICATION_JSON_VALUE)
public class BusinessServiceController {

    private final BusinessServiceExecutor executor;
    private final ServiceMetadataRepository serviceMetadataRepository;
    private final RequestContextResolver requestContextResolver;
    private final StandardResponseBuilder responseBuilder;

    public BusinessServiceController(BusinessServiceExecutor executor,
                                     ServiceMetadataRepository serviceMetadataRepository,
                                     RequestContextResolver requestContextResolver,
                                     StandardResponseBuilder responseBuilder) {
        this.executor = executor;
        this.serviceMetadataRepository = serviceMetadataRepository;
        this.requestContextResolver = requestContextResolver;
        this.responseBuilder = responseBuilder;
    }

    /**
     * Business Service 실행 (설계서 5.1 / 10.1).
     *
     * <p>설계서 4.3 표준 처리 순서는 Executor 가 템플릿으로 수행한다.</p>
     */
    @PostMapping(value = "/{serviceId}:execute", consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StandardResponse<?>> execute(@PathVariable("serviceId") String serviceId,
                                                       @RequestBody StandardRequest request,
                                                       HttpServletRequest httpRequest) {
        return executor.execute(serviceId, request, httpRequest);
    }

    /** 서비스 Catalog 목록 조회 (설계서 5.1). */
    @GetMapping("/catalog")
    public ResponseEntity<StandardResponse<?>> catalog(HttpServletRequest httpRequest) {
        List<ServiceMetadata> catalog = serviceMetadataRepository.findAll();
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("totalCount", catalog.size());
        result.put("services", catalog);
        return ok(httpRequest, "CATALOG", result);
    }

    /** 서비스 Catalog 단건 조회 (설계서 5.1). */
    @GetMapping("/catalog/{serviceId}")
    public ResponseEntity<StandardResponse<?>> catalogDetail(@PathVariable("serviceId") String serviceId,
                                                             HttpServletRequest httpRequest) {
        ServiceMetadata metadata = serviceMetadataRepository.findById(serviceId);
        if (metadata == null) {
            ServiceContext context = requestContextResolver.resolve(httpRequest, serviceId, null);
            StandardResponse<Object> body = responseBuilder.error(context, ErrorCode.BS_SVC_404,
                    "요청한 서비스를 찾을 수 없습니다. serviceId=" + serviceId, null, null, 0L);
            return ResponseEntity.status(ErrorCode.BS_SVC_404.httpStatus()).body((StandardResponse<?>) body);
        }
        return ok(httpRequest, serviceId, metadata);
    }

    /**
     * 호출 이력 조회 (설계서 5.1).
     *
     * <p><b>[Draft/Skeleton]</b> 감사로그는 DB(bs_call_log) 대신 콘솔로만 출력되므로
     * 본 Endpoint 는 계약(URI/응답 규격)만 제공한다. 실제 구현 시 bs_call_log 조회로 대체한다.</p>
     */
    @GetMapping("/calls/{requestId}")
    public ResponseEntity<StandardResponse<?>> callLog(@PathVariable("requestId") String requestId,
                                                       HttpServletRequest httpRequest) {
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("requestId", requestId);
        result.put("available", false);
        result.put("storage", "CONSOLE_ONLY");
        result.put("note", "Skeleton 단계에서는 감사로그를 DB(bs_call_log)에 저장하지 않고 콘솔로만 출력한다."
                + " WAS 표준출력에서 [BSL-AUDIT-START] / [BSL-AUDIT-SUCCESS] / [BSL-AUDIT-FAIL] 로 확인한다.");
        return ok(httpRequest, "CALL_LOG", result);
    }

    private ResponseEntity<StandardResponse<?>> ok(HttpServletRequest httpRequest, String serviceId, Object result) {
        ServiceContext context = requestContextResolver.resolve(httpRequest, serviceId, null);
        StandardResponse<Object> body = responseBuilder.success(context, null, result, 0L);
        return ResponseEntity.ok((StandardResponse<?>) body);
    }
}
