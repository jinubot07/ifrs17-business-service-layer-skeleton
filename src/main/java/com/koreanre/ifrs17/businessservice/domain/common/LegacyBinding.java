package com.koreanre.ifrs17.businessservice.domain.common;

/**
 * [Draft] 서비스별 Legacy 연계 정보.
 *
 * <p>각 서비스가 호출할 IFRS17 배치 프로그램과 대응 화면은 아직 확정되지 않았다
 * (설계서 14장 No.1 - 착수 5영업일 내 "현행 매핑서" 제출 대상).</p>
 *
 * <p>Skeleton 에서는 테스트로 확인 가능하도록 임시값을 응답에 노출한다.
 * 실제 구현 시 본 블록은 제거하거나 내부 감사 정보로만 사용한다.</p>
 */
public class LegacyBinding {

    /** [Draft] 호출 대상 배치 프로그램 ID (미확정). */
    private String batchProgramId;

    /** [Draft] 대응 현행 화면명 (미확정). */
    private String screenName;

    /** 확정 여부. Skeleton 단계에서는 항상 false. */
    private boolean confirmed;

    /** 비고. */
    private String note = "설계서 14장 No.1 현행 매핑서 확정 전 임시값(Draft)";

    public LegacyBinding() {
    }

    public LegacyBinding(String batchProgramId, String screenName) {
        this.batchProgramId = batchProgramId;
        this.screenName = screenName;
        this.confirmed = false;
    }

    public String getBatchProgramId() {
        return batchProgramId;
    }

    public void setBatchProgramId(String batchProgramId) {
        this.batchProgramId = batchProgramId;
    }

    public String getScreenName() {
        return screenName;
    }

    public void setScreenName(String screenName) {
        this.screenName = screenName;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
