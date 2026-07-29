package com.koreanre.ifrs17.businessservice.domain.csm;

import com.koreanre.ifrs17.businessservice.domain.common.ProcessingStatusCode;

/**
 * 포트폴리오별 CSM 산출 상태 (설계서 9.5 출력 - 산출 상태, 건수, 오류, 기준시각).
 */
public class CsmPortfolioStatus {

    private String portfolioCode;
    private String portfolioName;
    private ProcessingStatusCode status;
    private long contractCount;
    private int errorCount;
    private String baseDateTime;

    public CsmPortfolioStatus() {
    }

    public CsmPortfolioStatus(String portfolioCode, String portfolioName, ProcessingStatusCode status,
                              long contractCount, int errorCount, String baseDateTime) {
        this.portfolioCode = portfolioCode;
        this.portfolioName = portfolioName;
        this.status = status;
        this.contractCount = contractCount;
        this.errorCount = errorCount;
        this.baseDateTime = baseDateTime;
    }

    public String getPortfolioCode() {
        return portfolioCode;
    }

    public void setPortfolioCode(String portfolioCode) {
        this.portfolioCode = portfolioCode;
    }

    public String getPortfolioName() {
        return portfolioName;
    }

    public void setPortfolioName(String portfolioName) {
        this.portfolioName = portfolioName;
    }

    public ProcessingStatusCode getStatus() {
        return status;
    }

    public void setStatus(ProcessingStatusCode status) {
        this.status = status;
    }

    public long getContractCount() {
        return contractCount;
    }

    public void setContractCount(long contractCount) {
        this.contractCount = contractCount;
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
}
