package com.koreanre.ifrs17.businessservice.persistence.mapper;

import com.koreanre.ifrs17.businessservice.persistence.model.BsService;

import java.util.List;

/**
 * BS_SERVICE 접근 계약 (설계서 4.1 persistence.mapper / 7.2 주요 테이블).
 *
 * <p><b>[Skeleton — 미구현]</b> 본 인터페이스는 DB 연동 단계의 계약(메서드 시그니처)만 정의한다.
 * Skeleton 단계에서는 다음을 <b>의도적으로 포함하지 않는다</b>.</p>
 * <ul>
 *   <li>SqlMap.xml / Mapper.xml 등 XML 매핑 파일</li>
 *   <li>MyBatis {@code @Mapper} 애노테이션 및 DataSource 설정</li>
 *   <li>구현체 Bean 등록</li>
 * </ul>
 *
 * <p>따라서 현재 Catalog 조회는 {@code InMemoryServiceMetadataRepository}(하드코딩)가 담당하며,
 * DB 연동 단계에서 본 Mapper 를 사용하는 구현체로 교체한다.</p>
 */
public interface BsServiceMapper {

    /** 서비스 단건 조회. */
    BsService selectById(String serviceId);

    /** 활성 서비스 전체 조회 (active_yn = 'Y'). */
    List<BsService> selectAllActive();

    /** 서비스 등록 (관리 Console - 설계서 8.3). */
    int insert(BsService service);

    /** 서비스 수정. */
    int update(BsService service);

    /** 사용/미사용 전환 (설계서 8.7 CON-ACC-03). */
    int updateActiveYn(String serviceId, String activeYn, String updatedBy);
}
