package com.koreanre.ifrs17.businessservice.domain.statement;

import com.koreanre.ifrs17.businessservice.api.dto.request.StatusServiceRequest;

/**
 * IFRS17.STATEMENT.STATUS 요청 DTO (설계서 9.4).
 *
 * <p>입력: closingYearMonth(필수, YYYY-MM), statementType(optional)</p>
 */
public class StatementStatusRequest extends StatusServiceRequest {

    /** 재무제표 유형 (optional). 예: BS, IS, DISCLOSURE */
    private String statementType;

    public String getStatementType() {
        return statementType;
    }

    public void setStatementType(String statementType) {
        this.statementType = statementType;
    }
}
