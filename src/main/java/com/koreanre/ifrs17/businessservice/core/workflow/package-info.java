/**
 * Business Service Workflow 계층 (설계서 4.1 권장 패키지 구조 - core.workflow).
 *
 * <p>다중 Legacy Service 호출 조합 / 단계별 Workflow 는 Phase 1 Skeleton 범위에 포함하지 않는다.
 * 파일럿 5종은 단일 Legacy Adapter 호출 구조이므로 Handler 내부에서 직접 처리한다.
 * Action 서비스(승인·멱등성) 도입 시 본 패키지에 Workflow 컴포넌트를 추가한다(설계서 1.3 차기 단계).</p>
 */
package com.koreanre.ifrs17.businessservice.core.workflow;
