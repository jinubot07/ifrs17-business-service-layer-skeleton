package com.koreanre.ifrs17.businessservice.domain.statement;

import com.koreanre.ifrs17.businessservice.domain.common.ProcessingStatusCode;

/**
 * 보고서별 산출 상태 (설계서 9.4 출력 - 산출 상태, 버전, 최종생성시각).
 */
public class StatementTypeStatus {

    private String statementType;
    private String statementName;
    private ProcessingStatusCode status;
    private String reportVersion;
    private String generatedAt;
    private int errorCount;

    public StatementTypeStatus() {
    }

    public StatementTypeStatus(String statementType, String statementName, ProcessingStatusCode status,
                               String reportVersion, String generatedAt, int errorCount) {
        this.statementType = statementType;
        this.statementName = statementName;
        this.status = status;
        this.reportVersion = reportVersion;
        this.generatedAt = generatedAt;
        this.errorCount = errorCount;
    }

    public String getStatementType() {
        return statementType;
    }

    public void setStatementType(String statementType) {
        this.statementType = statementType;
    }

    public String getStatementName() {
        return statementName;
    }

    public void setStatementName(String statementName) {
        this.statementName = statementName;
    }

    public ProcessingStatusCode getStatus() {
        return status;
    }

    public void setStatus(ProcessingStatusCode status) {
        this.status = status;
    }

    public String getReportVersion() {
        return reportVersion;
    }

    public void setReportVersion(String reportVersion) {
        this.reportVersion = reportVersion;
    }

    public String getGeneratedAt() {
        return generatedAt;
    }

    public void setGeneratedAt(String generatedAt) {
        this.generatedAt = generatedAt;
    }

    public int getErrorCount() {
        return errorCount;
    }

    public void setErrorCount(int errorCount) {
        this.errorCount = errorCount;
    }
}
