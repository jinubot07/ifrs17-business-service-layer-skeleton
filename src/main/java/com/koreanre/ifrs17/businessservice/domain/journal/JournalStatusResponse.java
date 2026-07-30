package com.koreanre.ifrs17.businessservice.domain.journal;

import com.koreanre.ifrs17.businessservice.api.dto.response.StatusServiceResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * IFRS17.JOURNAL.STATUS 응답 DTO (설계서 9.2).
 */
public class JournalStatusResponse extends StatusServiceResponse {

    private long totalCreatedCount;
    private long totalPostedCount;
    private String lastProcessedAt;
    private List<JournalTypeStatus> journals = new ArrayList<JournalTypeStatus>();

    public long getTotalCreatedCount() {
        return totalCreatedCount;
    }

    public void setTotalCreatedCount(long totalCreatedCount) {
        this.totalCreatedCount = totalCreatedCount;
    }

    public long getTotalPostedCount() {
        return totalPostedCount;
    }

    public void setTotalPostedCount(long totalPostedCount) {
        this.totalPostedCount = totalPostedCount;
    }

    public String getLastProcessedAt() {
        return lastProcessedAt;
    }

    public void setLastProcessedAt(String lastProcessedAt) {
        this.lastProcessedAt = lastProcessedAt;
    }

    public List<JournalTypeStatus> getJournals() {
        return journals;
    }

    public void setJournals(List<JournalTypeStatus> journals) {
        this.journals = (journals == null) ? new ArrayList<JournalTypeStatus>() : journals;
    }
}
