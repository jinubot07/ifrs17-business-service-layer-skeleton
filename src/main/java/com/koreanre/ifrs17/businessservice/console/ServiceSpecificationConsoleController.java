package com.koreanre.ifrs17.businessservice.console;

import com.koreanre.ifrs17.businessservice.api.dto.response.ErrorDetail;
import com.koreanre.ifrs17.businessservice.api.dto.response.StandardResponse;
import com.koreanre.ifrs17.businessservice.core.context.IdGenerator;
import com.koreanre.ifrs17.businessservice.core.context.RequestContextResolver;
import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.exception.BusinessServiceException;
import com.koreanre.ifrs17.businessservice.core.exception.ErrorCode;
import com.koreanre.ifrs17.businessservice.core.metadata.ServiceMetadata;
import com.koreanre.ifrs17.businessservice.core.metadata.ServiceMetadataRepository;
import com.koreanre.ifrs17.businessservice.core.response.StandardResponseBuilder;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * CON-01 서비스 명세 관리 (설계서 8.2 화면 구성 / 8.3 서비스 명세 관리).
 *
 * <p>서비스 목록 조회, 신규 등록, 상세 조회, 수정, 사용/미사용 설정을 제공한다.</p>
 *
 * <p><b>[Skeleton / Draft]</b></p>
 * <ul>
 *   <li>화면(UI)은 Skeleton 범위가 아니며 본 클래스는 <b>Console 백엔드 API</b> 만 제공한다.</li>
 *   <li>Console URI 는 설계서에 명시되지 않아 임시 규칙을 사용한다(미확정).</li>
 *   <li>메타데이터는 DB(bs_service) 가 아닌 In-Memory Catalog 에 저장된다.</li>
 *   <li>운영자 역할 제한(설계서 6.2)은 SSO 연동 단계에서 적용한다.</li>
 * </ul>
 */
@RestController
@RequestMapping(value = "/api/business-service-console/v1/services",
        produces = MediaType.APPLICATION_JSON_VALUE)
public class ServiceSpecificationConsoleController {

    private final ServiceMetadataRepository serviceMetadataRepository;
    private final ServiceSpecificationValidator specificationValidator;
    private final RequestContextResolver requestContextResolver;
    private final StandardResponseBuilder responseBuilder;
    private final IdGenerator idGenerator;

    public ServiceSpecificationConsoleController(ServiceMetadataRepository serviceMetadataRepository,
                                                 ServiceSpecificationValidator specificationValidator,
                                                 RequestContextResolver requestContextResolver,
                                                 StandardResponseBuilder responseBuilder,
                                                 IdGenerator idGenerator) {
        this.serviceMetadataRepository = serviceMetadataRepository;
        this.specificationValidator = specificationValidator;
        this.requestContextResolver = requestContextResolver;
        this.responseBuilder = responseBuilder;
        this.idGenerator = idGenerator;
    }

    /** 서비스 목록 조회 (설계서 8.3). */
    @GetMapping
    public ResponseEntity<StandardResponse<?>> list(HttpServletRequest httpRequest) {
        List<ServiceMetadata> services = serviceMetadataRepository.findAll();
        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("totalCount", services.size());
        result.put("services", services);
        return ok(httpRequest, "CONSOLE.SERVICE.LIST", result);
    }

    /** 서비스 상세 조회 (설계서 8.7 CON-ACC-04). */
    @GetMapping("/{serviceId}")
    public ResponseEntity<StandardResponse<?>> detail(@PathVariable("serviceId") String serviceId,
                                                      HttpServletRequest httpRequest) {
        ServiceMetadata metadata = serviceMetadataRepository.findById(serviceId);
        if (metadata == null) {
            return error(httpRequest, serviceId, ErrorCode.BS_SVC_404,
                    "등록된 서비스가 없습니다. serviceId=" + serviceId, null);
        }
        return ok(httpRequest, "CONSOLE.SERVICE.DETAIL", metadata);
    }

    /**
     * 서비스 명세 신규 등록 / 수정 (설계서 8.3, 8.4 - 3·4단계).
     *
     * <p>저장 전 Bean 존재·Interface 구현·Service ID 일치를 자동 검증한다.</p>
     */
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<StandardResponse<?>> save(@RequestBody ServiceSpecificationForm form,
                                                    HttpServletRequest httpRequest) {
        try {
            specificationValidator.validate(form);
        } catch (BusinessServiceException e) {
            // 설계서 4.5 - 입력 오류는 필드별 오류를 함께 반환한다.
            return error(httpRequest, form.getServiceId(), e.getErrorCode(), e.getMessage(), e.getDetails());
        }

        ServiceMetadata previous = serviceMetadataRepository.findById(form.getServiceId());
        ServiceMetadata saved = serviceMetadataRepository.save(toMetadata(form, previous));

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("saved", true);
        result.put("mode", previous == null ? "INSERT" : "UPDATE");
        result.put("beanValidation", "PASSED");
        result.put("service", saved);
        return ok(httpRequest, "CONSOLE.SERVICE.SAVE", result);
    }

    /**
     * 사용 / 미사용 전환 (설계서 8.4 - 6·7단계, 8.7 CON-ACC-02·CON-ACC-03).
     *
     * <p>미사용으로 변경하면 공통 Endpoint 에서 즉시 BS-SVC-404 로 차단된다.</p>
     */
    @PatchMapping("/{serviceId}/active")
    public ResponseEntity<StandardResponse<?>> changeActive(@PathVariable("serviceId") String serviceId,
                                                            @RequestParam("use") boolean use,
                                                            HttpServletRequest httpRequest) {
        ServiceMetadata metadata = serviceMetadataRepository.findById(serviceId);
        if (metadata == null) {
            return error(httpRequest, serviceId, ErrorCode.BS_SVC_404,
                    "등록된 서비스가 없습니다. serviceId=" + serviceId, null);
        }
        ServiceMetadata updated = serviceMetadataRepository.updateActive(serviceId, use);

        Map<String, Object> result = new LinkedHashMap<String, Object>();
        result.put("serviceId", serviceId);
        result.put("active", updated.isActive());
        result.put("message", updated.isActive()
                ? "사용으로 전환되어 공통 Endpoint 호출이 허용됩니다."
                : "미사용으로 전환되어 공통 Endpoint 신규 호출이 차단됩니다.");
        return ok(httpRequest, "CONSOLE.SERVICE.ACTIVE", result);
    }

    private ServiceMetadata toMetadata(ServiceSpecificationForm form, ServiceMetadata previous) {
        return ServiceMetadata.builder(form.getServiceId())
                .serviceName(form.getServiceName())
                .domainCode(form.getDomainCode() != null ? form.getDomainCode() : domainOf(form.getServiceId()))
                .version(form.getVersion())
                .implementationBean(form.getImplementationBean())
                .description(form.getDescription())
                .requestSchema(form.getRequestSchema())
                .responseSchema(form.getResponseSchema())
                .timeoutMs(form.getTimeoutMs())
                .requiredRoles(form.getRequiredRoles().toArray(new String[0]))
                .active(form.isUseYn())
                .legacyBatchProgramId(previous != null ? previous.getLegacyBatchProgramId() : "TBD")
                .legacyScreenName(previous != null ? previous.getLegacyScreenName() : "TBD")
                .build();
    }

    /** IFRS17.CLOSING.STATUS -> CLOSING */
    private String domainOf(String serviceId) {
        String[] parts = serviceId.split("\\.");
        return parts.length >= 2 ? parts[1] : "COMMON";
    }

    private ResponseEntity<StandardResponse<?>> ok(HttpServletRequest httpRequest, String screenId, Object result) {
        ServiceContext context = requestContextResolver.resolve(httpRequest, screenId, null);
        StandardResponse<Object> body = responseBuilder.success(context, null, result, 0L);
        return ResponseEntity.ok((StandardResponse<?>) body);
    }

    private ResponseEntity<StandardResponse<?>> error(HttpServletRequest httpRequest, String serviceId,
                                                      ErrorCode errorCode, String message,
                                                      List<ErrorDetail> details) {
        ServiceContext context = requestContextResolver.resolve(httpRequest, serviceId, null);
        // 설계서 5.5 - 오류 응답에는 서버 로그 추적용 Error ID 를 포함한다.
        String errorId = idGenerator.newErrorId();
        System.out.println("[BSL-CONSOLE-ERROR] errorId=" + errorId
                + ", requestId=" + context.getRequestId()
                + ", code=" + errorCode.code() + ", message=" + message);
        StandardResponse<Object> body = responseBuilder.error(context, errorCode, message, errorId, details, 0L);
        return ResponseEntity.status(errorCode.httpStatus()).body((StandardResponse<?>) body);
    }
}
