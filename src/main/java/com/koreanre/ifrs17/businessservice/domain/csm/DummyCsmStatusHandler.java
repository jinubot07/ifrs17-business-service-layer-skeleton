package com.koreanre.ifrs17.businessservice.domain.csm;

import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.validator.ValidationUtils;
import com.koreanre.ifrs17.businessservice.core.workflow.BusinessServiceHandler;
import com.koreanre.ifrs17.businessservice.legacy.adapter.DummyCsmStatusLegacyAdapter;
import com.koreanre.ifrs17.businessservice.legacy.adapter.MockDataPolicy;
import org.springframework.stereotype.Component;

/**
 * IFRS17.CSM.STATUS - CSM 산출 상태 조회 Handler (설계서 9.5).
 *
 * <p><b>[Mock]</b> 하드코딩 결과 반환. 실제 구현 시 Bean 명은 csmStatusBusinessService 로 전환한다.</p>
 */
@Component("dummyCsmStatusHandler")
public class DummyCsmStatusHandler implements BusinessServiceHandler<CsmStatusRequest, CsmStatusResponse> {

    private final DummyCsmStatusLegacyAdapter adapter;

    public DummyCsmStatusHandler(DummyCsmStatusLegacyAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public String serviceId() {
        return "IFRS17.CSM.STATUS";
    }

    @Override
    public Class<CsmStatusRequest> requestType() {
        return CsmStatusRequest.class;
    }

    @Override
    public void validate(ServiceContext context, CsmStatusRequest request) {
        ValidationUtils.requireYearMonth("closingYearMonth", request.getClosingYearMonth());
        ValidationUtils.maxLength("portfolioCode", request.getPortfolioCode(), 30);
    }

    @Override
    public void authorize(ServiceContext context, CsmStatusRequest request) {
        // [Mock] 추가 권한 확인 없음.
    }

    @Override
    public CsmStatusResponse process(ServiceContext context, CsmStatusRequest request) {
        CsmStatusResponse response = adapter.invoke(request);
        if (response.getPortfolios().isEmpty()) {
            context.addWarning(MockDataPolicy.NO_DATA_CODE, MockDataPolicy.NO_DATA_MESSAGE);
        }
        return response;
    }
}
