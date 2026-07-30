package com.koreanre.ifrs17.businessservice.domain.common;

/**
 * 표준 상태코드 (설계서 부록 D. 표준 상태코드 / 9.1~9.5).
 */
public enum ProcessingStatusCode {

    /** 아직 실행되지 않음. */
    NOT_STARTED,
    /** 처리 중. */
    RUNNING,
    /** 정상 완료. */
    COMPLETED,
    /** 일부 완료 / 일부 오류. */
    PARTIAL,
    /** 실패. */
    FAILED,
    /** 상태 판정 불가. */
    UNKNOWN
}
