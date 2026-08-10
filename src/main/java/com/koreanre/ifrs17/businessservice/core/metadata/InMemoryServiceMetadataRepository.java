package com.koreanre.ifrs17.businessservice.core.metadata;

import com.koreanre.ifrs17.businessservice.core.exception.ServiceNotFoundException;
import com.koreanre.ifrs17.businessservice.core.exception.ValidationException;
import org.springframework.stereotype.Repository;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

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

    /**
     * [Mock] BS_SERVICE_VERSION.status_code = 'INACTIVE' 인 버전. 키 형식은 "serviceId:version".
     * DB 연동 시 BsServiceVersionMapper.selectActive() 조회로 교체한다.
     */
    private final Set<String> inactiveVersions = new LinkedHashSet<String>();

    /**
     * 별첨E 표준처리순서정의서 v3.0 - 5단계 Service Catalog에서 활성 버전 조회.
     *
     * <ul>
     *   <li>(1) Service ID 필수 입력 체크 — 미입력이면 BS-VAL-001(400)</li>
     *   <li>(2) Service ID 유효성 체크 — BS_SERVICE 존재 여부와 active_yn = 'Y', BS-SVC-404(404)</li>
     *   <li>(3) 버전 정보 설정 — 요청에 버전이 없으면 해당 서비스의 최신 버전으로 자동 설정</li>
     *   <li>(4) 버전 정보 유효성 체크 — BS_SERVICE_VERSION 존재 여부와 status_code = 'ACTIVE', BS-SVC-404(404)</li>
     * </ul>
     *
     * <p>BS_SERVICE 는 active_yn, BS_SERVICE_VERSION 은 status_code 로 상태를 표기하므로
     * 두 테이블의 상태 컬럼명이 서로 다른 점에 유의한다.</p>
     */
    @Override
    public ServiceMetadata findActive(String serviceId, String version) {
        // (1) Service ID 필수 입력 체크 - [Path] serviceId
        if (!StringUtils.hasText(serviceId)) {
            throw ValidationException.of("serviceId", "Service ID 가 입력되지 않았습니다.");
        }

        // (2) Service ID 유효성 체크 - BS_SERVICE (service_id, active_yn) [핵심 로직]
        ServiceMetadata metadata = catalog.get(serviceId);
        if (metadata == null) {
            throw new ServiceNotFoundException("서비스 미존재 : serviceId=" + serviceId);
        }
        if (!metadata.isActive()) {
            throw new ServiceNotFoundException("서비스 미사용 : serviceId=" + serviceId);
        }

        // (3) 버전 정보 설정 - 요청에 버전이 없으면 최신 버전으로 자동 설정
        String resolvedVersion = StringUtils.hasText(version) ? version : latestVersionOf(metadata);

        // (4) 버전 정보 유효성 체크 - BS_SERVICE_VERSION (version, status_code) [핵심 로직]
        if (!resolvedVersion.equals(metadata.getVersion())) {
            throw new ServiceNotFoundException("버전 미존재 : serviceId=" + serviceId
                    + ", version=" + resolvedVersion);
        }
        if (inactiveVersions.contains(serviceId + ":" + resolvedVersion)) {
            throw new ServiceNotFoundException("버전 미사용(INACTIVE) : serviceId=" + serviceId
                    + ", version=" + resolvedVersion);
        }

        return metadata;
    }

    /**
     * 서비스의 최신 버전.
     *
     * <p><b>[Mock]</b> In-Memory Catalog 는 서비스당 버전 1건만 보유하므로 그 값을 반환한다.
     * DB 연동 시 BS_SERVICE_VERSION 에서 status_code = 'ACTIVE' 인 최신 version 조회로 교체한다.</p>
     */
    private String latestVersionOf(ServiceMetadata metadata) {
        return metadata.getVersion();
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
