package com.koreanre.ifrs17.businessservice.core.audit;

import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.metadata.ServiceMetadata;

import java.util.Map;

/**
 * 감사 로그 기록 컴포넌트 (설계서 4.2 - AuditLogger).
 *
 * <p>필수 인터페이스: {@code start, success, fail}</p>
 *
 * <p>설계서 4.3 표준 처리 순서의 (8) 감사 시작 로그, (12) 감사 성공/실패 로그 단계를 담당한다.</p>
 */
public interface AuditLogger {

    /** 감사 시작 로그 (설계서 4.3 - 8단계). */
    AuditRecord start(ServiceContext context, ServiceMetadata metadata, Map<String, Object> parameters);

    /** 감사 성공 로그 (설계서 4.3 - 12단계). */
    void success(AuditRecord record, long elapsedMs, Integer resultCount);

    /** 감사 실패 로그 (설계서 4.3 - 12단계). */
    void fail(AuditRecord record, long elapsedMs, int httpStatus, String errorCode, String errorId, String message);
}
