package com.koreanre.ifrs17.businessservice.domain.csm;

import com.koreanre.ifrs17.businessservice.domain.common.StatusServiceRequest;

/**
 * IFRS17.CSM.STATUS 요청 DTO (설계서 9.5).
 *
 * <p>입력: closingYearMonth(필수, YYYY-MM), portfolioCode(optional)</p>
 */
public class CsmStatusRequest extends StatusServiceRequest {

    /** 포트폴리오 코드 (optional). 예: PF-LIFE-001 */
    private String portfolioCode;

    public String getPortfolioCode() {
        return portfolioCode;
    }

    public void setPortfolioCode(String portfolioCode) {
        this.portfolioCode = portfolioCode;
    }
}
