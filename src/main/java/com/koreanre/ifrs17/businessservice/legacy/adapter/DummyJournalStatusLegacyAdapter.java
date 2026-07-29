package com.koreanre.ifrs17.businessservice.legacy.adapter;

import com.koreanre.ifrs17.businessservice.domain.common.LegacyBinding;
import com.koreanre.ifrs17.businessservice.domain.common.ProcessingStatusCode;
import com.koreanre.ifrs17.businessservice.domain.journal.JournalStatusRequest;
import com.koreanre.ifrs17.businessservice.domain.journal.JournalStatusResponse;
import com.koreanre.ifrs17.businessservice.domain.journal.JournalTypeStatus;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * IFRS17.JOURNAL.STATUS 용 Legacy Adapter 의 Skeleton 구현체.
 *
 * <p><b>[Mock]</b> 기존 전표 Service 호출 대신 하드코딩 결과를 반환한다.</p>
 * <p><b>[Draft]</b> 배치 프로그램 ID·화면명 미확정(임시값).</p>
 */
@Component
public class DummyJournalStatusLegacyAdapter implements LegacyAdapter<JournalStatusRequest, JournalStatusResponse> {

    private static final String BATCH_PROGRAM_ID = "TBD-BAT-JOURNAL-STATUS-001";
    private static final String SCREEN_NAME = "TBD-전표생성현황조회";

    @Override
    public String legacyBatchProgramId() {
        return BATCH_PROGRAM_ID;
    }

    @Override
    public String legacyScreenName() {
        return SCREEN_NAME;
    }

    @Override
    public JournalStatusResponse invoke(JournalStatusRequest request) {
        System.out.println("[BSL-LEGACY] adapter=DummyJournalStatusLegacyAdapter"
                + ", batchProgramId=" + BATCH_PROGRAM_ID
                + ", closingYearMonth=" + request.getClosingYearMonth() + " (Mock 호출)");

        JournalStatusResponse response = new JournalStatusResponse();
        response.setClosingYearMonth(request.getClosingYearMonth());
        response.setBaseDateTime("2026-07-01T21:00:00+09:00");
        response.setLegacyBinding(new LegacyBinding(BATCH_PROGRAM_ID, SCREEN_NAME));

        if (!MockDataPolicy.hasData(request.getClosingYearMonth())) {
            response.setJournals(new ArrayList<JournalTypeStatus>());
            response.setOverallStatus(ProcessingStatusCode.NOT_STARTED);
            response.setResultCount(0);
            return response;
        }

        List<JournalTypeStatus> all = Arrays.asList(
                new JournalTypeStatus("INSURANCE_REVENUE", "보험수익 전표", ProcessingStatusCode.COMPLETED,
                        12450L, 12450L, 0, "2026-07-01T20:12:33+09:00"),
                new JournalTypeStatus("INSURANCE_EXPENSE", "보험비용 전표", ProcessingStatusCode.COMPLETED,
                        9832L, 9832L, 0, "2026-07-01T20:20:41+09:00"),
                new JournalTypeStatus("CSM_AMORTIZATION", "CSM 상각 전표", ProcessingStatusCode.PARTIAL,
                        4310L, 4285L, 25, "2026-07-01T20:47:09+09:00"),
                new JournalTypeStatus("REINSURANCE", "출재 전표", ProcessingStatusCode.NOT_STARTED,
                        0L, 0L, 0, null)
        );

        List<JournalTypeStatus> filtered = new ArrayList<JournalTypeStatus>();
        for (JournalTypeStatus item : all) {
            if (!StringUtils.hasText(request.getJournalType())
                    || item.getJournalType().equalsIgnoreCase(request.getJournalType())) {
                filtered.add(item);
            }
        }

        long created = 0L;
        long posted = 0L;
        int errors = 0;
        for (JournalTypeStatus item : filtered) {
            created += item.getCreatedCount();
            posted += item.getPostedCount();
            errors += item.getErrorCount();
        }

        response.setJournals(filtered);
        response.setTotalCreatedCount(created);
        response.setTotalPostedCount(posted);
        response.setErrorCount(errors);
        response.setResultCount(filtered.size());
        response.setLastProcessedAt("2026-07-01T20:47:09+09:00");
        response.setOverallStatus(filtered.isEmpty() ? ProcessingStatusCode.NOT_STARTED
                : (errors > 0 ? ProcessingStatusCode.PARTIAL : ProcessingStatusCode.COMPLETED));
        return response;
    }
}
