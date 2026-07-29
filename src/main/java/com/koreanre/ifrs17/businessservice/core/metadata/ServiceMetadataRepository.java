package com.koreanre.ifrs17.businessservice.core.metadata;

import java.util.List;

/**
 * 서비스 Catalog 조회 컴포넌트 (설계서 4.2 - ServiceMetadataRepository).
 *
 * <p>필수 인터페이스: {@code findActive(serviceId, version)}</p>
 */
public interface ServiceMetadataRepository {

    /**
     * 활성 상태의 서비스 메타데이터를 조회한다.
     *
     * @param serviceId 서비스 ID
     * @param version   요청 버전. null 이면 기본(활성) 버전을 반환한다.
     * @return 활성 메타데이터. 없으면 null (Executor 가 BS-SVC-404 로 변환)
     */
    ServiceMetadata findActive(String serviceId, String version);

    /** Catalog 전체 목록 (설계서 5.1 GET /catalog). */
    List<ServiceMetadata> findAll();

    /** 활성/비활성 무관 단건 조회 (설계서 5.1 GET /catalog/{serviceId}). */
    ServiceMetadata findById(String serviceId);
}
