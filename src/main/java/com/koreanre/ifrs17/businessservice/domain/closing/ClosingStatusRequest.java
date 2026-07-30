package com.koreanre.ifrs17.businessservice.domain.closing;

import com.koreanre.ifrs17.businessservice.api.dto.request.StatusServiceRequest;

/**
 * IFRS17.CLOSING.STATUS 요청 DTO (설계서 9.1).
 *
 * <p>입력: closingYearMonth(필수, YYYY-MM), closingType(optional)</p>
 */
public class ClosingStatusRequest extends StatusServiceRequest {

    /** 결산 유형 (optional). 예: MONTHLY, QUARTERLY, ANNUAL */
    private String closingType;

    public String getClosingType() {
        return closingType;
    }

    public void setClosingType(String closingType) {
        this.closingType = closingType;
    }
}
