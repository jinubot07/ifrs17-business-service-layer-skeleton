package com.koreanre.ifrs17.businessservice.legacy.adapter;

import com.koreanre.ifrs17.businessservice.api.dto.response.LegacyBinding;
import com.koreanre.ifrs17.businessservice.api.dto.response.ProcessingStatusCode;
import com.koreanre.ifrs17.businessservice.domain.expense.ExpenseCategoryStatus;
import com.koreanre.ifrs17.businessservice.domain.expense.ExpenseStatusRequest;
import com.koreanre.ifrs17.businessservice.domain.expense.ExpenseStatusResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * IFRS17.EXPENSE.STATUS 용 Legacy Adapter 의 Skeleton 구현체.
 *
 * <p><b>[Mock]</b> 기존 사업비 처리 Service 호출 대신 하드코딩 결과를 반환한다.</p>
 * <p><b>[Draft]</b> 배치 프로그램 ID·화면명 미확정(임시값).</p>
 */
@Component
public class DummyExpenseStatusLegacyAdapter implements LegacyAdapter<ExpenseStatusRequest, ExpenseStatusResponse> {

    private static final String BATCH_PROGRAM_ID = "TBD-BAT-EXPENSE-STATUS-001";
    private static final String SCREEN_NAME = "TBD-사업비처리현황조회";

    @Override
    public String legacyBatchProgramId() {
        return BATCH_PROGRAM_ID;
    }

    @Override
    public String legacyScreenName() {
        return SCREEN_NAME;
    }

    @Override
    public ExpenseStatusResponse invoke(ExpenseStatusRequest request) {
        System.out.println("[BSL-LEGACY] adapter=DummyExpenseStatusLegacyAdapter"
                + ", batchProgramId=" + BATCH_PROGRAM_ID
                + ", closingYearMonth=" + request.getClosingYearMonth() + " (Mock 호출)");

        ExpenseStatusResponse response = new ExpenseStatusResponse();
        response.setClosingYearMonth(request.getClosingYearMonth());
        response.setBaseDateTime("2026-07-01T21:00:00+09:00");
        response.setLegacyBinding(new LegacyBinding(BATCH_PROGRAM_ID, SCREEN_NAME));

        if (!MockDataPolicy.hasData(request.getClosingYearMonth())) {
            response.setExpenses(new ArrayList<ExpenseCategoryStatus>());
            response.setOverallStatus(ProcessingStatusCode.NOT_STARTED);
            response.setResultCount(0);
            return response;
        }

        List<ExpenseCategoryStatus> all = Arrays.asList(
                new ExpenseCategoryStatus("ACQUISITION", "신계약비", ProcessingStatusCode.COMPLETED,
                        ProcessingStatusCode.COMPLETED, ProcessingStatusCode.COMPLETED, 0,
                        "2026-07-01T19:41:22+09:00"),
                new ExpenseCategoryStatus("MAINTENANCE", "유지비", ProcessingStatusCode.COMPLETED,
                        ProcessingStatusCode.COMPLETED, ProcessingStatusCode.RUNNING, 0,
                        "2026-07-01T20:33:10+09:00"),
                new ExpenseCategoryStatus("ADMIN", "일반관리비", ProcessingStatusCode.COMPLETED,
                        ProcessingStatusCode.PARTIAL, ProcessingStatusCode.NOT_STARTED, 3,
                        "2026-07-01T20:05:44+09:00")
        );

        List<ExpenseCategoryStatus> filtered = new ArrayList<ExpenseCategoryStatus>();
        for (ExpenseCategoryStatus item : all) {
            if (!StringUtils.hasText(request.getExpenseCategory())
                    || item.getExpenseCategory().equalsIgnoreCase(request.getExpenseCategory())) {
                filtered.add(item);
            }
        }

        int errors = 0;
        for (ExpenseCategoryStatus item : filtered) {
            errors += item.getErrorCount();
        }

        response.setExpenses(filtered);
        response.setResultCount(filtered.size());
        response.setErrorCount(errors);
        response.setOverallStatus(filtered.isEmpty() ? ProcessingStatusCode.NOT_STARTED
                : ProcessingStatusCode.PARTIAL);
        return response;
    }
}
