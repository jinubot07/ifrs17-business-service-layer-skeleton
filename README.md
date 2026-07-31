# IFRS17 Business Service Layer — Skeleton (Branch: `Skeleton_v1.3`)

> IFRS17 시스템 AI 활용 기반 마련 방안 / Business Service Layer
> **설계서 기준: `IFRS17-BSL-SDD-001` v1.3 (Approved for Development)**

DB(SqlMap/MyBatis) 연동 및 세부 비즈니스 로직 작성 **이전 단계**로,
**REST/JSON 기반 Request–Response 통신 체계가 정상 동작하는지 검증**하기 위한 최소 통신 껍데기(Skeleton)이다.

설계서 **4.3 표준 처리 순서**를 Java Interface와 클래스 구조로 그대로 구현하되,
모든 하위 레이어(Auth / Audit / Handler / Legacy Adapter)는 **하드코딩된 Mock 객체**를 반환한다.

---

## 1. 개발 목적과 범위

| 구분 | 내용 |
|---|---|
| 목적 | REST/JSON 표준 Request–Response 통신 체계 검증 (DB 없이 HTTP 200 성공 응답 확인) |
| 검증 방법 | 화면 개발 전 단계이므로 **Postman 등 REST 클라이언트**로 Request/Response 성공 여부 판단 |
| 포함 | 공통 Framework(Controller·Resolver·Dispatcher·Executor·Audit·Response Builder), 파일럿 5종 Dummy Handler, 표준 오류코드, Console 백엔드 API |
| **제외** | **SqlMap.xml / Mapper.xml 등 XML 및 DB 연결 파일 (완전 배제)**, 실제 Legacy Service 연계, SSO 실연동, Console 화면(UI) |

### 개발 원칙

1. **Java 코드 단위 구현** — XML/DB 연결 파일을 작성하지 않는다.
2. **Interface 기반 모듈화** — 핵심 컴포넌트를 Interface로 분리하여 향후 실제 로직/Legacy 연계 시 구현체만 교체한다.
3. **Mocking** — Service Handler, Legacy Adapter, Auth, Audit 등 하위 레이어는 Dummy 객체를 반환한다.
4. **JSON 표준 규격 준수** — 설계서 5.3~5.5의 표준 Request / Success / Error Response 포맷을 유지한다.

---

## 2. 기술 스택

| 항목 | 버전 | 근거 |
|---|---|---|
| Java | **1.8** (source/target) | 설계서 2장 현행 환경 (Java 8) |
| Spring Boot | 2.7.18 (Spring Framework 5.3.x) | Java 8 지원 마지막 계열 |
| Build | Maven 3.6+ | — |
| DB | **없음** | Skeleton 단계 — 의존성 자체를 포함하지 않음 |
| JSON | Jackson (spring-boot-starter-web 내장) | 설계서 5.3~5.5 |

> 빌드 JDK는 8 이상이면 되고(`--release 8` 호환), 운영 WAS는 Java 8 기준으로 배포 가능하다.

---

## 3. 빠른 시작 (Quick Start)

```bash
# 1) 소스 받기
git clone <REPO_URL>
cd ifrs17-business-service-layer-skeleton
git checkout Skeleton_v1.3

# 2) 빌드 + 단위/통합 테스트 (22건)
mvn clean test

# 3) 실행 (둘 중 하나)
mvn spring-boot:run
#  또는
mvn clean package -DskipTests
java -Dfile.encoding=UTF-8 -jar target/ifrs17-business-service-layer-skeleton.jar
```

기동 확인 로그:

```
[BSL-REGISTRY] serviceId=IFRS17.CLOSING.STATUS, bean=DummyClosingStatusHandler
[BSL-REGISTRY] serviceId=IFRS17.JOURNAL.STATUS, bean=DummyJournalStatusHandler
...
Tomcat started on port(s): 8080 (http)
```

기본 포트는 `8080`이며, 변경은 `src/main/resources/application.yml` 의 `server.port` 또는
`java -jar ... --server.port=9090` 으로 한다.

---

## 4. 최종 검증 방법 (Definition of Done)

### 4.1 검증 요청 (Postman 등 REST 클라이언트)

| 항목 | 값 |
|---|---|
| Method | `POST` |
| URL | `http://localhost:8080/api/business-services/v1/IFRS17.CLOSING.STATUS:execute` |
| Header | `Content-Type: application/json`<br>`X-Client-ID: TEST-CLIENT` |
| Body | `{"serviceVersion": "1.0", "parameters": {"closingYearMonth": "2026-06"}}` |

> URL 끝의 `:execute` 는 설계서 5.1 URI 규칙(`POST /api/business-services/v1/{serviceId}:execute`)이므로
> 반드시 포함해야 한다.

### 4.2 기대 응답 (HTTP 200) — 실제 확인된 응답

```json
{
  "requestId": "REQ-20260729-0015",
  "traceId": "TRACE-20260729-0015",
  "serviceId": "IFRS17.CLOSING.STATUS",
  "serviceVersion": "1.0",
  "sourceSystem": "IFRS17",
  "status": "SUCCESS",
  "processedAt": "2026-07-30T02:49:37+09:00",
  "elapsedMs": 1,
  "result": {
    "closingYearMonth": "2026-06",
    "overallStatus": "RUNNING",
    "resultCount": 6,
    "errorCount": 0,
    "baseDateTime": "2026-07-01T21:00:00+09:00",
    "legacyBinding": {
      "batchProgramId": "TBD-BAT-CLOSING-STATUS-001",
      "screenName": "TBD-결산진행현황조회",
      "confirmed": false,
      "note": "설계서 14장 No.1 현행 매핑서 확정 전 임시값(Draft)"
    },
    "closingType": "MONTHLY",
    "progressRate": 60.4,
    "stages": [
      { "stageCode": "CL010", "stageName": "결산기초자료 적재", "status": "COMPLETED",
        "startedAt": "2026-07-01T19:00:00+09:00", "endedAt": "2026-07-01T19:24:31+09:00",
        "progressRate": 100.0, "errorCount": 0 },
      { "stageCode": "CL040", "stageName": "CSM 산출", "status": "RUNNING",
        "startedAt": "2026-07-01T20:45:00+09:00", "endedAt": null,
        "progressRate": 62.5, "errorCount": 0 }
    ]
  },
  "warnings": []
}
```

**DB 연결 없이 위 JSON이 HTTP 200으로 도착하면 껍데기 구축 성공으로 판단한다.**

### 4.3 WAS 콘솔에 출력되는 감사 로그

```
[BSL-AUTHN]         requestId=..., clientId=TEST-CLIENT, result=AUTHENTICATED(Mock)
[BSL-AUTHZ]         requestId=..., requiredRoles=[...], result=ALLOW(Mock)
[BSL-AUDIT-START]   requestId=..., traceId=..., serviceId=..., parameterHash=..., requestedAt=...
[BSL-LEGACY]        adapter=DummyClosingStatusLegacyAdapter, batchProgramId=TBD-..., (Mock 호출)
[BSL-AUDIT-SUCCESS] requestId=..., status=SUCCESS, httpStatus=200, resultCount=6, elapsedMs=1
```

---

## 5. 테스트 방법 3가지

### (1) 수동 호출 시나리오 (Postman 등 REST 클라이언트)

모든 요청은 공통으로 아래 Header 를 사용한다. (별도 Collection 파일은 관리하지 않으며, 아래 표를 보고 직접 입력한다.)

```
Content-Type: application/json
X-Client-ID:  TEST-CLIENT
```

공통 URL 접두어: `http://localhost:8080/api/business-services/v1`

| No | 시나리오 | Method / URI | Body(`parameters`) | 기대 결과 |
|---|---|---|---|---|
| 1 | **[DoD]** 결산 진행상태 조회 | `POST /IFRS17.CLOSING.STATUS:execute` | `{"closingYearMonth":"2026-06"}` | 200 / `status=SUCCESS` |
| 2 | 전표 생성·반영 상태 조회 | `POST /IFRS17.JOURNAL.STATUS:execute` | `{"closingYearMonth":"2026-06"}` | 200 / `status=SUCCESS` |
| 3 | 사업비 처리 상태 조회 | `POST /IFRS17.EXPENSE.STATUS:execute` | `{"closingYearMonth":"2026-06"}` | 200 / `status=SUCCESS` |
| 4 | 재무제표 산출 상태 조회 | `POST /IFRS17.STATEMENT.STATUS:execute` | `{"closingYearMonth":"2026-06"}` | 200 / `status=SUCCESS` |
| 5 | CSM 산출 상태 조회 | `POST /IFRS17.CSM.STATUS:execute` | `{"closingYearMonth":"2026-06"}` | 200 / `status=SUCCESS` |
| 6 | 데이터 없음 | `POST /IFRS17.CLOSING.STATUS:execute` | `{"closingYearMonth":"2020-01"}` | 200 / 빈 결과 + `warnings[0].code=BS-DATA-000` |
| 7 | 입력 오류 | `POST /IFRS17.CLOSING.STATUS:execute` | `{"closingYearMonth":"2026-13"}` | 400 / `BS-VAL-001` |
| 8 | 인증 실패 (`X-Client-ID` Header 제거) | `POST /IFRS17.CLOSING.STATUS:execute` | `{"closingYearMonth":"2026-06"}` | 401 / `BS-AUTH-001` |
| 9 | 권한 없음 (시뮬레이션) | `POST /IFRS17.CLOSING.STATUS:execute` | `{"closingYearMonth":"2026-06","__simulate":"FORBIDDEN"}` | 403 / `BS-AUTH-003` |
| 10 | 서비스 비활성 | `POST /IFRS17.SAMPLE.DISABLED:execute` | `{"closingYearMonth":"2026-06"}` | 404 / `BS-SVC-404` |
| 11 | Timeout (시뮬레이션) | `POST /IFRS17.CLOSING.STATUS:execute` | `{"closingYearMonth":"2026-06","__simulate":"TIMEOUT"}` | 504 / `BS-SYS-504` |
| 12 | Legacy 오류 (시뮬레이션) | `POST /IFRS17.CLOSING.STATUS:execute` | `{"closingYearMonth":"2026-06","__simulate":"LEGACY_ERROR"}` | 500 / `BS-LEG-500` |
| 13 | 서비스 Catalog 목록 | `GET /catalog` | — | 200 |
| 14 | 서비스 Catalog 단건 | `GET /catalog/IFRS17.CLOSING.STATUS` | — | 200 |
| 15 | 호출 이력 조회 (Draft) | `GET /calls/REQ-20260714-0001` | — | 200 (콘솔 로그 안내) |

POST 요청 Body 전체 형태는 다음과 같다.

```json
{
  "serviceVersion": "1.0",
  "parameters": { "closingYearMonth": "2026-06" }
}
```

### (2) curl 스모크 테스트 스크립트

애플리케이션 기동 후:

```bash
./scripts/smoke-test.sh                      # 기본 http://localhost:8080
./scripts/smoke-test.sh http://10.x.x.x:8080 # 원격 WAS 지정
```

13개 시나리오를 순차 호출하고 `PASS/FAIL` 집계를 출력한다.

### (3) 자동화 테스트 (MockMvc)

```bash
mvn test
# Tests run: 22, Failures: 0, Errors: 0, Skipped: 0
```

| 테스트 클래스 | 건수 | 검증 범위 |
|---|---|---|
| `BusinessServiceControllerTest` | 14 | DoD 시나리오 및 설계서 11.2 필수 인수 시나리오 |
| `ServiceConsoleControllerTest` | 8 | 설계서 8.7 Console 인수 기준 (CON-ACC-01 ~ CON-ACC-05) |

### 오류 시나리오 시뮬레이션 (`__simulate`)

DB·Legacy 미연동 상태에서도 표준 Error Response 규격을 검증할 수 있도록, 요청 `parameters`에
`__simulate` 값을 넣으면 해당 예외가 발생한다.

| `__simulate` | HTTP | 오류코드 |
|---|---|---|
| `UNAUTHORIZED` | 401 | `BS-AUTH-002` |
| `FORBIDDEN` | 403 | `BS-AUTH-003` |
| `TIMEOUT` | 504 | `BS-SYS-504` |
| `LEGACY_ERROR` | 500 | `BS-LEG-500` |
| `SYSTEM_ERROR` | 500 | `BS-SYS-500` |

> `SkeletonFailureSimulator` 는 **Skeleton 전용**이며 실제 구현 단계에서 반드시 제거한다.

### Mock 데이터 보유 기준년월

`2026-06`, `2026-05` → 데이터 있음 / 그 외 유효한 `YYYY-MM` → **SUCCESS + 빈 결과 + `BS-DATA-000` 경고**
(설계서 9장 "데이터 없음은 오류가 아니라 SUCCESS + 빈 결과" 규칙 검증용)

---

## 6. 설계서 4.3 표준 처리 순서 ↔ 구현 클래스 매핑

```
[클라이언트 JSON POST 요청]
        │
        ▼
 (1)(2) BusinessServiceController + HttpHeaderRequestContextResolver  ← Header/Body 수신, Request ID 생성, 가짜 Context 생성
 (3)    DummyClientAuthenticationService                              ← 호출 Client 검증
 (4)    HttpHeaderRequestContextResolver                              ← SSO 사용자 Context 추출 (Mock 사용자)
 (5)    InMemoryServiceMetadataRepository.findActive()                ← Catalog 활성 버전 조회
 (6)    DummyAuthorizationService.authorize()                         ← 권한 검사 (Always True)
 (7)    StandardRequestValidator + ParameterBinder + Handler.validate ← 입력 Schema/업무 파라미터 검증
 (8)    ConsoleAuditLogger.start()                                    ← 감사 시작 로그 (System.out)
 (9)    BusinessServiceDispatcher → DummyXxxStatusHandler.process()   ← Mock 결과 객체 반환
 (10)   DummyXxxStatusLegacyAdapter.invoke()                          ← Legacy 호출 (하드코딩)
 (11)   DefaultMaskingPolicy.mask()                                   ← 마스킹 (pass-through)
 (12)   ConsoleAuditLogger.success() / fail()                         ← 감사 종료 로그
 (13)   DefaultStandardResponseBuilder.success() / error()            ← 표준 JSON Envelope, HTTP 200
        │
        ▼
[클라이언트 표준 JSON 응답]
```

전체 흐름 제어(시간 측정 · 예외 감싸기 · try-catch 템플릿)는 **`DefaultBusinessServiceExecutor`** 가 담당하며,
설계서 10.4 공통 Executor 의사코드를 그대로 따른다.

---

## 7. 패키지 구조 (설계서 4.1 전수 준수)

설계서 4.1 권장 패키지 구조 **23개 패키지를 그대로 사용**한다.
설계서에 없는 패키지는 만들지 않으며, 공통 DTO·표준 인터페이스도 4.1 목록 안에 배치한다.

```
com.koreanre.ifrs17.businessservice
├── BusinessServiceLayerApplication.java            # Spring Boot 기동 클래스
├── api.controller     : BusinessServiceController, BusinessServiceExceptionHandler
├── api.dto.request    : StandardRequest, RequestOptions, StatusServiceRequest
├── api.dto.response   : StandardResponse, StandardError, ErrorDetail, Warning, ResponseStatus,
│                        StatusServiceResponse, ProcessingStatusCode, LegacyBinding
├── core.context       : ServiceContext, RequestContextResolver(I), HttpHeaderRequestContextResolver, IdGenerator
├── core.dispatcher    : BusinessServiceDispatcher(I), DefaultBusinessServiceDispatcher
├── core.executor      : BusinessServiceExecutor(I), DefaultBusinessServiceExecutor, SkeletonFailureSimulator
├── core.validator     : ValidationUtils, StandardRequestValidator, ParameterBinder
├── core.workflow      : BusinessServiceHandler(I)          # 4.4 표준 인터페이스
├── core.security      : ClientAuthenticationService(I)/Dummy, AuthorizationService(I)/Dummy, MaskingPolicy(I)/Default
├── core.audit         : AuditLogger(I), ConsoleAuditLogger, AuditRecord
├── core.exception     : ErrorCode(enum) 외 8종
├── core.metadata      : ServiceMetadata, ServiceMetadataRepository(I), InMemoryServiceMetadataRepository
├── core.response      : StandardResponseBuilder(I), DefaultStandardResponseBuilder
├── domain.closing     : DummyClosingStatusHandler, ClosingStatusRequest/Response, ClosingStageStatus
├── domain.journal     : DummyJournalStatusHandler, JournalStatusRequest/Response, JournalTypeStatus
├── domain.expense     : DummyExpenseStatusHandler, ExpenseStatusRequest/Response, ExpenseCategoryStatus
├── domain.statement   : DummyStatementStatusHandler, StatementStatusRequest/Response, StatementTypeStatus
├── domain.csm         : DummyCsmStatusHandler, CsmStatusRequest/Response, CsmPortfolioStatus
├── legacy.adapter     : LegacyAdapter(I), Dummy{Closing|Journal|Expense|Statement|Csm}StatusLegacyAdapter, MockDataPolicy
├── console            : ServiceSpecificationConsoleController(CON-01), ServiceTestConsoleController(CON-02),
│                        ServiceSpecificationForm, ServiceSpecificationValidator
├── persistence.mapper : BsServiceMapper, BsServiceVersionMapper, BsCallLogMapper   # Java 인터페이스만
└── persistence.model  : BsService, BsServiceVersion, BsCallLog                     # 7.3 DDL 대응 POJO
```

### 배치 근거

| 클래스 | 배치 패키지 | 근거 |
|---|---|---|
| `BusinessServiceHandler` | `core.workflow` | 설계서 4.2에서 공통 Framework 핵심 컴포넌트로 정의되며, `validate → authorize → process` 가 서비스 단위 처리 Workflow 에 해당 |
| `StatusServiceRequest` | `api.dto.request` | 파일럿 5종 공통 요청 항목(기준년월)의 상위 DTO |
| `StatusServiceResponse`<br>`ProcessingStatusCode`<br>`LegacyBinding` | `api.dto.response` | 응답 Payload 공통 항목 및 부록 D 표준 상태코드 |

### persistence 패키지 처리 기준

설계서 4.1에 명시된 패키지이므로 **생성하되, "XML 및 DB 연결 파일 완전 배제" 원칙을 지킨다.**

| 포함 | 미포함 (DB 연동 단계에서 추가) |
|---|---|
| Java 인터페이스(Mapper 계약) | SqlMap.xml / Mapper.xml |
| 7.3 DDL 대응 POJO | MyBatis `@Mapper` 애노테이션, DataSource 설정 |
| — | Mapper 구현체 Bean 등록 |

현재 Catalog 조회는 `InMemoryServiceMetadataRepository`, 감사로그는 `ConsoleAuditLogger` 가 담당하며,
DB 연동 단계에서 위 Mapper 를 사용하는 구현체로 교체한다.

---

## 8. API 규격 (설계서 5장)

### 8.1 URI 규칙 (5.1)

| Method | URI | 설명 | 구현 상태 |
|---|---|---|---|
| POST | `/api/business-services/v1/{serviceId}:execute` | 서비스 실행 (공통 단일 Endpoint) | 구현 |
| GET | `/api/business-services/v1/catalog` | 서비스 Catalog 목록 | 구현 (In-Memory) |
| GET | `/api/business-services/v1/catalog/{serviceId}` | 서비스 Catalog 단건 | 구현 (In-Memory) |
| GET | `/api/business-services/v1/calls/{requestId}` | 호출 이력 조회 | **Draft** (계약만 제공, 콘솔 로그 안내 반환) |

**Business Service Console (설계서 8장 / URI 미확정 — Draft)**

| 화면 | Method | URI | 설명 |
|---|---|---|---|
| CON-01 | GET | `/api/business-service-console/v1/services` | 서비스 명세 목록 |
| CON-01 | GET | `/api/business-service-console/v1/services/{serviceId}` | 서비스 명세 상세 |
| CON-01 | POST | `/api/business-service-console/v1/services` | 명세 등록/수정 (Bean·Interface·Service ID 자동 검증) |
| CON-01 | PATCH | `/api/business-service-console/v1/services/{serviceId}/active?use=true\|false` | 사용/미사용 전환 |
| CON-02 | POST | `/api/business-service-console/v1/services/{serviceId}/test` | 서비스 Test (운영과 동일 경로로 위임) |

> 화면(UI)은 Skeleton 범위가 아니며 **Console 백엔드 API 만** 제공한다. Console URI 는 설계서에 명시되지 않아 임시 규칙을 사용한다.

### 8.2 필수 Header (5.2)

| Header | 필수 | Skeleton 처리 |
|---|---|---|
| `Content-Type` | Y | `application/json` 고정 |
| `X-Client-ID` | Y | **누락 시 401 / BS-AUTH-001** |
| `Authorization` | Y | 수신만 하고 검증하지 않음 (Mock) |
| `X-Request-ID` | N | 미입력 시 서버 생성 (`REQ-yyyyMMdd-0001`) |
| `X-User-ID` | 조건부 | 미입력 시 Mock 사용자 `E00000` |
| `X-Trace-ID` | N | 미입력 시 서버 생성 (`TRACE-yyyyMMdd-0001`) |

### 8.3 표준 Request (5.3)

```json
{
  "serviceVersion": "1.0",
  "parameters": { "closingYearMonth": "2026-06", "accountingBasis": "IFRS17" },
  "options": { "locale": "ko-KR", "includeDetails": false }
}
```

### 8.4 표준 Error Response (5.5)

```json
{
  "requestId": "REQ-20260729-0008",
  "serviceId": "IFRS17.CLOSING.STATUS",
  "status": "ERROR",
  "error": {
    "code": "BS-VAL-001",
    "message": "기준년월 형식이 올바르지 않습니다. (형식: YYYY-MM, 입력값: 2026-13)",
    "errorId": "ERR-20260729-00001",
    "details": [{ "field": "closingYearMonth", "message": "..." }]
  }
}
```

### 8.5 표준 오류코드 (부록 A) — 전량 구현

`BS-VAL-001`(400) · `BS-VAL-002`(400) · `BS-AUTH-001`(401) · `BS-AUTH-002`(401) · `BS-AUTH-003`(403) ·
`BS-SVC-404`(404) · `BS-DATA-000`(200) · `BS-LEG-500`(500) · `BS-SYS-500`(500) · `BS-SYS-503`(503) · `BS-SYS-504`(504)

> 설계서 6.4에 따라 **Stack Trace는 서버 로그에만 기록**하고 외부 응답에는 `errorId`만 노출한다.
> 내부 오류(`BS-SYS-500`)는 상세 메시지를 감춘다.

---

## 9. 파일럿 5종 서비스 (설계서 9장) 및 **미확정(Draft) 항목**

| Service ID | 서비스명 | Handler (Dummy) | 배치 프로그램 ID | 화면명 |
|---|---|---|---|---|
| `IFRS17.CLOSING.STATUS` | 결산 진행상태 조회 | `dummyClosingStatusHandler` | `TBD-BAT-CLOSING-STATUS-001` | `TBD-결산진행현황조회` |
| `IFRS17.JOURNAL.STATUS` | 전표 생성·반영 상태 조회 | `dummyJournalStatusHandler` | `TBD-BAT-JOURNAL-STATUS-001` | `TBD-전표생성현황조회` |
| `IFRS17.EXPENSE.STATUS` | 사업비 처리 상태 조회 | `dummyExpenseStatusHandler` | `TBD-BAT-EXPENSE-STATUS-001` | `TBD-사업비처리현황조회` |
| `IFRS17.STATEMENT.STATUS` | 재무제표 산출 상태 조회 | `dummyStatementStatusHandler` | `TBD-BAT-STATEMENT-STATUS-001` | `TBD-재무제표산출현황조회` |
| `IFRS17.CSM.STATUS` | CSM 산출 상태 조회 | `dummyCsmStatusHandler` | `TBD-BAT-CSM-STATUS-001` | `TBD-CSM산출현황조회` |
| `IFRS17.SAMPLE.DISABLED` | 비활성 샘플(테스트 전용) | — | `TBD-NONE` | `TBD-NONE` |

> ⚠️ **배치 프로그램 ID와 화면명은 아직 확정되지 않은 항목이므로 테스트 가능한 임시값(`TBD-...`)만 넣은 Draft 이다.**
> 설계서 14장 No.1 "5개 서비스별 기존 화면·Controller·Service·DAO 매핑"(착수 D+5 제출) 확정 시 교체한다.
> 확인 위치: `InMemoryServiceMetadataRepository`, 각 `Dummy*LegacyAdapter`, 응답의 `result.legacyBinding` 블록.

---

## 10. Mock / Draft 처리 항목 요약

| 컴포넌트 | Skeleton 동작 | 실제 구현 시 전환 대상 |
|---|---|---|
| `HttpHeaderRequestContextResolver` | Mock 사용자(`E00000`/`IT001`/`IFRS17_USER`) 생성 | SSO Token 검증 → 실제 사용자 Context |
| `DummyClientAuthenticationService` | `X-Client-ID` 존재만 확인 | `BS_CLIENT`/`BS_CLIENT_SERVICE` 조회 + 토큰/허용 IP 검증 |
| `DummyAuthorizationService` | 항상 ALLOW | `BS_SERVICE_ROLE` + 기존 IFRS17 권한정보 대조 |
| `InMemoryServiceMetadataRepository` | 하드코딩 Catalog 6건 | `business_service.bs_service` / `bs_service_version` 조회 |
| `ConsoleAuditLogger` | `System.out` 출력 | `bs_call_log` INSERT/UPDATE |
| `DefaultMaskingPolicy` | pass-through | 민감정보 마스킹 정책 적용 |
| `Dummy*StatusHandler` | 하드코딩 Mock 결과 | `XxxStatusBusinessService` (Bean명: `closingStatusBusinessService` 등) |
| `Dummy*StatusLegacyAdapter` | 하드코딩 결과 | 기존 IFRS17 Service 호출 + Mapper 변환 |
| `SkeletonFailureSimulator` | `__simulate` 오류 유발 | **제거** |
| `GET /calls/{requestId}` | 안내 메시지 반환 | `bs_call_log` 조회 |

설계서 10.5 **구현 시 금지사항** 준수 상태: Controller에 업무 SQL/규칙 없음 · 타 시스템 DB 미접근 ·
Entity/Map 무검증 직렬화 없음(전용 DTO 사용) · 권한 확인 단계 존재 · 감사로그에 Request 원문 미저장(`parameterHash`만 기록).

---

## 11. 배포 및 테스트 환경 안내

### 11.1 로컬 개발자 PC (권장 — 현 단계 검증용)

```bash
mvn clean package -DskipTests
java -Dfile.encoding=UTF-8 -jar target/ifrs17-business-service-layer-skeleton.jar
# → http://localhost:8080 으로 REST 호출 검증
```

* 필요 환경: JDK 8+ , Maven 3.6+ , REST 클라이언트(Postman 등)
* 외부 의존성(DB, SSO, Legacy) **없음** → 망 분리 환경에서도 단독 기동 가능

### 11.2 개발서버(리눅스) 백그라운드 기동

```bash
nohup java -Dfile.encoding=UTF-8 -jar ifrs17-business-service-layer-skeleton.jar \
      --server.port=8080 > bsl-skeleton.log 2>&1 &

tail -f bsl-skeleton.log        # 감사 로그([BSL-AUDIT-*]) 실시간 확인
```

방화벽에서 8080 포트 인입을 허용하면, 현업 PC의 REST 클라이언트에서
`http://<개발서버IP>:8080/api/business-services/v1/IFRS17.CLOSING.STATUS:execute` 로 검증할 수 있다.

### 11.3 IFRS17 WAS(기존 컨테이너) 배포 시

현행 IFRS17 WAS에 얹어 검증하려면 WAR로 전환한다.

1. `pom.xml` 에 `<packaging>war</packaging>` 추가
2. `spring-boot-starter-tomcat` 을 `provided` scope로 지정
3. `BusinessServiceLayerApplication` 이 `SpringBootServletInitializer` 를 상속하도록 수정
4. `mvn clean package` → `target/ifrs17-business-service-layer-skeleton.war` 를 WAS에 배포
5. Context Path가 붙으므로 호출 URL은 `http://<WAS>/<contextPath>/api/business-services/v1/...` 가 된다

> 설계서 12.1에 따라 Business Service Layer는 IFRS17 애플리케이션 내 **독립 Package/Module**로 관리하며,
> 환경별 설정은 외부 Properties로 분리한다. Skeleton 단계에서는 DB Script와 Rollback 대상이 없다.

---

## 12. 신규 서비스 추가 방법 (개발자 가이드)

1. `domain/<도메인>/XxxRequest.java`, `XxxResponse.java` 작성
2. `BusinessServiceHandler<REQ, RES>` 구현 클래스 작성 후 `@Component("xxxBusinessService")` 등록
   - `serviceId()`, `requestType()`, `validate()`, `authorize()`, `process()` 구현
3. `legacy/adapter/XxxLegacyAdapter` 작성 (기존 Service 호출 + DTO 변환)
4. `InMemoryServiceMetadataRepository` 에 Catalog 항목 등록
   *(실제 구현 시에는 관리 Console에서 서비스 명세를 등록 → Bean 검증 → 사용 전환, 설계서 8.4)*
5. README 5장 호출 시나리오 표에 요청 추가 및 테스트 케이스 작성

Dispatcher가 기동 시 `BusinessServiceHandler` 구현 Bean을 자동 수집하므로 **Controller 수정은 불필요**하다.
`serviceId` 중복 등록 시 기동이 실패하여 조기에 오류를 검출한다.

---

## 13. 다음 단계 (Phase 1 본 개발)

1. 설계서 14장 미결사항 확정 — **5개 서비스별 배치 프로그램/화면/Service/DAO 매핑(현행 매핑서)**
2. `business_service` Schema DDL 적용 (설계서 7.3) 및 MyBatis Mapper 작성
3. Dummy 컴포넌트 → 실 구현체 교체 (SSO / 권한 / 감사로그 DB 저장 / Legacy Adapter)
4. Business Service Console 2개 화면(CON-01 서비스 명세 관리, CON-02 서비스 Test) 개발
5. Timeout·Circuit·Payload 제한 등 운영 보호 로직 적용 (설계서 3.3 / 11.3)
6. OpenAPI(Swagger) 명세 및 테스트 결과서 제출 (설계서 13.2)

---

## 부록. 저장소 구조 및 검증 결과

```
.
├── pom.xml
├── README.md
├── docs/IFRS17-BSL-기능정의서_v1.0.md   # 기능정의서 (개발관리·통합테스트 기준서)
├── scripts/smoke-test.sh                # curl 스모크 테스트 (13 시나리오)
└── src
    ├── main/java/com/koreanre/ifrs17/businessservice/...       # 본문 7장 참조
    ├── main/resources/{application.yml, banner.txt}
    └── test/java/.../{BusinessServiceControllerTest, console/ServiceConsoleControllerTest}.java  # 22건
```

| 검증 항목 | 결과 |
|---|---|
| `mvn clean test` | **Tests run: 22, Failures: 0, Errors: 0** |
| `./scripts/smoke-test.sh` | **PASS=13, FAIL=0** |
| DoD (`IFRS17.CLOSING.STATUS:execute`) | **HTTP 200 / `status=SUCCESS`** |

---

## 형상관리 브랜치 규칙

| 브랜치 | 용도 |
|---|---|
| `main` | 기준 브랜치. 검수 완료분만 반영한다. |
| `Skeleton_v1.1` | Skeleton 구현본 v1.1 |
| `Skeleton_v1.2` | Skeleton 구현본 v1.2 — 설계서 4.1 패키지 구조 전수 준수 |
| `Skeleton_v1.3` | Skeleton 구현본 v1.3 — 기능정의서 추가 (본 브랜치) |
| `Skeleton_v1.4`, `v1.5`, ... | 이후 Skeleton 개선·보완 시 버전을 올려 신규 브랜치로 관리한다. |

각 버전 브랜치는 `main` 에서 파생하며, 이전 버전 브랜치는 이력 추적을 위해 보존한다.

