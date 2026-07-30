package com.koreanre.ifrs17.businessservice.domain.expense;

import com.koreanre.ifrs17.businessservice.api.dto.response.StatusServiceResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * IFRS17.EXPENSE.STATUS 응답 DTO (설계서 9.3).
 */
public class ExpenseStatusResponse extends StatusServiceResponse {

    private List<ExpenseCategoryStatus> expenses = new ArrayList<ExpenseCategoryStatus>();

    public List<ExpenseCategoryStatus> getExpenses() {
        return expenses;
    }

    public void setExpenses(List<ExpenseCategoryStatus> expenses) {
        this.expenses = (expenses == null) ? new ArrayList<ExpenseCategoryStatus>() : expenses;
    }
}
