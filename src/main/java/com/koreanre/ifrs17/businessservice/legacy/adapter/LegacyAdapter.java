package com.koreanre.ifrs17.businessservice.legacy.adapter;

/**
 * 기존(Legacy) IFRS17 Service 호출 및 DTO 변환 (설계서 4.2 - LegacyAdapter).
 *
 * <p>필수 인터페이스: {@code invoke(domainRequest)}</p>
 *
 * <p>Adapter 는 기존 Service 호출과 DTO 변환만 담당하며, 업무규칙 재구현과
 * 신규 DAO 남발은 금지된다(설계서 3.2 책임 분리).</p>
 *
 * @param <REQ> 도메인 요청 DTO
 * @param <RES> 도메인 응답 DTO
 */
public interface LegacyAdapter<REQ, RES> {

    /** 대상 Legacy 연계 식별자 (배치 프로그램 ID 등). Skeleton 단계에서는 임시값(Draft)이다. */
    String legacyBatchProgramId();

    /** 대응 현행 화면명. Skeleton 단계에서는 임시값(Draft)이다. */
    String legacyScreenName();

    /** 기존 Service 호출. */
    RES invoke(REQ domainRequest);
}
