package com.koreanre.ifrs17.businessservice.domain.closing;

import com.koreanre.ifrs17.businessservice.domain.common.StatusServiceResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * IFRS17.CLOSING.STATUS 응답 DTO (설계서 9.1).
 */
public class ClosingStatusResponse extends StatusServiceResponse {

    /** 결산 유형. */
    private String closingType;

    /** 전체 진행률(%). */
    private double progressRate;

    /** 단계별 상태 목록. */
    private List<ClosingStageStatus> stages = new ArrayList<ClosingStageStatus>();

    public String getClosingType() {
        return closingType;
    }

    public void setClosingType(String closingType) {
        this.closingType = closingType;
    }

    public double getProgressRate() {
        return progressRate;
    }

    public void setProgressRate(double progressRate) {
        this.progressRate = progressRate;
    }

    public List<ClosingStageStatus> getStages() {
        return stages;
    }

    public void setStages(List<ClosingStageStatus> stages) {
        this.stages = (stages == null) ? new ArrayList<ClosingStageStatus>() : stages;
    }
}
