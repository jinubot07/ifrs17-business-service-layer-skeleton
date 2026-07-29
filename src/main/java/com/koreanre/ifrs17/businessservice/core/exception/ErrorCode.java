package com.koreanre.ifrs17.businessservice.core.exception;

/**
 * 표준 오류코드 (설계서 부록 A. 표준 오류코드 / 4.5 예외 처리 기준).
 */
public enum ErrorCode {

    /** 필수값 누락 또는 형식 오류. */
    BS_VAL_001("BS-VAL-001", 400, "요청 값이 올바르지 않습니다."),
    /** 허용범위 초과. */
    BS_VAL_002("BS-VAL-002", 400, "요청 값이 허용범위를 초과했습니다."),
    /** 인증 실패. */
    BS_AUTH_001("BS-AUTH-001", 401, "인증에 실패했습니다."),
    /** Token 만료. */
    BS_AUTH_002("BS-AUTH-002", 401, "인증 토큰이 만료되었습니다."),
    /** 서비스 권한 없음. */
    BS_AUTH_003("BS-AUTH-003", 403, "해당 서비스에 대한 권한이 없습니다."),
    /** 서비스/버전 없음 또는 비활성. */
    BS_SVC_404("BS-SVC-404", 404, "요청한 서비스 또는 버전을 찾을 수 없습니다."),
    /** 정상이나 결과 없음 (HTTP 200 + 빈 결과). */
    BS_DATA_000("BS-DATA-000", 200, "조회 결과가 없습니다."),
    /** Legacy Service 처리 오류. */
    BS_LEG_500("BS-LEG-500", 500, "기간계 서비스 처리 중 오류가 발생했습니다."),
    /** 내부 시스템 오류. */
    BS_SYS_500("BS-SYS-500", 500, "내부 시스템 오류가 발생했습니다."),
    /** 일시적 사용불가. */
    BS_SYS_503("BS-SYS-503", 503, "일시적으로 서비스를 사용할 수 없습니다."),
    /** Timeout. */
    BS_SYS_504("BS-SYS-504", 504, "처리 시간이 초과되었습니다.");

    private final String code;
    private final int httpStatus;
    private final String defaultMessage;

    ErrorCode(String code, int httpStatus, String defaultMessage) {
        this.code = code;
        this.httpStatus = httpStatus;
        this.defaultMessage = defaultMessage;
    }

    public String code() {
        return code;
    }

    public int httpStatus() {
        return httpStatus;
    }

    public String defaultMessage() {
        return defaultMessage;
    }
}
