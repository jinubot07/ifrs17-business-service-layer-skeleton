package com.koreanre.ifrs17.businessservice.core.audit;

import com.koreanre.ifrs17.businessservice.api.dto.response.ProcessingStatusCode;
import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.metadata.ServiceMetadata;
import com.koreanre.ifrs17.businessservice.persistence.mapper.BsCallLogMapper;
import com.koreanre.ifrs17.businessservice.persistence.model.BsCallLog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.util.Map;

/**
 * AuditLogger 의 Skeleton 구현체.
 *
 * <p>기능정의서 v2.7 - 8단계 감사 시작 로그 기록 / 12단계 감사 성공·실패 로그 기록.</p>
 * <ul>
 *   <li>8단계 : 2·3·5·7단계에서 확정된 값으로 적재 항목을 조립하고(parameters 는 parameter_hash 로 변환),
 *       PK 인 request_id 를 유일 기준으로 BS_CALL_LOG 에 INSERT 한다. status_code 는 RUNNING.</li>
 *   <li>12단계 : completed_at · elapsed_ms · http_status · error_code · error_id · result_count 를
 *       UPDATE 하고 status_code 를 COMPLETED / FAILED 로 갱신한다.</li>
 * </ul>
 *
 * <p>감사 저장 실패는 업무 응답을 실패시키지 않는다(설계서 7.4). 저장에 실패하면
 * 보조 저장(재처리 대기)으로 전환하며 오류코드를 반환하지 않는다.</p>
 *
 * <p><b>[Skeleton]</b> BsCallLogMapper 구현체(Bean)가 아직 없으므로 주입되지 않으면
 * 콘솔 출력이 보조 저장을 대신한다. DB 연동 시 JdbcAuditLogger 로 교체한다(설계서 7.3).</p>
 */
@Component
public class ConsoleAuditLogger implements AuditLogger {

    /** [Mock] 실제 WAS 인스턴스 식별자로 교체 대상. */
    private static final String SERVER_INSTANCE = "IFRS17-WAS-SKELETON-01";
    private static final String APPLICATION_VERSION = "1.1.0-SKELETON";

    /** DB 연동 전에는 Bean 이 없으므로 선택 주입한다. */
    @Autowired(required = false)
    private BsCallLogMapper bsCallLogMapper;

    // ────────────────────────────── 8단계 ──────────────────────────────

    @Override
    public AuditRecord start(ServiceContext context, ServiceMetadata metadata, Map<String, Object> parameters) {
        AuditRecord record = AuditRecord.from(context);
        record.setSourceSystem(metadata == null ? "IFRS17" : metadata.getSourceSystem());
        record.setServiceVersion(metadata == null ? context.getServiceVersion() : metadata.getVersion());
        record.setStatus(ProcessingStatusCode.RUNNING.name());
        record.setAuthorizationResult("ALLOW");
        record.setParameterHash(hash(parameters));
        record.setSensitiveAccessFlag(false);
        record.setServerInstance(SERVER_INSTANCE);
        record.setApplicationVersion(APPLICATION_VERSION);

        System.out.println("[BSL-AUDIT-START] requestId=" + record.getRequestId()
                + ", clientRequestId=" + record.getClientRequestId()
                + ", traceId=" + record.getTraceId()
                + ", serviceId=" + record.getServiceId()
                + ", version=" + record.getServiceVersion()
                + ", clientId=" + record.getClientId()
                + ", userId=" + record.getUserId()
                + ", deptCode=" + record.getDepartmentCode()
                + ", status=" + record.getStatus()
                + ", remoteIp=" + record.getRemoteIp()
                + ", parameterHash=" + record.getParameterHash()
                + ", requestedAt=" + record.getRequestedAt());

        insertStart(record);
        return record;
    }

    /** BS_CALL_LOG INSERT. 실패해도 업무 응답을 실패시키지 않는다(설계서 7.4). */
    private void insertStart(AuditRecord record) {
        BsCallLog callLog = toCallLog(record);
        try {
            if (bsCallLogMapper == null) {
                throw new IllegalStateException("BsCallLogMapper 구현체가 없습니다(Skeleton).");
            }
            bsCallLogMapper.insertStart(callLog);
        } catch (Exception e) {
            fallback("INSERT", record.getRequestId(), e);
        }
    }

    // ───────────────────────────── 12단계 ──────────────────────────────

    @Override
    public void success(AuditRecord record, long elapsedMs, Integer resultCount) {
        record.setStatus(ProcessingStatusCode.COMPLETED.name());
        record.setHttpStatus(200);
        record.setElapsedMs(elapsedMs);
        record.setResultCount(resultCount);
        record.setCompletedAt(LocalDateTime.now());

        System.out.println("[BSL-AUDIT-SUCCESS] requestId=" + record.getRequestId()
                + ", serviceId=" + record.getServiceId()
                + ", status=" + record.getStatus()
                + ", httpStatus=200"
                + ", resultCount=" + resultCount
                + ", elapsedMs=" + elapsedMs
                + ", completedAt=" + record.getCompletedAt());

        updateResult(record);
    }

    @Override
    public void fail(AuditRecord record, long elapsedMs, int httpStatus, String errorCode, String errorId,
                     String message) {
        record.setStatus(ProcessingStatusCode.FAILED.name());
        record.setHttpStatus(httpStatus);
        record.setErrorCode(errorCode);
        record.setErrorId(errorId);
        record.setElapsedMs(elapsedMs);
        record.setCompletedAt(LocalDateTime.now());

        System.out.println("[BSL-AUDIT-FAIL] requestId=" + record.getRequestId()
                + ", serviceId=" + record.getServiceId()
                + ", status=" + record.getStatus()
                + ", httpStatus=" + httpStatus
                + ", errorCode=" + errorCode
                + ", errorId=" + errorId
                + ", message=" + message
                + ", elapsedMs=" + elapsedMs
                + ", completedAt=" + record.getCompletedAt());

        updateResult(record);
    }

    /** BS_CALL_LOG UPDATE. INSERT 와 동일하게 실패해도 업무 응답에 영향을 주지 않는다. */
    private void updateResult(AuditRecord record) {
        BsCallLog callLog = toCallLog(record);
        try {
            if (bsCallLogMapper == null) {
                throw new IllegalStateException("BsCallLogMapper 구현체가 없습니다(Skeleton).");
            }
            bsCallLogMapper.updateResult(callLog);
        } catch (Exception e) {
            fallback("UPDATE", record.getRequestId(), e);
        }
    }

    // ────────────────────────────── 공통 ───────────────────────────────

    /** AuditRecord → BS_CALL_LOG 적재 항목. */
    private BsCallLog toCallLog(AuditRecord record) {
        BsCallLog callLog = new BsCallLog();
        callLog.setRequestId(record.getRequestId());
        callLog.setClientRequestId(record.getClientRequestId());
        callLog.setTraceId(record.getTraceId());
        callLog.setServiceId(record.getServiceId());
        callLog.setServiceVersion(record.getServiceVersion());
        callLog.setClientId(record.getClientId());
        callLog.setUserId(record.getUserId());
        callLog.setDepartmentCode(record.getDepartmentCode());
        callLog.setRequestedAt(record.getRequestedAt());
        callLog.setCompletedAt(record.getCompletedAt());
        callLog.setElapsedMs(record.getElapsedMs() == null ? null : record.getElapsedMs().intValue());
        callLog.setStatusCode(record.getStatus());
        callLog.setHttpStatus(record.getHttpStatus());
        callLog.setErrorCode(record.getErrorCode());
        callLog.setErrorId(record.getErrorId());
        callLog.setParameterHash(record.getParameterHash());
        callLog.setResultCount(record.getResultCount());
        callLog.setRemoteIp(record.getRemoteIp());
        callLog.setServerInstance(record.getServerInstance());
        return callLog;
    }

    /**
     * 보조 저장(재처리 대기) 전환 - 설계서 7.4.
     *
     * <p><b>[Skeleton]</b> 콘솔 출력으로 대신한다. 실구현 시 로컬 파일·큐 등
     * 재처리 가능한 보조 저장소로 적재한다.</p>
     */
    private void fallback(String operation, String requestId, Exception cause) {
        System.out.println("[BSL-AUDIT-FALLBACK] operation=" + operation
                + ", requestId=" + requestId
                + ", reason=" + cause.getMessage()
                + ", action=보조 저장(재처리 대기). 업무 응답은 계속 진행한다.");
    }

    /**
     * 설계서 6.4 - Request 원문 저장 금지. 파라미터는 Hash 로만 기록한다.
     * BS_CALL_LOG.parameter_hash 는 varchar(128) 이며 SHA-256 hex(64자)를 사용한다.
     */
    private String hash(Map<String, Object> parameters) {
        if (parameters == null || parameters.isEmpty()) {
            return "EMPTY";
        }
        try {
            byte[] digest = MessageDigest.getInstance("SHA-256")
                    .digest(parameters.toString().getBytes(Charset.forName("UTF-8")));
            StringBuilder hex = new StringBuilder(digest.length * 2);
            for (byte b : digest) {
                hex.append(String.format("%02x", b));
            }
            return hex.toString();
        } catch (Exception e) {
            return "HASH-UNAVAILABLE";
        }
    }
}
