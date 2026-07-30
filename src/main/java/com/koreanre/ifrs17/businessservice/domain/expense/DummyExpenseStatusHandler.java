package com.koreanre.ifrs17.businessservice.domain.expense;

import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.validator.ValidationUtils;
import com.koreanre.ifrs17.businessservice.core.workflow.BusinessServiceHandler;
import com.koreanre.ifrs17.businessservice.legacy.adapter.DummyExpenseStatusLegacyAdapter;
import com.koreanre.ifrs17.businessservice.legacy.adapter.MockDataPolicy;
import org.springframework.stereotype.Component;

/**
 * IFRS17.EXPENSE.STATUS - 사업비 처리 상태 조회 Handler (설계서 9.3).
 *
 * <p><b>[Mock]</b> 하드코딩 결과 반환. 실제 구현 시 Bean 명은 expenseStatusBusinessService 로 전환한다.</p>
 */
@Component("dummyExpenseStatusHandler")
public class DummyExpenseStatusHandler implements BusinessServiceHandler<ExpenseStatusRequest, ExpenseStatusResponse> {

    private final DummyExpenseStatusLegacyAdapter adapter;

    public DummyExpenseStatusHandler(DummyExpenseStatusLegacyAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public String serviceId() {
        return "IFRS17.EXPENSE.STATUS";
    }

    @Override
    public Class<ExpenseStatusRequest> requestType() {
        return ExpenseStatusRequest.class;
    }

    @Override
    public void validate(ServiceContext context, ExpenseStatusRequest request) {
        ValidationUtils.requireYearMonth("closingYearMonth", request.getClosingYearMonth());
        ValidationUtils.maxLength("expenseCategory", request.getExpenseCategory(), 30);
    }

    @Override
    public void authorize(ServiceContext context, ExpenseStatusRequest request) {
        // [Mock] 추가 권한 확인 없음.
    }

    @Override
    public ExpenseStatusResponse process(ServiceContext context, ExpenseStatusRequest request) {
        ExpenseStatusResponse response = adapter.invoke(request);
        if (response.getExpenses().isEmpty()) {
            context.addWarning(MockDataPolicy.NO_DATA_CODE, MockDataPolicy.NO_DATA_MESSAGE);
        }
        return response;
    }
}
