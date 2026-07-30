package com.koreanre.ifrs17.businessservice.api.dto.response;

/**
 * 파일럿 5종 상태조회 서비스의 공통 응답 항목 (설계서 9장).
 */
public abstract class StatusServiceResponse {

    /** 기준년월 (YYYY-MM). */
    private String closingYearMonth;

    /** 전체 상태 (부록 D 표준 상태코드). */
    private ProcessingStatusCode overallStatus = ProcessingStatusCode.UNKNOWN;

    /** 결과 건수 (설계서 6.3 감사 필드 result_count). */
    private int resultCount;

    /** 오류 건수. */
    private int errorCount;

    /** 조회 기준시각 (문자열 ISO-8601). */
    private String baseDateTime;

    /** [Draft] Legacy 연계 정보(미확정 임시값). */
    private LegacyBinding legacyBinding;

    public String getClosingYearMonth() {
        return closingYearMonth;
    }

    public void setClosingYearMonth(String closingYearMonth) {
        this.closingYearMonth = closingYearMonth;
    }

    public ProcessingStatusCode getOverallStatus() {
        return overallStatus;
    }

    public void setOverallStatus(ProcessingStatusCode overallStatus) {
        this.overallStatus = overallStatus;
    }

    public int getResultCount() {
        return resultCount;
    }

    public void setResultCount(int resultCount) {
        this.resultCount = resultCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public String getBaseDateTime() {
        return baseDateTime;
    }

    public void setBaseDateTime(String baseDateTime) {
        this.baseDateTime = baseDateTime;
    }

    public LegacyBinding getLegacyBinding() {
        return legacyBinding;
    }

    public void setLegacyBinding(LegacyBinding legacyBinding) {
        this.legacyBinding = legacyBinding;
    }
}
