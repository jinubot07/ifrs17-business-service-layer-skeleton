package com.koreanre.ifrs17.businessservice.domain.journal;

import com.koreanre.ifrs17.businessservice.domain.common.StatusServiceRequest;

/**
 * IFRS17.JOURNAL.STATUS 요청 DTO (설계서 9.2).
 *
 * <p>입력: closingYearMonth(필수, YYYY-MM), journalType(optional)</p>
 */
public class JournalStatusRequest extends StatusServiceRequest {

    /** 전표 유형 (optional). 예: INSURANCE_REVENUE, INSURANCE_EXPENSE, CSM_AMORTIZATION */
    private String journalType;

    public String getJournalType() {
        return journalType;
    }

    public void setJournalType(String journalType) {
        this.journalType = journalType;
    }
}
