package com.koreanre.ifrs17.businessservice.core.audit;

import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.metadata.ServiceMetadata;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * AuditLogger 의 Skeleton 구현체.
 *
 * <p><b>[Mock]</b> DB(bs_call_log) 저장 대신 콘솔(System.out)로 감사 로그를 출력한다.
 * 실제 구현 시 business_service.bs_call_log INSERT / UPDATE 로 대체한다(설계서 7.3).</p>
 *
 * <p>설계서 6.4에 따라 Request 원문은 저장하지 않고 parameter_hash 만 기록한다.</p>
 */
@Component
public class ConsoleAuditLogger implements AuditLogger {

    private static final String SERVER_INSTANCE = "IFRS17-WAS-SKELETON-01";
    private static final String APPLICATION_VERSION = "1.0.0-SKELETON";

    @Override
    public AuditRecord start(ServiceContext context, ServiceMetadata metadata, Map<String, Object> parameters) {
        AuditRecord record = AuditRecord.from(context);
        record.setSourceSystem(metadata == null ? "IFRS17" : metadata.getSourceSystem());
        record.setServiceVersion(metadata == null ? context.getServiceVersion() : metadata.getVersion());
        record.setStatus("STARTED");
        record.setAuthorizationResult("ALLOW");
        record.setParameterHash(hash(parameters));
        record.setSensitiveAccessFlag(false);
        record.setServerInstance(SERVER_INSTANCE);
        record.setApplicationVersion(APPLICATION_VERSION);

        System.out.println("[BSL-AUDIT-START] requestId=" + record.getRequestId()
                + ", traceId=" + record.getTraceId()
                + ", serviceId=" + record.getServiceId()
                + ", version=" + record.getServiceVersion()
                + ", clientId=" + record.getClientId()
                + ", userId=" + record.getUserId()
                + ", deptCode=" + record.getDepartmentCode()
                + ", roles=" + record.getRoles()
                + ", remoteIp=" + record.getRemoteIp()
                + ", parameterHash=" + record.getParameterHash()
                + ", requestedAt=" + record.getRequestedAt());
        return record;
    }

    @Override
    public void success(AuditRecord record, long elapsedMs, Integer resultCount) {
        record.setStatus("SUCCESS");
        record.setHttpStatus(200);
        record.setElapsedMs(elapsedMs);
        record.setResultCount(resultCount);
        record.setCompletedAt(LocalDateTime.now());

        System.out.println("[BSL-AUDIT-SUCCESS] requestId=" + record.getRequestId()
                + ", serviceId=" + record.getServiceId()
                + ", status=SUCCESS"
                + ", httpStatus=200"
                + ", resultCount=" + resultCount
                + ", elapsedMs=" + elapsedMs
                + ", completedAt=" + record.getCompletedAt());
    }

    @Override
    public void fail(AuditRecord record, long elapsedMs, int httpStatus, String errorCode, String errorId,
                     String message) {
        record.setStatus("ERROR");
        record.setHttpStatus(httpStatus);
        record.setErrorCode(errorCode);
        record.setErrorId(errorId);
        record.setElapsedMs(elapsedMs);
        record.setCompletedAt(LocalDateTime.now());

        System.out.println("[BSL-AUDIT-FAIL] requestId=" + record.getRequestId()
                + ", serviceId=" + record.getServiceId()
                + ", status=ERROR"
                + ", httpStatus=" + httpStatus
                + ", errorCode=" + errorCode
                + ", errorId=" + errorId
                + ", message=" + message
                + ", elapsedMs=" + elapsedMs
                + ", completedAt=" + record.getCompletedAt());
    }

    /** 설계서 6.4 - Request 원문 저장 금지. 파라미터는 Hash 로만 기록한다. */
    private String hash(Map<String, Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return "EMPTY";
        }
        return String.format("%08X", parameters.toString().hashCode());
    }
}
