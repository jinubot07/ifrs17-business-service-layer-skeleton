package com.koreanre.ifrs17.businessservice.core.context;

import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Request ID / Trace ID / Error ID 생성기.
 *
 * <p>별첨E 표준처리순서정의서 v3.0 - 2단계 Request ID 생성 또는 검증.
 * request_id 는 호출자 입력 여부와 무관하게 항상 서버가 채번하며,
 * 테이블 설계서 BS_CALL_LOG 의 PK 양식 {@code REQ-yyyyMMddHHmmssSSS-n} 을 따른다.
 * (예: REQ-20260714093012345-1)</p>
 *
 * <p>Trace ID 는 BS_CALL_LOG 의 PK 가 아니므로 호출자 입력값이 있으면 그대로 사용한다.
 * 본 채번은 Trace ID 를 서버가 만들어야 하는 경우에만 사용한다.</p>
 *
 * <p><b>[Skeleton]</b> WAS 단일 인스턴스 기준의 In-Memory Sequence 를 사용한다.
 * 운영 다중 WAS 환경에서는 server_instance 를 포함한 채번 정책으로 대체해야 한다.</p>
 */
@Component
public class IdGenerator {

    /** BS_CALL_LOG PK 양식의 시각 부분 (밀리초까지). */
    private static final DateTimeFormatter REQUEST_ID_TIME = DateTimeFormatter.ofPattern("yyyyMMddHHmmssSSS");
    private static final DateTimeFormatter DATE = DateTimeFormatter.ofPattern("yyyyMMdd");

    private final AtomicInteger requestSequence = new AtomicInteger(0);
    private final AtomicInteger traceSequence = new AtomicInteger(0);
    private final AtomicInteger errorSequence = new AtomicInteger(0);

    /** BS_CALL_LOG PK 채번 : REQ-yyyyMMddHHmmssSSS-n (기능정의서 2단계). */
    public String newRequestId() {
        return String.format("REQ-%s-%d",
                LocalDateTime.now().format(REQUEST_ID_TIME), next(requestSequence, 9999));
    }

    public String newTraceId() {
        return String.format("TRACE-%s-%04d", today(), next(traceSequence, 9999));
    }

    public String newErrorId() {
        return String.format("ERR-%s-%05d", today(), next(errorSequence, 99999));
    }

    private String today() {
        return LocalDate.now().format(DATE);
    }

    private int next(AtomicInteger sequence, int max) {
        int value = sequence.incrementAndGet();
        if (value > max) {
            sequence.set(1);
            value = 1;
        }
        return value;
    }
}
