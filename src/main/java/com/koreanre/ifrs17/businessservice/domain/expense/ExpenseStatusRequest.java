package com.koreanre.ifrs17.businessservice.domain.expense;

import com.koreanre.ifrs17.businessservice.domain.common.StatusServiceRequest;

/**
 * IFRS17.EXPENSE.STATUS 요청 DTO (설계서 9.3).
 *
 * <p>입력: closingYearMonth(필수, YYYY-MM), expenseCategory(optional)</p>
 */
public class ExpenseStatusRequest extends StatusServiceRequest {

    /** 사업비 구분 (optional). 예: ACQUISITION, MAINTENANCE, ADMIN */
    private String expenseCategory;

    public String getExpenseCategory() {
        return expenseCategory;
    }

    public void setExpenseCategory(String expenseCategory) {
        this.expenseCategory = expenseCategory;
    }
}
