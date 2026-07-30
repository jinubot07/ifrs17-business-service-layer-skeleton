package com.koreanre.ifrs17.businessservice.domain.closing;

import com.koreanre.ifrs17.businessservice.domain.common.ProcessingStatusCode;

/**
 * 결산 단계별 상태 (설계서 9.1 출력 - 단계별 상태, 시작/종료시간, 진행률, 오류건수).
 */
public class ClosingStageStatus {

    private String stageCode;
    private String stageName;
    private ProcessingStatusCode status;
    private String startedAt;
    private String endedAt;
    private double progressRate;
    private int errorCount;

    public ClosingStageStatus() {
    }

    public ClosingStageStatus(String stageCode, String stageName, ProcessingStatusCode status,
                              String startedAt, String endedAt, double progressRate, int errorCount) {
        this.stageCode = stageCode;
        this.stageName = stageName;
        this.status = status;
        this.startedAt = startedAt;
        this.endedAt = endedAt;
        this.progressRate = progressRate;
        this.errorCount = errorCount;
    }

    public String getStageCode() {
        return stageCode;
    }

    public void setStageCode(String stageCode) {
        this.stageCode = stageCode;
    }

    public String getStageName() {
        return stageName;
    }

    public void setStageName(String stageName) {
        this.stageName = stageName;
    }

    public ProcessingStatusCode getStatus() {
        return status;
    }

    public void setStatus(ProcessingStatusCode status) {
        this.status = status;
    }

    public String getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(String startedAt) {
        this.startedAt = startedAt;
    }

    public String getEndedAt() {
        return endedAt;
    }

    public void setEndedAt(String endedAt) {
        this.endedAt = endedAt;
    }

    public double getProgressRate() {
        return progressRate;
    }

    public void setProgressRate(double progressRate) {
        this.progressRate = progressRate;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
}
