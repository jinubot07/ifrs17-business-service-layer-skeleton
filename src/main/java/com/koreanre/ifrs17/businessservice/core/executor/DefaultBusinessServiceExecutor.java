package com.koreanre.ifrs17.businessservice.core.executor;

import com.koreanre.ifrs17.businessservice.api.dto.request.StandardRequest;
import com.koreanre.ifrs17.businessservice.api.dto.response.StandardResponse;
import com.koreanre.ifrs17.businessservice.api.dto.response.StatusServiceResponse;
import com.koreanre.ifrs17.businessservice.core.audit.AuditLogger;
import com.koreanre.ifrs17.businessservice.core.audit.AuditRecord;
import com.koreanre.ifrs17.businessservice.core.context.IdGenerator;
import com.koreanre.ifrs17.businessservice.core.context.RequestContextResolver;
import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.dispatcher.BusinessServiceDispatcher;
import com.koreanre.ifrs17.businessservice.core.exception.BusinessServiceException;
import com.koreanre.ifrs17.businessservice.core.exception.ErrorCode;
import com.koreanre.ifrs17.businessservice.core.exception.ServiceNotFoundException;
import com.koreanre.ifrs17.businessservice.core.metadata.ServiceMetadata;
import com.koreanre.ifrs17.businessservice.core.metadata.ServiceMetadataRepository;
import com.koreanre.ifrs17.businessservice.core.response.StandardResponseBuilder;
import com.koreanre.ifrs17.businessservice.core.security.AuthorizationService;
import com.koreanre.ifrs17.businessservice.core.security.ClientAuthenticationService;
import com.koreanre.ifrs17.businessservice.core.security.MaskingPolicy;
import com.koreanre.ifrs17.businessservice.core.validator.ParameterBinder;
import com.koreanre.ifrs17.businessservice.core.validator.StandardRequestValidator;
import com.koreanre.ifrs17.businessservice.core.workflow.BusinessServiceHandler;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

/**
 * BusinessServiceExecutor 기본 구현체 — 설계서 4.3 표준 처리 순서(13단계)의 템플릿.
 *
 * <p>설계서 10.4 공통 Executor 의사코드:</p>
 * <pre>
 * resolveContext -&gt; validateClient -&gt; loadMetadata -&gt; validateVersion
 * -&gt; authorizeService -&gt; writeAuditStart -&gt; invokeHandler
 * -&gt; maskResult -&gt; buildSuccessResponse -&gt; writeAuditSuccess
 * catch ValidationException/AuthException/Timeout/SystemException
 * -&gt; convertStandardError -&gt; writeAuditFailure -&gt; return
 * </pre>
 *
 * <p><b>[Skeleton]</b> 각 하위 컴포넌트는 Dummy 구현체이며 DB 접근이 없다.
 * 처리 순서/예외변환/감사/응답 규격만 실제와 동일하게 유지한다.</p>
 */
@Component
public class DefaultBusinessServiceExecutor implements BusinessServiceExecutor {

    private final RequestContextResolver requestContextResolver;
    private final ClientAuthenticationService clientAuthenticationService;
    private final ServiceMetadataRepository serviceMetadataRepository;
    private final AuthorizationService authorizationService;
    private final StandardRequestValidator standardRequestValidator;
    private final ParameterBinder parameterBinder;
    private final BusinessServiceDispatcher dispatcher;
    private final AuditLogger auditLogger;
    private final MaskingPolicy maskingPolicy;
    private final StandardResponseBuilder responseBuilder;
    private final IdGenerator idGenerator;
    private final SkeletonFailureSimulator failureSimulator;

    public DefaultBusinessServiceExecutor(RequestContextResolver requestContextResolver,
                                          ClientAuthenticationService clientAuthenticationService,
                                          ServiceMetadataRepository serviceMetadataRepository,
                                          AuthorizationService authorizationService,
                                          StandardRequestValidator standardRequestValidator,
                                          ParameterBinder parameterBinder,
                                          BusinessServiceDispatcher dispatcher,
                                          AuditLogger auditLogger,
                                          MaskingPolicy maskingPolicy,
                                          StandardResponseBuilder responseBuilder,
                                          IdGenerator idGenerator,
                                          SkeletonFailureSimulator failureSimulator) {
        this.requestContextResolver = requestContextResolver;
        this.clientAuthenticationService = clientAuthenticationService;
        this.serviceMetadataRepository = serviceMetadataRepository;
        this.authorizationService = authorizationService;
        this.standardRequestValidator = standardRequestValidator;
        this.parameterBinder = parameterBinder;
        this.dispatcher = dispatcher;
        this.auditLogger = auditLogger;
        this.maskingPolicy = maskingPolicy;
        this.responseBuilder = responseBuilder;
        this.idGenerator = idGenerator;
        this.failureSimulator = failureSimulator;
    }

    @Override
    @SuppressWarnings("unchecked")
    public ResponseEntity<StandardResponse<?>> execute(String serviceId, StandardRequest request,
                                                       HttpServletRequest httpRequest) {
        long startedAt = System.currentTimeMillis();
        ServiceContext context = null;
        AuditRecord auditRecord = null;

        try {
            // (1)(2) HTTP Header/Body 수신 + Request ID 생성 또는 검증 + (4) SSO 사용자 Context 추출
            // resolve() 는 3단계 (1) X-Client-ID 필수 입력 체크까지 수행하므로 예외로 빠질 수 있다.
            // 그때도 오류 응답에 추적 ID 가 실리도록 최소 Context 를 확보한 뒤 예외를 그대로 올린다.
            try {
                context = requestContextResolver.resolve(httpRequest, serviceId, request);
            } catch (RuntimeException e) {
                context = traceOnlyContext(httpRequest, serviceId);
                throw e;
            }

            // (3) 호출 Client 검증
            clientAuthenticationService.authenticate(context);

            // (5) Service Catalog 에서 활성 버전 조회
            ServiceMetadata metadata = serviceMetadataRepository.findActive(serviceId, request.getServiceVersion());
            if (metadata == null) {
                throw new ServiceNotFoundException("서비스가 없거나 비활성 상태입니다. serviceId=" + serviceId
                        + ", serviceVersion=" + request.getServiceVersion());
            }
            context.setServiceVersion(metadata.getVersion());

            // (6) 서비스/역할 권한 확인
            authorizationService.authorize(context, metadata);
            failureSimulator.simulate(request.getParameters(), SkeletonFailureSimulator.Phase.AUTHORIZE);

            // (7) 입력 Schema 및 업무 파라미터 검증
            standardRequestValidator.validate(request, metadata);

            BusinessServiceHandler<Object, Object> handler =
                    (BusinessServiceHandler<Object, Object>) dispatcher.dispatch(context, request.getParameters());
            Object domainRequest = parameterBinder.bind(request.getParameters(), handler.requestType());
            handler.validate(context, domainRequest);

            // (6-2) 업무 파라미터 기반 추가 권한 확인 (설계서 6.1 - 5단계)
            handler.authorize(context, domainRequest);

            // (8) 감사 시작 로그 기록
            auditRecord = auditLogger.start(context, metadata, request.getParameters());

            // (9)(10) Business Service Handler 실행 -> Legacy Adapter 를 통한 기존 Service 호출
            failureSimulator.simulate(request.getParameters(), SkeletonFailureSimulator.Phase.PROCESS);
            Object result = handler.process(context, domainRequest);

            // (11) 결과 DTO 를 표준 JSON 으로 변환 및 마스킹
            Object masked = maskingPolicy.mask(result, metadata.getSensitivePolicy());

            long elapsedMs = System.currentTimeMillis() - startedAt;

            // (12) 감사 성공 로그 기록
            auditLogger.success(auditRecord, elapsedMs, resultCountOf(masked));

            // (13) 표준 Response 반환
            StandardResponse<Object> response = responseBuilder.success(context, metadata, masked, elapsedMs);
            return ResponseEntity.status(HttpStatus.OK).body((StandardResponse<?>) response);

        } catch (BusinessServiceException e) {
            // 표준 예외 -> 표준 Error Response 변환 (설계서 4.5)
            return handleFailure(context, auditRecord, startedAt, e.getErrorCode(), e.getMessage(), e);

        } catch (Exception e) {
            // 미분류 예외 -> 내부 오류. 외부에는 상세를 숨기고 Error ID 만 노출한다(설계서 6.4).
            return handleFailure(context, auditRecord, startedAt, ErrorCode.BS_SYS_500,
                    ErrorCode.BS_SYS_500.defaultMessage(), e);
        }
    }

    /**
     * 추적 ID 만 채운 최소 Context.
     *
     * <p>Context 확보 단계에서 검증 실패로 빠졌을 때, 오류 응답과 감사 로그가
     * requestId · traceId 없이 나가지 않도록 대체한다(설계서 12.3 추적 기준 키).</p>
     */
    private ServiceContext traceOnlyContext(HttpServletRequest httpRequest, String serviceId) {
        ServiceContext context = new ServiceContext();
        context.setRequestId(idGenerator.newRequestId());
        context.setServiceId(serviceId);
        if (httpRequest != null) {
            context.setClientRequestId(httpRequest.getHeader("X-Request-ID"));
            context.setTraceId(httpRequest.getHeader("X-Trace-ID"));
            context.setClientId(httpRequest.getHeader("X-Client-ID"));
        }
        if (context.getTraceId() == null) {
            context.setTraceId(idGenerator.newTraceId());
        }
        context.setRequestedAt(LocalDateTime.now());
        return context;
    }

    private ResponseEntity<StandardResponse<?>> handleFailure(ServiceContext context, AuditRecord auditRecord,
                                                              long startedAt, ErrorCode errorCode,
                                                              String message, Exception cause) {
        long elapsedMs = System.currentTimeMillis() - startedAt;
        String errorId = idGenerator.newErrorId();

        // Stack Trace 는 서버 로그에만 남긴다(설계서 6.4).
        System.out.println("[BSL-ERROR] errorId=" + errorId
                + ", requestId=" + (context == null ? "N/A" : context.getRequestId())
                + ", code=" + errorCode.code() + ", message=" + message);
        if (errorCode == ErrorCode.BS_SYS_500) {
            cause.printStackTrace(System.out);
        }

        // 감사 실패 로그 기록 (설계서 4.3 - 12단계)
        if (auditRecord == null && context != null) {
            auditRecord = auditLogger.start(context, null, null);
        }
        if (auditRecord != null) {
            auditLogger.fail(auditRecord, elapsedMs, errorCode.httpStatus(), errorCode.code(), errorId, message);
        }

        String externalMessage = (errorCode == ErrorCode.BS_SYS_500)
                ? errorCode.defaultMessage()   // 내부 상세 숨김
                : message;

        StandardResponse<Object> response = responseBuilder.error(context, errorCode, externalMessage, errorId,
                (cause instanceof BusinessServiceException) ? ((BusinessServiceException) cause).getDetails() : null,
                elapsedMs);
        return ResponseEntity.status(errorCode.httpStatus()).body((StandardResponse<?>) response);
    }

    /** 감사 필드 result_count (설계서 6.3). */
    private Integer resultCountOf(Object result) {
        if (result instanceof StatusServiceResponse) {
            return ((StatusServiceResponse) result).getResultCount();
        }
        return null;
    }
}
