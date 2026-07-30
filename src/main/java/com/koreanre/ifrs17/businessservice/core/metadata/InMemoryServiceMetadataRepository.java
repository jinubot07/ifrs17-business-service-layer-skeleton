package com.koreanre.ifrs17.businessservice.core.metadata;

import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * ServiceMetadataRepository 의 Skeleton 구현체.
 *
 * <p><b>[Mock]</b> DB(business_service.bs_service / bs_service_version) 대신
 * 설계서 9장 파일럿 5종 명세를 하드코딩한 In-Memory Catalog 를 제공한다.</p>
 *
 * <p>[Draft] legacyBatchProgramId / legacyScreenName 은 아직 확정되지 않은 항목이므로
 * 테스트 가능한 임시값(TBD-...)을 넣어둔다. 설계서 14장 No.1 착수분석에서 확정한다.</p>
 */
@Repository
public class InMemoryServiceMetadataRepository implements ServiceMetadataRepository {

    private final Map<String, ServiceMetadata> catalog = new LinkedHashMap<String, ServiceMetadata>();

    public InMemoryServiceMetadataRepository() {
        register(ServiceMetadata.builder("IFRS17.CLOSING.STATUS")
                .serviceName("결산 진행상태 조회")
                .domainCode("CLOSING")
                .implementationBean("dummyClosingStatusHandler")
                .requiredRoles("IFRS17_USER", "IFRS17_CLOSING_VIEW")
                .legacyBatchProgramId("TBD-BAT-CLOSING-STATUS-001")
                .legacyScreenName("TBD-결산진행현황조회")
                .build());

        register(ServiceMetadata.builder("IFRS17.JOURNAL.STATUS")
                .serviceName("전표 생성·반영 상태 조회")
                .domainCode("JOURNAL")
                .implementationBean("dummyJournalStatusHandler")
                .requiredRoles("IFRS17_USER", "IFRS17_JOURNAL_VIEW")
                .legacyBatchProgramId("TBD-BAT-JOURNAL-STATUS-001")
                .legacyScreenName("TBD-전표생성현황조회")
                .build());

        register(ServiceMetadata.builder("IFRS17.EXPENSE.STATUS")
                .serviceName("사업비 처리 상태 조회")
                .domainCode("EXPENSE")
                .implementationBean("dummyExpenseStatusHandler")
                .requiredRoles("IFRS17_USER", "IFRS17_EXPENSE_VIEW")
                .legacyBatchProgramId("TBD-BAT-EXPENSE-STATUS-001")
                .legacyScreenName("TBD-사업비처리현황조회")
                .build());

        register(ServiceMetadata.builder("IFRS17.STATEMENT.STATUS")
                .serviceName("재무제표 산출 상태 조회")
                .domainCode("STATEMENT")
                .implementationBean("dummyStatementStatusHandler")
                .requiredRoles("IFRS17_USER", "IFRS17_STATEMENT_VIEW")
                .legacyBatchProgramId("TBD-BAT-STATEMENT-STATUS-001")
                .legacyScreenName("TBD-재무제표산출현황조회")
                .build());

        register(ServiceMetadata.builder("IFRS17.CSM.STATUS")
                .serviceName("CSM 산출 상태 조회")
                .domainCode("CSM")
                .implementationBean("dummyCsmStatusHandler")
                .requiredRoles("IFRS17_USER", "IFRS17_CSM_VIEW")
                .legacyBatchProgramId("TBD-BAT-CSM-STATUS-001")
                .legacyScreenName("TBD-CSM산출현황조회")
                .build());

        // [테스트용] 비활성 서비스 - 설계서 8.7 CON-ACC-03(미사용 서비스 차단) 확인용
        register(ServiceMetadata.builder("IFRS17.SAMPLE.DISABLED")
                .serviceName("비활성 샘플 서비스(테스트용)")
                .domainCode("SAMPLE")
                .implementationBean("dummyClosingStatusHandler")
                .requiredRoles("IFRS17_USER")
                .active(false)
                .legacyBatchProgramId("TBD-NONE")
                .legacyScreenName("TBD-NONE")
                .build());
    }

    private void register(ServiceMetadata metadata) {
        catalog.put(metadata.getServiceId(), metadata);
    }

    @Override
    public ServiceMetadata findActive(String serviceId, String version) {
        ServiceMetadata metadata = catalog.get(serviceId);
        if (metadata == null || !metadata.isActive()) {
            return null;
        }
        if (StringUtils.hasText(version) && !version.equals(metadata.getVersion())) {
            return null;
        }
        return metadata;
    }

    @Override
    public List<ServiceMetadata> findAll() {
        return Collections.unmodifiableList(new ArrayList<ServiceMetadata>(catalog.values()));
    }

    @Override
    public ServiceMetadata findById(String serviceId) {
        return catalog.get(serviceId);
    }

    @Override
    public ServiceMetadata save(ServiceMetadata metadata) {
        catalog.put(metadata.getServiceId(), metadata);
        System.out.println("[BSL-CONSOLE] 서비스 명세 저장: serviceId=" + metadata.getServiceId()
                + ", version=" + metadata.getVersion()
                + ", bean=" + metadata.getImplementationBean()
                + ", active=" + metadata.isActive());
        return metadata;
    }

    @Override
    public ServiceMetadata updateActive(String serviceId, boolean active) {
        ServiceMetadata metadata = catalog.get(serviceId);
        if (metadata == null) {
            return null;
        }
        metadata.setActive(active);
        System.out.println("[BSL-CONSOLE] 사용여부 변경: serviceId=" + serviceId
                + ", active=" + active);
        return metadata;
    }
}
