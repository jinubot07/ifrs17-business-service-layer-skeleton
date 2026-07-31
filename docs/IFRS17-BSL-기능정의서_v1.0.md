# IFRS17 Business Service Layer 기능정의서

> **문서번호** IFRS17-BSL-FD-001 · **버전** 1.0
> **근거 설계서** IFRS17-BSL-SDD-001 v1.3 — 4.3 표준 처리 순서 / 부록 A 표준 오류코드
> **대상 소스** `Skeleton_v1.3` (`com.koreanre.ifrs17.businessservice`)
> **용도** 개발 진행 관리 · 통합테스트 기준서

---

## 0. 문서 사용 방법

| 용도 | 사용 章 |
|---|---|
| 개발 진행 관리 | 3장 기능 목록(Master), 9장 진행 관리 대장 |
| 통합테스트 수행 | 5장 기능 상세(단계별 판정 기준), 8장 통합테스트 케이스 |
| 오류 처리 검증 | 6장 오류코드 역매핑, 7장 미구현 오류코드 |
| 실구현 전환 계획 | 5장 "현재 상태" 열, 10장 전환 영향 범위 |

**단계 번호 표기**: 설계서 Word 문서상 4.3 목록은 **6~18번**으로 표시된다(1.5 성공기준 5개 항목에서 번호가 연속되기 때문). 본 문서는 **설계서 표시번호(6~18)를 기준**으로 하며, 소스 주석의 `(1)~(13)`과의 대응은 3장 표에 병기한다.

---

## 1. 적용 범위

| 구분 | 내용 |
|---|---|
| 대상 API | `POST /api/business-services/v1/{serviceId}:execute` (공통 단일 Endpoint) |
| 부가 API | Catalog 조회 2종, 호출이력 조회 1종, Console 5종 |
| 대상 서비스 | 파일럿 5종 (CLOSING / JOURNAL / EXPENSE / STATEMENT / CSM STATUS) |
| 현 단계 | **Skeleton** — DB · SSO · Legacy 미연동, 하드코딩 Mock 데이터 |

---

## 2. 기능 ID 체계

```
BSL-F-nn        표준 처리 순서 단계 기능 (nn = 설계서 표시번호 06~18)
BSL-F-nn-x      해당 단계의 세부 기능
BSL-A-nn        부가 API 기능 (Catalog / 호출이력 / Console)
TC-nn-x         통합테스트 케이스
```

---

## 3. 기능 목록 (Master)

전 단계는 **`DefaultBusinessServiceExecutor.execute()`** 가 순차 호출한다. "호출 위치"는 해당 파일의 라인이다.

| 기능ID | 설계서<br>번호 | 소스<br>주석 | 기능명 | 담당 컴포넌트 | 호출<br>위치 | 발생 오류코드 | 현재<br>상태 |
|---|:---:|:---:|---|---|:---:|---|:---:|
| BSL-F-06 | 6 | (1) | HTTP Header/Body 수신 | `BusinessServiceController` | `:63` | BS-VAL-001 | ✅ 완료 |
| BSL-F-07 | 7 | (2) | Request ID 생성·검증 | `HttpHeaderRequestContextResolver`<br>`IdGenerator` | `:97` | — | ✅ 완료 |
| BSL-F-08 | 8 | (3) | 호출 Client 검증 | `DummyClientAuthenticationService` | `:100` | BS-AUTH-001<br>BS-AUTH-002 | ⚠️ Mock |
| BSL-F-09 | 9 | (4) | SSO 사용자 Context 추출 | `HttpHeaderRequestContextResolver` | `:97` | BS-AUTH-001 | ⚠️ Mock |
| BSL-F-10 | 10 | (5) | Catalog 활성 버전 조회 | `InMemoryServiceMetadataRepository` | `:103` | BS-SVC-404 | ⚠️ Mock |
| BSL-F-11 | 11 | (6) | 서비스/역할 권한 확인 | `DummyAuthorizationService`<br>`Handler.authorize()` | `:111`<br>`:123` | BS-AUTH-003 | ⚠️ Mock |
| BSL-F-12 | 12 | (7) | 입력 Schema·파라미터 검증 | `StandardRequestValidator`<br>`ParameterBinder`<br>`ValidationUtils` | `:115`<br>`:119`<br>`:120` | BS-VAL-001<br>BS-VAL-002 | ✅ 완료 |
| BSL-F-13 | 13 | (8) | 감사 시작 로그 기록 | `ConsoleAuditLogger.start()` | `:126` | — | ⚠️ Mock |
| BSL-F-14 | 14 | (9) | Business Service Handler 실행 | `DefaultBusinessServiceDispatcher`<br>`Dummy*StatusHandler` | `:118`<br>`:130` | BS-SVC-404 | ⚠️ Mock |
| BSL-F-15 | 15 | (10) | Legacy Adapter 호출 | `Dummy*StatusLegacyAdapter` | Handler<br>내부 | BS-LEG-500<br>BS-SYS-504 | ⚠️ Mock |
| BSL-F-16 | 16 | (11) | 표준 JSON 변환·마스킹 | `DefaultMaskingPolicy` | `:133` | BS-DATA-000<sup>주</sup> | ⚠️ Mock |
| BSL-F-17 | 17 | (12) | 감사 성공/실패 로그 기록 | `ConsoleAuditLogger.success()/fail()` | `:138`<br>`:174` | — | ⚠️ Mock |
| BSL-F-18 | 18 | (13) | 표준 Response 반환 | `DefaultStandardResponseBuilder` | `:141`<br>`:181` | BS-SYS-500 | ✅ 완료 |

<sup>주</sup> BS-DATA-000은 오류가 아니라 **성공 응답의 `warnings[]`** 로 반환된다(설계서 4.5).

**상태 범례** — ✅ 완료: 실구현과 동일 / ⚠️ Mock: 계약·흐름은 완성, 내부는 하드코딩

---

## 4. 데이터 변환 흐름

```
HTTP Request
  │  Header: X-Client-ID, X-Request-ID, X-User-ID, X-Trace-ID, Authorization
  │  Body  : StandardRequest { serviceVersion, parameters{}, options{} }
  ▼
[06] Controller          → serviceId(PathVariable) + StandardRequest 객체화
  ▼
[07][09] ContextResolver → ServiceContext 생성 (requestId·traceId·clientId·userId·roles·remoteIp)
  ▼
[08] Authentication      → ServiceContext 검증 (변경 없음)
  ▼
[10] MetadataRepository  → ServiceMetadata 획득 → context.serviceVersion 확정
  ▼
[11] Authorization       → context.roles × metadata.requiredRoles 대조
  ▼
[12] Validator+Binder    → parameters(Map) → ClosingStatusRequest(타입 DTO)
  ▼
[13] AuditLogger.start   → AuditRecord 생성 (parameterHash만 보관)
  ▼
[14] Dispatcher+Handler  → ClosingStatusRequest → ClosingStatusResponse
  ▼
[15] LegacyAdapter       → (실구현) LegacyClosingStatus → Mapper → ClosingStatusResponse
  ▼
[16] MaskingPolicy       → ClosingStatusResponse (마스킹 적용)
  ▼
[17] AuditLogger.success → AuditRecord 갱신 (elapsedMs, resultCount)
  ▼
[18] ResponseBuilder     → StandardResponse<ClosingStatusResponse>
  ▼
HTTP Response (200 / 4xx / 5xx)
```

---

## 5. 기능 상세 정의

---

### BSL-F-06 · HTTP Header 및 Request Body 수신

| 항목 | 내용 |
|---|---|
| **목적** | 외부 채널의 JSON 요청을 수신하고 Executor에 위임한다 |
| **담당 파일** | `api/controller/BusinessServiceController.java` |
| **메서드** | `execute(String serviceId, StandardRequest request, HttpServletRequest httpRequest)` (`:63`) |
| **URI** | `POST /api/business-services/v1/{serviceId}:execute` |
| **관련 DTO** | `api/dto/request/StandardRequest.java`, `RequestOptions.java` |

**입력 데이터**

| 위치 | 항목 | 타입 | 필수 | 비고 |
|---|---|---|:---:|---|
| Path | `serviceId` | String | Y | 예: `IFRS17.CLOSING.STATUS` |
| Header | `Content-Type` | String | Y | `application/json` 고정 |
| Header | `X-Client-ID` | String | Y | BSL-F-08에서 검증 |
| Header | `Authorization` | String | Y | 현재 미검증(Mock) |
| Header | `X-Request-ID` `X-User-ID` `X-Trace-ID` | String | N | 미입력 시 서버 생성/기본값 |
| Body | `serviceVersion` | String | N | 미입력 시 Catalog 활성버전 |
| Body | `parameters` | Map | Y | 서비스별 업무 파라미터 |
| Body | `options.locale` | String | N | 기본 `ko-KR` |
| Body | `options.includeDetails` | boolean | N | 기본 `false` |

**처리 로직** — Jackson이 Body를 `StandardRequest`로 역직렬화 → `executor.execute()` 위임. **Controller에 업무 SQL·업무규칙 작성 금지**(설계서 10.5).

**출력** — `ResponseEntity<StandardResponse<?>>` (Executor 반환값 그대로)

**오류코드**

| 코드 | HTTP | 발생 조건 | 처리 위치 |
|---|:---:|---|---|
| BS-VAL-001 | 400 | JSON 파싱 실패 (문법 오류, 타입 불일치) | `BusinessServiceExceptionHandler.handleUnreadable()` `:44` |
| BS-SVC-404 | 404 | 미정의 URI 호출 | `BusinessServiceExceptionHandler.handleNoHandler()` `:50` |

**판정 기준** — 정상 요청 시 HTTP 200 및 `Content-Type: application/json` 반환

---

### BSL-F-07 · Request ID 생성 또는 검증

| 항목 | 내용 |
|---|---|
| **목적** | 요청 단위 추적 식별자를 확정한다 (설계서 12.3 장애 대응의 기준 키) |
| **담당 파일** | `core/context/RequestContextResolver.java` (Interface)<br>`core/context/HttpHeaderRequestContextResolver.java` (구현)<br>`core/context/IdGenerator.java` |
| **메서드** | `resolve(HttpServletRequest, String serviceId, StandardRequest)` (`Executor:97`)<br>`IdGenerator.newRequestId()` / `newTraceId()` |

**입력 → 출력 데이터**

| 항목 | 입력 출처 | 미입력 시 생성 규칙 | 저장 위치 |
|---|---|---|---|
| `requestId` | Header `X-Request-ID` | `REQ-yyyyMMdd-0001` (AtomicInteger) | `ServiceContext.requestId` |
| `traceId` | Header `X-Trace-ID` | `TRACE-yyyyMMdd-0001` | `ServiceContext.traceId` |
| `requestedAt` | — | `LocalDateTime.now()` | `ServiceContext.requestedAt` |

**처리 로직** — Header 값이 있으면 그대로 사용(검증), 없으면 서버가 채번(설계서 5.2). 채번 값은 응답 Envelope과 감사로그에 동일하게 사용되어 **Postman 응답 ↔ WAS 로그 대조**가 가능하다.

**오류코드** — 없음

**판정 기준**
- `X-Request-ID` 미전송 → 응답 `requestId`가 `REQ-` 로 시작
- `X-Request-ID: REQ-20260714-0001` 전송 → 응답 `requestId`가 **동일 값**

**⚠️ 실구현 시 조치** — `IdGenerator`는 WAS 단일 인스턴스 기준 In-Memory 채번이다. 다중 WAS 환경에서는 `server_instance` 포함 채번 규칙으로 교체 필요.

---

### BSL-F-08 · 호출 Client 검증

| 항목 | 내용 |
|---|---|
| **목적** | 등록된 호출 채널(MCP/Portal/Teams)인지 확인한다 (설계서 6.1 - 1단계) |
| **담당 파일** | `core/security/ClientAuthenticationService.java` (Interface)<br>`core/security/DummyClientAuthenticationService.java` (구현) |
| **메서드** | `authenticate(ServiceContext context)` (`Executor:100`) |

**입력 데이터** — `ServiceContext.clientId` (Header `X-Client-ID` 유래), `ServiceContext.authType`

**처리 로직 (현재 / Mock)**
```java
if (!StringUtils.hasText(context.getClientId())) {
    throw new AuthenticationException("필수 Header 인 X-Client-ID 가 없습니다.");
}
// 그 외에는 무조건 통과 + 콘솔 로그 [BSL-AUTHN]
```

**출력** — 없음(통과) 또는 예외

**오류코드**

| 코드 | HTTP | 발생 조건 | 현재 |
|---|:---:|---|---|
| BS-AUTH-001 | 401 | `X-Client-ID` Header 누락 | ✅ 동작 |
| BS-AUTH-002 | 401 | Token 만료 | `__simulate=UNAUTHORIZED` 로만 발생 |

**판정 기준** — `X-Client-ID` 헤더 제거 후 호출 시 **401 / BS-AUTH-001**

**⚠️ 실구현 시 조치** — `BS_CLIENT` · `BS_CLIENT_SERVICE` 조회, SSO Token 서명·만료 검증, 허용 IP 확인(설계서 3.3) 추가

---

### BSL-F-09 · SSO 사용자 Context 추출

| 항목 | 내용 |
|---|---|
| **목적** | 최종 권한판단 기준이 되는 사용자 신원을 확보한다 (설계서 1.2) |
| **담당 파일** | `core/context/HttpHeaderRequestContextResolver.java`<br>`core/context/ServiceContext.java` |
| **메서드** | `resolve(...)` 내부 (`Executor:97` 에서 BSL-F-07과 동시 수행) |

**출력 데이터 (ServiceContext 필드)**

| 필드 | 현재 값(Mock) | 실구현 시 출처 |
|---|---|---|
| `userId` | Header `X-User-ID` → 없으면 `E00000` | SSO Token Claim |
| `departmentCode` | `IT001` 고정 | SSO / IFRS17 사용자 테이블 |
| `roles` | `[IFRS17_USER, IFRS17_CLOSING_VIEW]` 고정 | IFRS17 권한 Service |
| `authType` | `Authorization` 有 → `BEARER` / 無 → `NONE` | Token 유형 |
| `remoteIp` | `X-Forwarded-For` 첫 값 → 없으면 `getRemoteAddr()` | 동일 |
| `locale` | `options.locale` → 기본 `ko-KR` | 동일 |

**오류코드** — BS-AUTH-001 (실구현 시 SSO 검증 실패)

**판정 기준** — 응답 로그 `[BSL-AUDIT-START]` 에 `userId`, `deptCode`, `roles` 가 출력됨

**⚠️ 실구현 시 조치** — 하드코딩 상수 `MOCK_USER_ID` / `MOCK_DEPARTMENT_CODE` / `MOCK_ROLES` 3개 삭제

---

### BSL-F-10 · Service Catalog에서 활성 버전 조회

| 항목 | 내용 |
|---|---|
| **목적** | 등록·승인·활성 상태의 서비스만 호출을 허용한다 (설계서 8.4 - 6·7단계) |
| **담당 파일** | `core/metadata/ServiceMetadataRepository.java` (Interface)<br>`core/metadata/InMemoryServiceMetadataRepository.java` (구현)<br>`core/metadata/ServiceMetadata.java` (모델) |
| **메서드** | `findActive(String serviceId, String version)` (`Executor:103`) |

**입력** — `serviceId`(Path), `request.serviceVersion`(Body, nullable)

**처리 로직**
```java
metadata = catalog.get(serviceId);
if (metadata == null || !metadata.isActive())              → null 반환
if (version 입력됨 && version != metadata.getVersion())     → null 반환
null 이면 Executor 가 ServiceNotFoundException 발생 (:104)
```

**출력 데이터 (ServiceMetadata)**

| 필드 | 용도 | 소비 단계 |
|---|---|---|
| `version` | `context.serviceVersion` 확정 (`:108`) | F-18 응답 |
| `requiredRoles` | 권한 대조 기준 | F-11 |
| `sensitivePolicy` | 마스킹 정책 코드 | F-16 |
| `sourceSystem` | 응답 `sourceSystem` | F-18 |
| `timeoutMs` | Timeout 기준 (현재 미적용) | F-15 |
| `implementationBean` | Console Bean 검증 | BSL-A-04 |
| `legacyBatchProgramId`<br>`legacyScreenName` | **[Draft]** 미확정 임시값 | F-15 |

**등록 데이터 (현재 6건)** — 파일럿 5종 + `IFRS17.SAMPLE.DISABLED`(비활성, 테스트 전용)

**오류코드**

| 코드 | HTTP | 발생 조건 |
|---|:---:|---|
| BS-SVC-404 | 404 | 미등록 serviceId / 비활성 서비스 / 활성버전 불일치 |

**판정 기준**
- `IFRS17.SAMPLE.DISABLED` 호출 → 404 / BS-SVC-404
- `serviceVersion: "9.9"` 전송 → 404 / BS-SVC-404

**⚠️ 실구현 시 조치** — `business_service.bs_service` + `bs_service_version` 조회로 대체 (`persistence/mapper/BsServiceMapper.java` 계약 사용)

---

### BSL-F-11 · 서비스/역할 권한 확인

| 항목 | 내용 |
|---|---|
| **목적** | 서비스 단위 + 업무 파라미터 단위 권한을 판정한다 (설계서 6.1 - 4·5단계) |
| **담당 파일** | `core/security/AuthorizationService.java` (Interface)<br>`core/security/DummyAuthorizationService.java` (구현)<br>`core/workflow/BusinessServiceHandler.java` → `authorize()` |
| **메서드** | ① `authorize(ServiceContext, ServiceMetadata)` (`Executor:111`) — 공통 역할 검사<br>② `handler.authorize(ServiceContext, REQ)` (`Executor:123`) — 업무 파라미터 기반 추가 검사 |

**입력 데이터** — `context.roles` (사용자 역할) × `metadata.requiredRoles` (서비스 요구 역할)

**처리 로직 (현재 / Mock)** — **무조건 통과(Always ALLOW)** 후 `[BSL-AUTHZ]` 콘솔 출력. 실구현 로직은 주석으로 명시되어 있다.

**오류코드**

| 코드 | HTTP | 발생 조건 | 현재 |
|---|:---:|---|---|
| BS-AUTH-003 | 403 | 역할 불일치 / 업무 데이터 접근 권한 없음 | `__simulate=FORBIDDEN` 로만 발생 |

**판정 기준** — `parameters.__simulate = "FORBIDDEN"` 전송 시 **403 / BS-AUTH-003** + 감사로그 `[BSL-AUDIT-FAIL]` 기록

**⚠️ 실구현 시 조치** — ① `BS_SERVICE_ROLE` 조회 ② 교집합 검사 ③ 불일치 시 `AuthorizationException`. **MCP/AI가 최종 권한을 결정하지 않는다**(설계서 6.2).

---

### BSL-F-12 · 입력 Schema 및 업무 파라미터 검증

| 항목 | 내용 |
|---|---|
| **목적** | Envelope 규격과 업무 파라미터를 검증하고 타입 DTO로 바인딩한다 |
| **담당 파일** | `core/validator/StandardRequestValidator.java`<br>`core/validator/ParameterBinder.java`<br>`core/validator/ValidationUtils.java`<br>`domain/*/Dummy*StatusHandler.java` → `validate()` |

**세부 기능**

| 기능ID | 처리 | 메서드 | 호출 |
|---|---|---|:---:|
| BSL-F-12-1 | Envelope 검증 (`parameters` 비어있는지) | `StandardRequestValidator.validate(request, metadata)` | `:115` |
| BSL-F-12-2 | Map → 타입 DTO 바인딩 | `ParameterBinder.bind(Map, Class<T>)` | `:119` |
| BSL-F-12-3 | 업무 파라미터 검증 | `handler.validate(context, domainRequest)` | `:120` |

**데이터 변환 (BSL-F-12-2)**
```
parameters { "closingYearMonth":"2026-06", "closingType":"MONTHLY" }
    ↓ ObjectMapper.convertValue(map, handler.requestType())
ClosingStatusRequest { closingYearMonth="2026-06", closingType="MONTHLY" }
```
> `fail-on-unknown-properties=false` 설정이므로 정의되지 않은 키(`__simulate` 등)는 무시된다.

**검증 규칙 (BSL-F-12-3, 서비스 공통)**

| 항목 | 규칙 | 검증 메서드 |
|---|---|---|
| `closingYearMonth` | 필수, `^\d{4}-(0[1-9]\|1[0-2])$` | `ValidationUtils.requireYearMonth()` |
| 선택 파라미터 | 길이 제한 20~30자 | `ValidationUtils.maxLength()` |

**오류코드**

| 코드 | HTTP | 발생 조건 | 응답 `details[]` |
|---|:---:|---|---|
| BS-VAL-001 | 400 | 필수값 누락, 형식 오류, 바인딩 실패 | `field` + `message` 포함 |
| BS-VAL-002 | 400 | 허용범위 초과 | **현재 미사용** (7장 참조) |

**판정 기준**
- `closingYearMonth: "2026-13"` → 400 / BS-VAL-001 / `details[0].field = "closingYearMonth"`
- `parameters: {}` → 400 / BS-VAL-001 / `details[0].field = "parameters"`

**⚠️ 정합성 참고** — `StandardRequestValidator` 의 `serviceVersion` 불일치 검증은 **도달 불가 코드**이다. BSL-F-10의 `findActive()` 가 먼저 `null`을 반환해 404로 처리되기 때문이다. 실구현 단계에서 정리 대상.

---

### BSL-F-13 · 감사 시작 로그 기록

| 항목 | 내용 |
|---|---|
| **목적** | 호출 사실을 처리 이전에 기록한다 (설계서 6.3) |
| **담당 파일** | `core/audit/AuditLogger.java` (Interface)<br>`core/audit/ConsoleAuditLogger.java` (구현)<br>`core/audit/AuditRecord.java` (모델) |
| **메서드** | `start(ServiceContext, ServiceMetadata, Map parameters)` (`Executor:126`) |

**출력 데이터 (AuditRecord — 설계서 6.3 필수항목)**

| 분류 | 필드 | 현재 값 |
|---|---|---|
| 추적 | `requestId` `traceId` `parentRequestId` | Context 유래 / parent는 미사용 |
| 호출자 | `clientId` `userId` `departmentCode` `roles` | Context 유래 |
| 서비스 | `serviceId` `serviceVersion` `sourceSystem` | Context + Metadata |
| 시간 | `requestedAt` | Context 유래 |
| 보안 | `remoteIp` `authType` `authorizationResult` | Context + `ALLOW` 고정 |
| 데이터 | `parameterHash` `sensitiveAccessFlag` | `hashCode()` 8자리 / `false` 고정 |
| 운영 | `serverInstance` `applicationVersion` | `IFRS17-WAS-SKELETON-01` / `1.0.0-SKELETON` |

**설계서 6.4 준수** — Request 원문은 저장하지 않고 **`parameterHash` 만** 기록한다.

**오류코드** — 없음

**판정 기준** — WAS 콘솔에 `[BSL-AUDIT-START] requestId=... parameterHash=...` 1행 출력

**⚠️ 실구현 시 조치** — `System.out` → `bs_call_log` INSERT (`BsCallLogMapper.insertStart()`). 저장 실패가 업무 응답을 실패시키지 않도록 보조 저장 정책 적용(설계서 7.4).

---

### BSL-F-14 · Business Service Handler 실행

| 항목 | 내용 |
|---|---|
| **목적** | serviceId에 매핑된 도메인 구현체를 찾아 업무 처리를 위임한다 |
| **담당 파일** | `core/dispatcher/BusinessServiceDispatcher.java` (Interface)<br>`core/dispatcher/DefaultBusinessServiceDispatcher.java` (구현)<br>`core/workflow/BusinessServiceHandler.java` (표준 인터페이스)<br>`domain/{closing,journal,expense,statement,csm}/Dummy*StatusHandler.java` |

**세부 기능**

| 기능ID | 처리 | 메서드 | 호출 |
|---|---|---|:---:|
| BSL-F-14-1 | Handler Registry 구축 (기동 시 1회) | `DefaultBusinessServiceDispatcher.initRegistry()` `@PostConstruct` | 기동 |
| BSL-F-14-2 | serviceId → Handler Bean 조회 | `dispatch(ServiceContext, payload)` | `:118` |
| BSL-F-14-3 | 업무 처리 실행 | `handler.process(context, domainRequest)` | `:130` |

**Registry 구축 (BSL-F-14-1)** — Spring Container의 모든 `BusinessServiceHandler` 구현 Bean을 수집하여 `handler.serviceId()` 를 키로 등록. 중복 serviceId 발견 시 **기동 실패**(조기 검출). 기동 로그 `[BSL-REGISTRY]` 5행이 등록 증거이다.

**Handler 표준 인터페이스 (설계서 4.4)**
```java
String serviceId();               // Catalog 키
Class<REQ> requestType();         // BSL-F-12-2 바인딩 대상
void validate(ctx, req);          // BSL-F-12-3
void authorize(ctx, req);         // BSL-F-11
RES process(ctx, req);            // BSL-F-14-3
```

**데이터 흐름 (5종 공통 패턴)**
```
ClosingStatusRequest → adapter.invoke() → ClosingStatusResponse
                     → 결과 비었으면 context.addWarning("BS-DATA-000", ...)
```

**오류코드**

| 코드 | HTTP | 발생 조건 | 처리 위치 |
|---|:---:|---|---|
| BS-SVC-404 | 404 | Catalog에는 있으나 구현 Bean 미배포 | `DefaultBusinessServiceDispatcher.dispatch()` |

**판정 기준** — 5종 서비스 각각 호출 시 응답 `result` 구조가 서비스별로 상이하게 반환됨

---

### BSL-F-15 · Legacy Adapter를 통한 기존 Service 호출

| 항목 | 내용 |
|---|---|
| **목적** | 기존 IFRS17 Service를 재사용하여 업무 데이터를 조회한다 (설계서 2.1) |
| **담당 파일** | `legacy/adapter/LegacyAdapter.java` (Interface)<br>`legacy/adapter/Dummy{Closing,Journal,Expense,Statement,Csm}StatusLegacyAdapter.java`<br>`legacy/adapter/MockDataPolicy.java` (Skeleton 전용) |
| **메서드** | `invoke(REQ domainRequest)` — Handler의 `process()` 내부에서 호출<br>`legacyBatchProgramId()` / `legacyScreenName()` |

**Mock 데이터 정책 (현재)**

| 기준년월 | 반환 |
|---|---|
| `2026-06`, `2026-05` | 하드코딩 업무 데이터 |
| 그 외 유효한 `YYYY-MM` | **빈 결과** + `BS-DATA-000` 경고 |

**서비스별 반환 데이터 구조**

| Service ID | 응답 핵심 필드 | 건수 |
|---|---|:---:|
| IFRS17.CLOSING.STATUS | `stages[]` (stageCode·status·startedAt·endedAt·progressRate) | 6 |
| IFRS17.JOURNAL.STATUS | `journals[]` (createdCount·postedCount·errorCount) | 4 |
| IFRS17.EXPENSE.STATUS | `expenses[]` (loadStatus·validationStatus·allocationStatus) | 3 |
| IFRS17.STATEMENT.STATUS | `statements[]` (status·reportVersion·generatedAt) | 3 |
| IFRS17.CSM.STATUS | `portfolios[]` (status·contractCount·errorCount) | 3 |

**[Draft] 미확정 데이터** — 각 Adapter의 `BATCH_PROGRAM_ID`, `SCREEN_NAME` 상수는 `TBD-...` 임시값이며 응답 `result.legacyBinding` 에 `confirmed:false` 로 노출된다. **설계서 14장 No.1 현행 매핑서(D+5)** 확정 시 교체한다.

**오류코드**

| 코드 | HTTP | 발생 조건 | 현재 |
|---|:---:|---|---|
| BS-LEG-500 | 500 | 기존 Service 예외 | `__simulate=LEGACY_ERROR` 로만 발생 |
| BS-SYS-504 | 504 | 처리시간 초과 (기본 30,000ms) | `__simulate=TIMEOUT` 로만 발생 |

**판정 기준**
- `2026-06` 조회 → 서비스별 하드코딩 데이터 반환
- `2020-01` 조회 → 200 / 빈 배열 / `warnings[0].code = "BS-DATA-000"`

**⚠️ 실구현 시 조치** — 각 Adapter의 `invoke()` 전면 교체 (기존 Service 주입 + Mapper 변환 + try-catch 예외 변환). `MockDataPolicy.java` 삭제.

---

### BSL-F-16 · 결과 DTO를 표준 JSON으로 변환 및 마스킹

| 항목 | 내용 |
|---|---|
| **목적** | 민감정보를 정책에 따라 마스킹한다 (설계서 6.4) |
| **담당 파일** | `core/security/MaskingPolicy.java` (Interface)<br>`core/security/DefaultMaskingPolicy.java` (구현) |
| **메서드** | `mask(T result, String policy)` (`Executor:133`) |

**입력** — `handler.process()` 결과 객체 + `metadata.sensitivePolicy` (현재 전 서비스 `DEFAULT_MASKING`)

**처리 로직 (현재 / Mock)** — 파일럿 5종은 상태·건수 중심 조회로 민감정보가 없어 **pass-through**

**JSON 직렬화 규칙** — Jackson

| 설정 | 값 | 효과 |
|---|---|---|
| `write-dates-as-timestamps` | false | `processedAt` ISO-8601 문자열 |
| `time-zone` | Asia/Seoul | `+09:00` 오프셋 |
| `@JsonInclude(NON_NULL)` | StandardResponse | 미해당 필드 제외 |

**오류코드** — 없음 (BS-DATA-000은 F-15에서 warning으로 등록됨)

**판정 기준** — 응답 JSON에 주민번호·계좌번호 형식 문자열이 없음

**⚠️ 실구현 시 조치** — Action 서비스나 계약자 정보 조회 서비스 추가 시 필드 단위 마스킹 구현 필수

---

### BSL-F-17 · 감사 성공/실패 로그 기록

| 항목 | 내용 |
|---|---|
| **목적** | 처리 결과와 소요시간을 기록해 Request ID로 추적 가능하게 한다 (설계서 13.3) |
| **담당 파일** | `core/audit/ConsoleAuditLogger.java` |
| **메서드** | `success(AuditRecord, long elapsedMs, Integer resultCount)` (`Executor:138`)<br>`fail(AuditRecord, long elapsedMs, int httpStatus, String errorCode, String errorId, String message)` (`Executor:174`) |

**출력 데이터**

| 필드 | 성공 시 | 실패 시 |
|---|---|---|
| `status` | `SUCCESS` | `ERROR` |
| `httpStatus` | 200 | 400/401/403/404/500/504 |
| `elapsedMs` | `System.currentTimeMillis()` 차이 | 동일 |
| `resultCount` | `StatusServiceResponse.getResultCount()` | null |
| `errorCode` / `errorId` | null | 부록 A 코드 / `ERR-yyyyMMdd-00001` |
| `completedAt` | `LocalDateTime.now()` | 동일 |

**실패 경로 보정** — 감사 시작(F-13) 이전에 예외가 발생한 경우(예: BS-AUTH-001), `handleFailure()` 가 `auditLogger.start()` 를 먼저 호출해 **누락 없이 실패 로그를 남긴다**(`Executor:171`).

**오류코드** — 없음

**판정 기준** — 모든 시나리오(정상·오류)에서 `[BSL-AUDIT-SUCCESS]` 또는 `[BSL-AUDIT-FAIL]` 이 **1행 반드시** 출력됨 (설계서 11.2 감사추적)

---

### BSL-F-18 · 표준 Response 반환

| 항목 | 내용 |
|---|---|
| **목적** | 채널 독립적인 표준 Envelope으로 응답한다 (설계서 5.4 / 5.5) |
| **담당 파일** | `core/response/StandardResponseBuilder.java` (Interface)<br>`core/response/DefaultStandardResponseBuilder.java` (구현)<br>`api/dto/response/StandardResponse.java` 외 4종 |
| **메서드** | `success(ServiceContext, ServiceMetadata, T result, long elapsedMs)` (`Executor:141`)<br>`error(ServiceContext, ErrorCode, String message, String errorId, List<ErrorDetail>, long elapsedMs)` (`Executor:181`) |

**성공 응답 필드 매핑 (설계서 5.4)**

| 필드 | 데이터 출처 |
|---|---|
| `requestId` `traceId` `serviceId` | `ServiceContext` |
| `serviceVersion` `sourceSystem` | `ServiceMetadata` |
| `status` | `ResponseStatus.SUCCESS` 고정 |
| `processedAt` | `OffsetDateTime.now(Asia/Seoul)` 초 단위 절삭 |
| `elapsedMs` | Executor 측정값 |
| `result` | 마스킹된 도메인 응답 객체 |
| `warnings[]` | `ServiceContext.warnings` (BS-DATA-000 등) |

**오류 응답 필드 매핑 (설계서 5.5)**

| 필드 | 데이터 출처 |
|---|---|
| `status` | `ResponseStatus.ERROR` |
| `error.code` | `ErrorCode.code()` (부록 A) |
| `error.message` | 예외 메시지. **단 BS-SYS-500은 내부 상세를 숨기고 기본 메시지 사용** (`Executor:177`) |
| `error.errorId` | `IdGenerator.newErrorId()` → `ERR-yyyyMMdd-00001` |
| `error.details[]` | `BusinessServiceException.getDetails()` (필드 단위 오류) |

**HTTP Status 결정** — `ErrorCode.httpStatus()` 값을 그대로 사용

**오류코드**

| 코드 | HTTP | 발생 조건 | 처리 위치 |
|---|:---:|---|---|
| BS-SYS-500 | 500 | 미분류 예외 (NullPointer 등) | `Executor:148` `catch (Exception e)` |

**설계서 6.4 준수** — Stack Trace는 `System.out`(서버 로그)에만 출력하고 외부 응답에는 `errorId`만 노출

**판정 기준** — 성공/오류 모든 응답에 `requestId`, `processedAt`, `elapsedMs` 존재

---

## 6. 오류코드 역매핑 (부록 A)

| 코드 | HTTP | 발생 기능 | 발생 클래스 · 라인 | 통합테스트 |
|---|:---:|---|---|---|
| **BS-VAL-001** | 400 | BSL-F-06<br>BSL-F-12 | `BusinessServiceExceptionHandler:44` (JSON 파싱)<br>`ValidationException:11` ← `ValidationUtils` / `ParameterBinder` / `StandardRequestValidator` | TC-12-1<br>TC-12-2 |
| **BS-VAL-002** | 400 | BSL-F-12 | **발생 지점 없음** (7장 참조) | — |
| **BS-AUTH-001** | 401 | BSL-F-08<br>BSL-F-09 | `DummyClientAuthenticationService` → `AuthenticationException:11` | TC-08-1 |
| **BS-AUTH-002** | 401 | BSL-F-08 | `SkeletonFailureSimulator:47` (시뮬레이션 전용) | TC-08-2 |
| **BS-AUTH-003** | 403 | BSL-F-11 | `AuthorizationException:11` ← `SkeletonFailureSimulator` | TC-11-1 |
| **BS-SVC-404** | 404 | BSL-F-06<br>BSL-F-10<br>BSL-F-14 | `BusinessServiceExceptionHandler:50` (미정의 URI)<br>`DefaultBusinessServiceExecutor:104` (Catalog)<br>`DefaultBusinessServiceDispatcher` (Bean 없음)<br>`ServiceSpecificationConsoleController:81,124` (Console) | TC-10-1<br>TC-10-2<br>TC-10-3 |
| **BS-DATA-000** | 200 | BSL-F-15 | `MockDataPolicy.NO_DATA_CODE` → `context.addWarning()`<br>**예외가 아닌 `warnings[]` 로 반환** | TC-15-2 |
| **BS-LEG-500** | 500 | BSL-F-15 | `LegacyServiceException:11` ← `SkeletonFailureSimulator` | TC-15-3 |
| **BS-SYS-500** | 500 | BSL-F-18 | `DefaultBusinessServiceExecutor:150` `catch (Exception)`<br>`BusinessServiceExceptionHandler:57` | TC-18-1 |
| **BS-SYS-503** | 503 | — | **발생 지점 없음** (7장 참조) | — |
| **BS-SYS-504** | 504 | BSL-F-15 | `ServiceTimeoutException:11` ← `SkeletonFailureSimulator` | TC-15-4 |

**정의 위치** — `core/exception/ErrorCode.java` (11종 전량 enum 정의 완료)

---

## 7. 미구현 오류코드 (개발 관리 항목)

| 코드 | 설계서 정의 | 현재 상태 | 조치 계획 |
|---|---|---|---|
| **BS-VAL-002** | 허용범위 초과 (400) | enum 정의만 존재.<br>`ValidationUtils.maxLength()` 가 BS-VAL-001로 반환 중 | 길이·범위 초과는 BS-VAL-002로 분리 |
| **BS-SYS-503** | 일시적 사용불가 (503) | enum 정의만 존재 | Circuit Breaker / 점검모드 도입 시 적용 |

---

## 8. 통합테스트 케이스

**공통 전제** — 애플리케이션 기동, Header `Content-Type: application/json`, `X-Client-ID: TEST-CLIENT`
**공통 URI** — `POST http://{host}:8080/api/business-services/v1/{serviceId}:execute`

| TC-ID | 대상 기능 | 시나리오 | 요청 `parameters` | 기대 HTTP | 기대 코드 | 판정 기준 |
|---|---|---|---|:---:|---|---|
| TC-06-1 | F-06 | 정상 호출 | `{"closingYearMonth":"2026-06"}` | 200 | — | `status=SUCCESS` |
| TC-06-2 | F-06 | JSON 문법 오류 | `{closingYearMonth:` | 400 | BS-VAL-001 | — |
| TC-06-3 | F-06 | 미정의 URI | — | 404 | BS-SVC-404 | — |
| TC-07-1 | F-07 | Request ID 서버 생성 | 정상 | 200 | — | `requestId` 가 `REQ-` 시작 |
| TC-07-2 | F-07 | Request ID 전달 | Header `X-Request-ID: REQ-20260714-0001` | 200 | — | 응답 `requestId` 동일 |
| TC-08-1 | F-08 | Client ID 누락 | Header 제거 | 401 | BS-AUTH-001 | — |
| TC-08-2 | F-08 | Token 만료 | `__simulate:"UNAUTHORIZED"` | 401 | BS-AUTH-002 | — |
| TC-09-1 | F-09 | 사용자 Context | Header `X-User-ID: E12345` | 200 | — | 감사로그 `userId=E12345` |
| TC-10-1 | F-10 | 미등록 서비스 | serviceId=`IFRS17.UNKNOWN.SERVICE` | 404 | BS-SVC-404 | — |
| TC-10-2 | F-10 | 비활성 서비스 | serviceId=`IFRS17.SAMPLE.DISABLED` | 404 | BS-SVC-404 | — |
| TC-10-3 | F-10 | 버전 불일치 | `serviceVersion:"9.9"` | 404 | BS-SVC-404 | — |
| TC-11-1 | F-11 | 권한 없음 | `__simulate:"FORBIDDEN"` | 403 | BS-AUTH-003 | 감사로그 기록 |
| TC-12-1 | F-12 | 기준년월 형식 오류 | `{"closingYearMonth":"2026-13"}` | 400 | BS-VAL-001 | `details[0].field` |
| TC-12-2 | F-12 | 파라미터 누락 | `{}` | 400 | BS-VAL-001 | `details[0].field=parameters` |
| TC-13-1 | F-13 | 감사 시작 로그 | 정상 | 200 | — | `[BSL-AUDIT-START]` 출력 |
| TC-14-1 | F-14 | 파일럿 5종 | 5개 serviceId 순차 | 200 | — | 서비스별 result 구조 상이 |
| TC-15-1 | F-15 | 데이터 존재 | `{"closingYearMonth":"2026-06"}` | 200 | — | `stages` 6건 |
| TC-15-2 | F-15 | 데이터 없음 | `{"closingYearMonth":"2020-01"}` | 200 | BS-DATA-000 | 빈 배열 + `warnings[0]` |
| TC-15-3 | F-15 | Legacy 오류 | `__simulate:"LEGACY_ERROR"` | 500 | BS-LEG-500 | `errorId` 존재 |
| TC-15-4 | F-15 | Timeout | `__simulate:"TIMEOUT"` | 504 | BS-SYS-504 | — |
| TC-15-5 | F-15 | 선택 파라미터 필터 | `{"closingYearMonth":"2026-06","journalType":"CSM_AMORTIZATION"}` | 200 | — | 결과 1건 |
| TC-16-1 | F-16 | 마스킹 | 정상 | 200 | — | 민감정보 미노출 |
| TC-17-1 | F-17 | 감사 종료 로그 | 정상/오류 각각 | — | — | SUCCESS/FAIL 로그 1행 |
| TC-17-2 | F-17 | Request ID 추적 | 정상 | 200 | — | 응답 `requestId` = 로그 `requestId` |
| TC-18-1 | F-18 | 내부 오류 | `__simulate:"SYSTEM_ERROR"` | 500 | BS-SYS-500 | 상세 미노출 + `errorId` |
| TC-18-2 | F-18 | Envelope 규격 | 정상 | 200 | — | 5.4 전 필드 존재 |

**부가 API 테스트**

| TC-ID | 대상 | Method / URI | 기대 |
|---|---|---|:---:|
| TC-A-01 | Catalog 목록 | `GET /api/business-services/v1/catalog` | 200 / 6건 |
| TC-A-02 | Catalog 단건 | `GET /api/business-services/v1/catalog/IFRS17.CLOSING.STATUS` | 200 |
| TC-A-03 | Catalog 미등록 | `GET .../catalog/IFRS17.UNKNOWN` | 404 |
| TC-A-04 | 호출이력 | `GET /api/business-services/v1/calls/{requestId}` | 200 (Draft 안내) |
| TC-A-05 | Console 목록 | `GET /api/business-service-console/v1/services` | 200 |
| TC-A-06 | Console 저장(Bean 없음) | `POST /api/business-service-console/v1/services` | 400 / BS-VAL-001 |
| TC-A-07 | Console 미사용 전환 | `PATCH .../{serviceId}/active?use=false` | 200 → 이후 execute 404 |
| TC-A-08 | Console 서비스 Test | `POST .../{serviceId}/test` | 200 |

---

## 9. 진행 관리 대장

| 기능ID | 기능명 | 설계 | 구현 | 단위<br>테스트 | 통합<br>테스트 | 실구현<br>전환 | 비고 |
|---|---|:---:|:---:|:---:|:---:|:---:|---|
| BSL-F-06 | HTTP 수신 | ✅ | ✅ | ✅ | ✅ | — | 완료 |
| BSL-F-07 | Request ID | ✅ | ✅ | ✅ | ✅ | ⬜ | 다중 WAS 채번 |
| BSL-F-08 | Client 검증 | ✅ | ✅ | ✅ | ✅ | ⬜ | BS_CLIENT 조회 |
| BSL-F-09 | SSO Context | ✅ | ✅ | ✅ | ✅ | ⬜ | SSO 연동 |
| BSL-F-10 | Catalog 조회 | ✅ | ✅ | ✅ | ✅ | ⬜ | DB 전환 |
| BSL-F-11 | 권한 확인 | ✅ | ✅ | ✅ | ✅ | ⬜ | BS_SERVICE_ROLE |
| BSL-F-12 | 입력 검증 | ✅ | ✅ | ✅ | ✅ | ⬜ | BS-VAL-002 분리 |
| BSL-F-13 | 감사 시작 | ✅ | ✅ | ✅ | ✅ | ⬜ | bs_call_log |
| BSL-F-14 | Handler 실행 | ✅ | ✅ | ✅ | ✅ | ⬜ | Bean명 변경 |
| BSL-F-15 | Legacy 호출 | ✅ | ✅ | ✅ | ✅ | ⬜ | **매핑서 확정 선행** |
| BSL-F-16 | 마스킹 | ✅ | ✅ | ✅ | ✅ | ⬜ | 정책 구현 |
| BSL-F-17 | 감사 종료 | ✅ | ✅ | ✅ | ✅ | ⬜ | bs_call_log |
| BSL-F-18 | 표준 Response | ✅ | ✅ | ✅ | ✅ | — | 완료 |

---

## 10. 실구현 전환 시 영향 범위

| 선행 조건 | 영향 기능 | 대상 파일 |
|---|---|---|
| 설계서 14장 No.1<br>**현행 매핑서 확정 (D+5)** | BSL-F-15 | `Dummy*StatusLegacyAdapter` 5종<br>`InMemoryServiceMetadataRepository` |
| SSO 사용자 속성 확정 | BSL-F-09 | `HttpHeaderRequestContextResolver` |
| IFRS17 권한 재사용 방식 확정 | BSL-F-11 | `DummyAuthorizationService` |
| 메타 테이블 Schema 위치 확정 | BSL-F-10, F-13, F-17 | `persistence.mapper` 3종 + 구현체 신규 |
| 감사로그 정책 확정 | BSL-F-13, F-17 | `ConsoleAuditLogger` → `JdbcAuditLogger` |

**공통 삭제 대상** — `core/executor/SkeletonFailureSimulator.java`, `legacy/adapter/MockDataPolicy.java`

> ⚠️ `SkeletonFailureSimulator` 삭제 시 TC-08-2 / TC-11-1 / TC-15-3 / TC-15-4 / TC-18-1 은 **실제 오류 유발 방식으로 재작성**해야 한다.

---

## 부록. 콘솔 로그 ↔ 기능 대응

| 로그 태그 | 기능ID | 출력 클래스 |
|---|---|---|
| `[BSL-REGISTRY]` | BSL-F-14-1 | `DefaultBusinessServiceDispatcher` (기동 시) |
| `[BSL-AUTHN]` | BSL-F-08 | `DummyClientAuthenticationService` |
| `[BSL-AUTHZ]` | BSL-F-11 | `DummyAuthorizationService` |
| `[BSL-AUDIT-START]` | BSL-F-13 | `ConsoleAuditLogger` |
| `[BSL-LEGACY]` | BSL-F-15 | `Dummy*StatusLegacyAdapter` |
| `[BSL-AUDIT-SUCCESS]` | BSL-F-17 | `ConsoleAuditLogger` |
| `[BSL-AUDIT-FAIL]` | BSL-F-17 | `ConsoleAuditLogger` |
| `[BSL-ERROR]` | BSL-F-18 | `DefaultBusinessServiceExecutor` |
| `[BSL-CONSOLE]` | BSL-A-* | Console Controller |

한 요청의 정상 처리 시 출력 순서:
```
[BSL-AUTHN] → [BSL-AUTHZ] → [BSL-AUDIT-START] → [BSL-LEGACY] → [BSL-AUDIT-SUCCESS]
```
이 순서가 어긋나면 설계서 4.3 표준 처리 순서 위반이므로 **통합테스트 불합격**으로 판정한다.
