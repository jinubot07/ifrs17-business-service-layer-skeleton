package com.koreanre.ifrs17.businessservice.domain.closing;

import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.validator.ValidationUtils;
import com.koreanre.ifrs17.businessservice.core.workflow.BusinessServiceHandler;
import com.koreanre.ifrs17.businessservice.legacy.adapter.DummyClosingStatusLegacyAdapter;
import com.koreanre.ifrs17.businessservice.legacy.adapter.MockDataPolicy;
import org.springframework.stereotype.Component;

/**
 * IFRS17.CLOSING.STATUS - 결산 진행상태 조회 Handler (설계서 9.1 / 10.2).
 *
 * <p><b>[Mock]</b> DB 접근 없이 Legacy Adapter 의 하드코딩 결과를 반환한다.
 * 실제 구현 시 클래스명은 {@code ClosingStatusBusinessService}, Bean 명은
 * {@code closingStatusBusinessService} 로 전환한다(설계서 10.2 / 부록 B).</p>
 */
@Component("dummyClosingStatusHandler")
public class DummyClosingStatusHandler implements BusinessServiceHandler<ClosingStatusRequest, ClosingStatusResponse> {

    private final DummyClosingStatusLegacyAdapter adapter;

    public DummyClosingStatusHandler(DummyClosingStatusLegacyAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public String serviceId() {
        return "IFRS17.CLOSING.STATUS";
    }

    @Override
    public Class<ClosingStatusRequest> requestType() {
        return ClosingStatusRequest.class;
    }

    @Override
    public void validate(ServiceContext context, ClosingStatusRequest request) {
        // 기준년월은 YYYY-MM 형식으로 검증한다(설계서 9.1).
        ValidationUtils.requireYearMonth("closingYearMonth", request.getClosingYearMonth());
        ValidationUtils.maxLength("closingType", request.getClosingType(), 20);
    }

    @Override
    public void authorize(ServiceContext context, ClosingStatusRequest request) {
        // [Mock] 공통 Role 검사 통과 후 추가 확인 없음.
        // 실제 구현 시 기존 IFRS17 결산/회계 권한 Service 로 기준년월 단위 권한을 추가 확인한다(설계서 9.1).
    }

    @Override
    public ClosingStatusResponse process(ServiceContext context, ClosingStatusRequest request) {
        ClosingStatusResponse response = adapter.invoke(request);
        if (response.getStages().isEmpty()) {
            // 데이터 없음: SUCCESS + 빈 결과 + 경고 (설계서 4.5 BS-DATA-000)
            context.addWarning(MockDataPolicy.NO_DATA_CODE, MockDataPolicy.NO_DATA_MESSAGE);
        }
        return response;
    }
}
