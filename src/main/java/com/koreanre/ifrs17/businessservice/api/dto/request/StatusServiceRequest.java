package com.koreanre.ifrs17.businessservice.api.dto.request;

/**
 * 파일럿 5종 상태조회 서비스의 공통 요청 항목 (설계서 9장).
 *
 * <p>모든 상태조회 서비스는 기준년월(closingYearMonth, YYYY-MM)을 필수로 요구한다.</p>
 */
public abstract class StatusServiceRequest {

    /** 기준년월 (YYYY-MM, 필수). */
    private String closingYearMonth;

    public String getClosingYearMonth() {
        return closingYearMonth;
    }

    public void setClosingYearMonth(String closingYearMonth) {
        this.closingYearMonth = closingYearMonth;
    }
}
