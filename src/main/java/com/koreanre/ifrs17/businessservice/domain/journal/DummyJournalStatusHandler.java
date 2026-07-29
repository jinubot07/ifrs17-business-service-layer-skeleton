package com.koreanre.ifrs17.businessservice.domain.journal;

import com.koreanre.ifrs17.businessservice.core.context.ServiceContext;
import com.koreanre.ifrs17.businessservice.core.validator.ValidationUtils;
import com.koreanre.ifrs17.businessservice.domain.BusinessServiceHandler;
import com.koreanre.ifrs17.businessservice.legacy.adapter.DummyJournalStatusLegacyAdapter;
import com.koreanre.ifrs17.businessservice.legacy.adapter.MockDataPolicy;
import org.springframework.stereotype.Component;

/**
 * IFRS17.JOURNAL.STATUS - 전표 생성·반영 상태 조회 Handler (설계서 9.2).
 *
 * <p><b>[Mock]</b> 하드코딩 결과 반환. 실제 구현 시 Bean 명은 journalStatusBusinessService 로 전환한다.</p>
 */
@Component("dummyJournalStatusHandler")
public class DummyJournalStatusHandler implements BusinessServiceHandler<JournalStatusRequest, JournalStatusResponse> {

    private final DummyJournalStatusLegacyAdapter adapter;

    public DummyJournalStatusHandler(DummyJournalStatusLegacyAdapter adapter) {
        this.adapter = adapter;
    }

    @Override
    public String serviceId() {
        return "IFRS17.JOURNAL.STATUS";
    }

    @Override
    public Class<JournalStatusRequest> requestType() {
        return JournalStatusRequest.class;
    }

    @Override
    public void validate(ServiceContext context, JournalStatusRequest request) {
        ValidationUtils.requireYearMonth("closingYearMonth", request.getClosingYearMonth());
        ValidationUtils.maxLength("journalType", request.getJournalType(), 30);
    }

    @Override
    public void authorize(ServiceContext context, JournalStatusRequest request) {
        // [Mock] 추가 권한 확인 없음.
    }

    @Override
    public JournalStatusResponse process(ServiceContext context, JournalStatusRequest request) {
        JournalStatusResponse response = adapter.invoke(request);
        if (response.getJournals().isEmpty()) {
            context.addWarning(MockDataPolicy.NO_DATA_CODE, MockDataPolicy.NO_DATA_MESSAGE);
        }
        return response;
    }
}
