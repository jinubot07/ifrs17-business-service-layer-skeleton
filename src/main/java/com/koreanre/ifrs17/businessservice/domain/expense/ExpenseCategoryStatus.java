package com.koreanre.ifrs17.businessservice.domain.expense;

import com.koreanre.ifrs17.businessservice.domain.common.ProcessingStatusCode;

/**
 * 사업비 구분별 적재/검증/배부 상태 (설계서 9.3 출력).
 */
public class ExpenseCategoryStatus {

    private String expenseCategory;
    private String categoryName;
    private ProcessingStatusCode loadStatus;
    private ProcessingStatusCode validationStatus;
    private ProcessingStatusCode allocationStatus;
    private int errorCount;
    private String lastProcessedAt;

    public ExpenseCategoryStatus() {
    }

    public ExpenseCategoryStatus(String expenseCategory, String categoryName, ProcessingStatusCode loadStatus,
                                 ProcessingStatusCode validationStatus, ProcessingStatusCode allocationStatus,
                                 int errorCount, String lastProcessedAt) {
        this.expenseCategory = expenseCategory;
        this.categoryName = categoryName;
        this.loadStatus = loadStatus;
        this.validationStatus = validationStatus;
        this.allocationStatus = allocationStatus;
        this.errorCount = errorCount;
        this.lastProcessedAt = lastProcessedAt;
    }

    public String getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(String expenseCategory) {
        this.expenseCategory = expenseCategory;
    }

    public String getCategoryName() {
        return categoryName;
    }

    public void setCategoryName(String categoryName) {
        this.categoryName = categoryName;
    }

    public ProcessingStatusCode getLoadStatus() {
        return loadStatus;
    }

    public void setLoadStatus(ProcessingStatusCode loadStatus) {
        this.loadStatus = loadStatus;
    }

    public ProcessingStatusCode getValidationStatus() {
        return validationStatus;
    }

    public void setValidationStatus(ProcessingStatusCode validationStatus) {
        this.validationStatus = validationStatus;
    }

    public ProcessingStatusCode getAllocationStatus() {
        return allocationStatus;
    }

    public void setAllocationStatus(ProcessingStatusCode allocationStatus) {
        this.allocationStatus = allocationStatus;
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
