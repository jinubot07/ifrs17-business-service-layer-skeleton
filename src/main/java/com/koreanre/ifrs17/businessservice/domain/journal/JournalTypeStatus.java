package com.koreanre.ifrs17.businessservice.domain.journal;

import com.koreanre.ifrs17.businessservice.domain.common.ProcessingStatusCode;

/**
 * 전표 유형별 상태 (설계서 9.2 출력 - 생성건수, 반영건수, 오류건수, 최종처리시간).
 */
public class JournalTypeStatus {

    private String journalType;
    private String journalTypeName;
    private ProcessingStatusCode status;
    private long createdCount;
    private long postedCount;
    private int errorCount;
    private String lastProcessedAt;

    public JournalTypeStatus() {
    }

    public JournalTypeStatus(String journalType, String journalTypeName, ProcessingStatusCode status,
                             long createdCount, long postedCount, int errorCount, String lastProcessedAt) {
        this.journalType = journalType;
        this.journalTypeName = journalTypeName;
        this.status = status;
        this.createdCount = createdCount;
        this.postedCount = postedCount;
        this.errorCount = errorCount;
        this.lastProcessedAt = lastProcessedAt;
    }

    public String getJournalType() {
        return journalType;
    }

    public void setJournalType(String journalType) {
        this.journalType = journalType;
    }

    public String getJournalTypeName() {
        return journalTypeName;
    }

    public void setJournalTypeName(String journalTypeName) {
        this.journalTypeName = journalTypeName;
    }

    public ProcessingStatusCode getStatus() {
        return status;
    }

    public void setStatus(ProcessingStatusCode status) {
        this.status = status;
    }

    public long getCreatedCount() {
        return createdCount;
    }

    public void setCreatedCount(long createdCount) {
        this.createdCount = createdCount;
    }

    public long getPostedCount() {
        return postedCount;
    }

    public void setPostedCount(long postedCount) {
        this.postedCount = postedCount;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }

    public String getLastProcessedAt() {
        return lastProcessedAt;
    }

    public void setLastProcessedAt(String lastProcessedAt) {
        this.lastProcessedAt = lastProcessedAt;
    }
}
