package com.koreanre.ifrs17.businessservice.domain.statement;

import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.validator.ValidationUtils;
import com.koreanre.ifrs17.businessservice.core.workflow.BusinessServiceHandler;
import com.koreanre.ifrs17.businessservice.legacy.adapter.DummyStatementStatusLegacyAdapter;
import com.koreanre.ifrs17.businessservice.legacy.adapter.MockDataPolicy;
import org.springframework.stereotype.Component;

/**
 * IFRS17.STATEMENT.STATUS - 재무제표 산출 상태 조회 Handler (설계서 9.4).
 *
 * <p><b>[Mock]</b> 하드코딩 결과 반환. 실제 구현 시 Bean 명은 statementStatusBusinessService 로 전환한다.</p>
 */
@Component("dummyStatementStatusHandler")
public class DummyStatementStatusHandler
        implements BusinessServiceHandler<StatementStatusRequest, StatementStatusResponse> {

    private final DummyStatementStatusLegacyAdapter adapter;

    public DummyStatementStatusHandler(DummyStatementStatusLegacyAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public String serviceId() {
        return "IFRS17.STATEMENT.STATUS";
    }

    @Override
    public Class<StatementStatusRequest> requestType() {
        return StatementStatusRequest.class;
    }

    @Override
    public void validate(ServiceContext context, StatementStatusRequest request) {
        ValidationUtils.requireYearMonth("closingYearMonth", request.getClosingYearMonth());
        ValidationUtils.maxLength("statementType", request.getStatementType(), 30);
    }

    @Override
    public void authorize(ServiceContext context, StatementStatusRequest request) {
        // [Mock] 추가 권한 확인 없음.
    }

    @Override
    public StatementStatusResponse process(ServiceContext context, StatementStatusRequest request) {
        StatementStatusResponse response = adapter.invoke(request);
        if (response.getStatements().isEmpty()) {
            context.addWarning(MockDataPolicy.NO_DATA_CODE, MockDataPolicy.NO_DATA_MESSAGE);
        }
        return response;
    }
}
