package com.koreanre.ifrs17.businessservice.persistence.mapper;

import com.koreanre.ifrs17.businessservice.persistence.model.BsServiceVersion;

import java.util.List;

/**
 * BS_SERVICE_VERSION 접근 계약 (설계서 7.2 / 7.3).
 *
 * <p><b>[Skeleton — 미구현]</b> XML 매핑 파일과 구현체는 DB 연동 단계에서 작성한다.
 * {@link BsServiceMapper} 의 주석을 함께 참고한다.</p>
 */
public interface BsServiceVersionMapper {

    /** 활성 버전 조회 (설계서 4.2 - findActive(serviceId, version) 의 DB 대응). */
    BsServiceVersion selectActive(String serviceId, String version);

    /** 서비스별 버전 목록 조회. */
    List<BsServiceVersion> selectByServiceId(String serviceId);

    int insert(BsServiceVersion version);

    int update(BsServiceVersion version);
}
