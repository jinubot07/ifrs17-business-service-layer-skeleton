package com.koreanre.ifrs17.businessservice.legacy.adapter;

import com.koreanre.ifrs17.businessservice.domain.common.LegacyBinding;
import com.koreanre.ifrs17.businessservice.domain.common.ProcessingStatusCode;
import com.koreanre.ifrs17.businessservice.domain.csm.CsmPortfolioStatus;
import com.koreanre.ifrs17.businessservice.domain.csm.CsmStatusRequest;
import com.koreanre.ifrs17.businessservice.domain.csm.CsmStatusResponse;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * IFRS17.CSM.STATUS 용 Legacy Adapter 의 Skeleton 구현체.
 *
 * <p><b>[Mock]</b> 기존 CSM 산출 Service 호출 대신 하드코딩 결과를 반환한다.</p>
 * <p><b>[Draft]</b> 배치 프로그램 ID·화면명 미확정(임시값).</p>
 */
@Component
public class DummyCsmStatusLegacyAdapter implements LegacyAdapter<CsmStatusRequest, CsmStatusResponse> {

    private static final String BATCH_PROGRAM_ID = "TBD-BAT-CSM-STATUS-001";
    private static final String SCREEN_NAME = "TBD-CSM산출현황조회";

    @Override
    public String legacyBatchProgramId() {
        return BATCH_PROGRAM_ID;
    }

    @Override
    public String legacyScreenName() {
        return SCREEN_NAME;
    }

    @Override
    public CsmStatusResponse invoke(CsmStatusRequest request) {
        System.out.println("[BSL-LEGACY] adapter=DummyCsmStatusLegacyAdapter"
                + ", batchProgramId=" + BATCH_PROGRAM_ID
                + ", closingYearMonth=" + request.getClosingYearMonth() + " (Mock 호출)");

        CsmStatusResponse response = new CsmStatusResponse();
        response.setClosingYearMonth(request.getClosingYearMonth());
        response.setBaseDateTime("2026-07-01T21:00:00+09:00");
        response.setLegacyBinding(new LegacyBinding(BATCH_PROGRAM_ID, SCREEN_NAME));

        if (!MockDataPolicy.hasData(request.getClosingYearMonth())) {
            response.setPortfolios(new ArrayList<CsmPortfolioStatus>());
            response.setOverallStatus(ProcessingStatusCode.NOT_STARTED);
            response.setResultCount(0);
            return response;
        }

        List<CsmPortfolioStatus> all = Arrays.asList(
                new CsmPortfolioStatus("PF-LIFE-001", "생명재보험 일반", ProcessingStatusCode.COMPLETED,
                        128340L, 0, "2026-07-01T20:41:07+09:00"),
                new CsmPortfolioStatus("PF-LIFE-002", "생명재보험 특약", ProcessingStatusCode.RUNNING,
                        64120L, 0, "2026-07-01T20:52:15+09:00"),
                new CsmPortfolioStatus("PF-NONLIFE-001", "손해재보험 일반", ProcessingStatusCode.PARTIAL,
                        31980L, 12, "2026-07-01T20:48:53+09:00")
        );

        List<CsmPortfolioStatus> filtered = new ArrayList<CsmPortfolioStatus>();
        for (CsmPortfolioStatus item : all) {
            if (!StringUtils.hasText(request.getPortfolioCode())
                    || item.getPortfolioCode().equalsIgnoreCase(request.getPortfolioCode())) {
                filtered.add(item);
            }
        }

        long contracts = 0L;
        int errors = 0;
        for (CsmPortfolioStatus item : filtered) {
            contracts += item.getContractCount();
            errors += item.getErrorCount();
        }

        response.setPortfolios(filtered);
        response.setTotalContractCount(contracts);
        response.setResultCount(filtered.size());
        response.setErrorCount(errors);
        response.setOverallStatus(filtered.isEmpty() ? ProcessingStatusCode.NOT_STARTED
                : (errors > 0 ? ProcessingStatusCode.PARTIAL : ProcessingStatusCode.RUNNING));
        return response;
    }
}
