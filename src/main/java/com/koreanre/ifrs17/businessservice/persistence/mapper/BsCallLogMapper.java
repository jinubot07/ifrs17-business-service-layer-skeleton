package com.koreanre.ifrs17.businessservice.persistence.mapper;

import com.koreanre.ifrs17.businessservice.persistence.model.BsCallLog;

/**
 * BS_CALL_LOG 접근 계약 (설계서 6.3 감사로그 / 7.3 DDL).
 *
 * <p><b>[Skeleton — 미구현]</b> 현재 감사로그는 {@code ConsoleAuditLogger} 가 콘솔로 출력한다.
 * DB 연동 단계에서 본 Mapper 를 사용하는 {@code JdbcAuditLogger} 로 교체하며,
 * 설계서 7.4에 따라 저장 실패 시 보조 저장/재처리 정책을 함께 적용한다.</p>
 */
public interface BsCallLogMapper {

    /** 감사 시작 로그 INSERT (설계서 4.3 - 8단계). */
    int insertStart(BsCallLog callLog);

    /** 감사 성공 로그 UPDATE (별첨E v3.0 - 12단계 (1)). */
    int updateSuccess(BsCallLog callLog);

    /** 감사 실패 로그 UPDATE (별첨E v3.0 - 12단계 (1)). */
    int updateFail(BsCallLog callLog);

    /** Request ID 기준 호출 이력 조회 (설계서 5.1 GET /calls/{requestId}). */
    BsCallLog selectByRequestId(String requestId);
}
