package com.koreanre.ifrs17.businessservice.core.context;

import com.koreanre.ifrs17.businessservice.api.dto.request.StandardRequest;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.LinkedHashSet;

/**
 * RequestContextResolver 의 Skeleton 구현체.
 *
 * <p><b>[Mock]</b> SSO 연동 전 단계이므로 사용자 정보는 HTTP Header 값 또는
 * 하드코딩된 가짜 사용자(E00000 / 정보기술팀 / IFRS17_USER)를 사용한다.
 * 실제 구현 시 Authorization 토큰 검증 및 SSO Context 조회로 대체한다.</p>
 */
@Component
public class HttpHeaderRequestContextResolver implements RequestContextResolver {

    /** 설계서 5.2 필수 Header. */
    public static final String HEADER_AUTHORIZATION = "Authorization";
    public static final String HEADER_REQUEST_ID = "X-Request-ID";
    public static final String HEADER_CLIENT_ID = "X-Client-ID";
    public static final String HEADER_USER_ID = "X-User-ID";
    public static final String HEADER_TRACE_ID = "X-Trace-ID";

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

        // (2) Request ID 생성 또는 검증 - Header 미입력 시 서버가 생성한다(설계서 5.2).
        String requestId = header(httpRequest, HEADER_REQUEST_ID);
        context.setRequestId(StringUtils.hasText(requestId) ? requestId : idGenerator.newRequestId());

        String traceId = header(httpRequest, HEADER_TRACE_ID);
        context.setTraceId(StringUtils.hasText(traceId) ? traceId : idGenerator.newTraceId());

        // (1) 호출 Client 식별정보
        context.setClientId(header(httpRequest, HEADER_CLIENT_ID));

        // (4) SSO 사용자 Context 추출 - [Mock] Header 우선, 없으면 가짜 사용자
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
