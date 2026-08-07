package com.koreanre.ifrs17.businessservice.core.context;

import com.koreanre.ifrs17.businessservice.api.dto.request.StandardRequest;
import com.koreanre.ifrs17.businessservice.core.exception.ValidationException;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * RequestContextResolver 의 Skeleton 구현체.
 *
 * <p>기능정의서 v2.7 기준 담당 범위</p>
 * <ul>
 *   <li>2단계 (1) X-Request-ID 입력 여부 확인. 미입력이면 client_request_id 를 적재하지 않는다(NULL).</li>
 *   <li>2단계 (2) 입력값은 client_request_id 에 원본 그대로 보관하고, PK 인 request_id 는 항상 서버가 채번한다.</li>
 *   <li>3단계 (1) 필수 Header X-Client-ID 미입력 여부 확인 (BS-VAL-001 / HTTP 400).</li>
 * </ul>
 *
 * <p><b>[Mock]</b> SSO 연동 전 단계이므로 사용자 정보는 HTTP Header 값 또는
 * 하드코딩된 가짜 사용자(E00000 / IT001 / IFRS17_USER)를 사용한다.
 * 실제 구현 시 Authorization 토큰 검증 및 SSO Context 조회로 대체한다(4단계, 이번 구현 미대상).</p>
 */
@Component
public class HttpHeaderRequestContextResolver implements RequestContextResolver {

    /** 설계서 5.2 필수 Header. */
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_REQUEST_ID = "X-Request-ID";
    public static final String HEADER_CLIENT_ID = "X-Client-ID";
    public static final String HEADER_USER_ID = "X-User-ID";
    public static final String HEADER_TRACE_ID = "X-Trace-ID";

    /** BS_CALL_LOG.client_request_id 컬럼 길이 (테이블 설계서 varchar(80)). */
    private static final int CLIENT_REQUEST_ID_MAX_LENGTH = 80;

    /** [Mock] SSO 미연동 구간에서 사용할 가짜 사용자 정보. */
    private static final String MOCK_USER_ID = "E00000";
    private static final String MOCK_DEPARTMENT_CODE = "IT001";
    private static final String[] MOCK_ROLES = {"IFRS17_USER", "IFRS17_CLOSING_VIEW"};

    private final IdGenerator idGenerator;

    public HttpHeaderRequestContextResolver(IdGenerator idGenerator) {
        this.idGenerator = idGenerator;
    }

    @Override
    public ServiceContext resolve(HttpServletRequest httpRequest, String serviceId, StandardRequest request) {
        ServiceContext context = new ServiceContext();

        // ── 2단계 : Request ID 생성 또는 검증 ─────────────────────────────
        // (1)(2) request_id 는 호출자 입력 여부와 무관하게 항상 서버가 채번한다.
        //         호출자가 보낸 X-Request-ID 는 PK 로 쓰지 않고 client_request_id 에 원본 보관한다.
        context.setRequestId(idGenerator.newRequestId());
        context.setClientRequestId(truncate(header(httpRequest, HEADER_REQUEST_ID)));

        // trace_id 는 BS_CALL_LOG 의 PK 가 아니므로 호출자 입력값이 있으면 재채번하지 않고 그대로 사용한다.
        // 미입력 시에는 응답 에코와 분산추적을 위해 서버가 채번한다(설계서 5.2).
        String traceId = header(httpRequest, HEADER_TRACE_ID);
        context.setTraceId(StringUtils.hasText(traceId) ? traceId : idGenerator.newTraceId());

        // ── 3단계 (1) : 필수 Header X-Client-ID 미입력 여부 ───────────────
        String clientId = header(httpRequest, HEADER_CLIENT_ID);
        if (!StringUtils.hasText(clientId)) {
            throw ValidationException.of(HEADER_CLIENT_ID, "필수 Header 인 X-Client-ID 가 입력되지 않았습니다.");
        }
        context.setClientId(clientId);

        // ── 4단계 : SSO 사용자 Context 추출 - [Mock] 이번 구현 미대상 ──────
        String userId = header(httpRequest, HEADER_USER_ID);
        context.setUserId(StringUtils.hasText(userId) ? userId : MOCK_USER_ID);
        context.setDepartmentCode(MOCK_DEPARTMENT_CODE);
        context.setRoles(new LinkedHashSet<String>(Arrays.asList(MOCK_ROLES)));
        context.setAuthType(StringUtils.hasText(header(httpRequest, HEADER_AUTHORIZATION)) ? "BEARER" : "NONE");

        context.setServiceId(serviceId);
        context.setServiceVersion(request == null ? null : request.getServiceVersion());
        context.setRemoteIp(resolveRemoteIp(httpRequest));
        context.setRequestedAt(LocalDateTime.now());
        if (request != null && request.getOptions() != null && StringUtils.hasText(request.getOptions().getLocale())) {
            context.setLocale(request.getOptions().getLocale());
        }
        return context;
    }

    /**
     * client_request_id 는 호출자가 보내는 자유값이므로 컬럼 길이를 초과할 수 있다.
     * 정보성 컬럼이라 요청을 거부하지 않고 컬럼 길이에 맞춰 절단한다.
     */
    private String truncate(String clientRequestId) {
        if (!StringUtils.hasText(clientRequestId)) {
            return null;
        }
        return clientRequestId.length() <= CLIENT_REQUEST_ID_MAX_LENGTH
                ? clientRequestId
                : clientRequestId.substring(0, CLIENT_REQUEST_ID_MAX_LENGTH);
    }

    private String header(HttpServletRequest httpRequest, String name) {
        return httpRequest == null ? null : httpRequest.getHeader(name);
    }

    private String resolveRemoteIp(HttpServletRequest httpRequest) {
        if (httpRequest == null) {
            return null;
        }
        String forwarded = httpRequest.getHeader("X-Forwarded-For");
        if (StringUtils.hasText(forwarded)) {
            return forwarded.split(",")[0].trim();
        }
        return httpRequest.getRemoteAddr();
    }
}
