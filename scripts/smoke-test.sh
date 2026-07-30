#!/usr/bin/env bash
#
# IFRS17 Business Service Layer - Skeleton 통신 검증 스크립트
# 사전 조건: 애플리케이션이 http://localhost:8080 에서 기동 중이어야 한다.
#
# 사용법: ./scripts/smoke-test.sh [BASE_URL]
#
set -u

BASE_URL="${1:-http://localhost:8080}"
API="${BASE_URL}/api/business-services/v1"
CLIENT_ID="TEST-CLIENT"
PASS=0
FAIL=0

call() {
  local title="$1"; shift
  local expected="$1"; shift
  local service_id="$1"; shift
  local body="$1"; shift
  local extra_header="${1:-}"

  echo "------------------------------------------------------------"
  echo "[TEST] ${title}  (기대 HTTP ${expected})"

  if [ -n "${extra_header}" ]; then
    response=$(curl -s --noproxy '*' -w '\n%{http_code}' -X POST "${API}/${service_id}:execute" \
      -H 'Content-Type: application/json' -H "${extra_header}" -d "${body}")
  else
    response=$(curl -s --noproxy '*' -w '\n%{http_code}' -X POST "${API}/${service_id}:execute" \
      -H 'Content-Type: application/json' -d "${body}")
  fi

  status=$(echo "${response}" | tail -n 1)
  payload=$(echo "${response}" | sed '$d')
  echo "${payload}"

  if [ "${status}" = "${expected}" ]; then
    echo "  => PASS (HTTP ${status})"
    PASS=$((PASS + 1))
  else
    echo "  => FAIL (HTTP ${status})"
    FAIL=$((FAIL + 1))
  fi
}

echo "============================================================"
echo " IFRS17 Business Service Layer - Skeleton Smoke Test"
echo " BASE_URL : ${BASE_URL}"
echo "============================================================"

# 1. [DoD] 정상 호출
call "[DoD] 결산 진행상태 조회" 200 "IFRS17.CLOSING.STATUS" \
  '{"serviceVersion":"1.0","parameters":{"closingYearMonth":"2026-06"}}' "X-Client-ID: ${CLIENT_ID}"

# 2. 파일럿 나머지 4종
call "전표 생성·반영 상태 조회" 200 "IFRS17.JOURNAL.STATUS" \
  '{"serviceVersion":"1.0","parameters":{"closingYearMonth":"2026-06"}}' "X-Client-ID: ${CLIENT_ID}"
call "사업비 처리 상태 조회" 200 "IFRS17.EXPENSE.STATUS" \
  '{"serviceVersion":"1.0","parameters":{"closingYearMonth":"2026-06"}}' "X-Client-ID: ${CLIENT_ID}"
call "재무제표 산출 상태 조회" 200 "IFRS17.STATEMENT.STATUS" \
  '{"serviceVersion":"1.0","parameters":{"closingYearMonth":"2026-06"}}' "X-Client-ID: ${CLIENT_ID}"
call "CSM 산출 상태 조회" 200 "IFRS17.CSM.STATUS" \
  '{"serviceVersion":"1.0","parameters":{"closingYearMonth":"2026-06"}}' "X-Client-ID: ${CLIENT_ID}"

# 3. 데이터 없음 (SUCCESS + BS-DATA-000)
call "데이터 없음(SUCCESS + 빈 결과)" 200 "IFRS17.CLOSING.STATUS" \
  '{"serviceVersion":"1.0","parameters":{"closingYearMonth":"2020-01"}}' "X-Client-ID: ${CLIENT_ID}"

# 4. 오류 시나리오 (설계서 11.2)
call "입력 오류 BS-VAL-001" 400 "IFRS17.CLOSING.STATUS" \
  '{"serviceVersion":"1.0","parameters":{"closingYearMonth":"2026-13"}}' "X-Client-ID: ${CLIENT_ID}"
call "인증 실패 BS-AUTH-001 (X-Client-ID 누락)" 401 "IFRS17.CLOSING.STATUS" \
  '{"serviceVersion":"1.0","parameters":{"closingYearMonth":"2026-06"}}' ""
call "권한 없음 BS-AUTH-003 (시뮬레이션)" 403 "IFRS17.CLOSING.STATUS" \
  '{"serviceVersion":"1.0","parameters":{"closingYearMonth":"2026-06","__simulate":"FORBIDDEN"}}' "X-Client-ID: ${CLIENT_ID}"
call "서비스 비활성 BS-SVC-404" 404 "IFRS17.SAMPLE.DISABLED" \
  '{"serviceVersion":"1.0","parameters":{"closingYearMonth":"2026-06"}}' "X-Client-ID: ${CLIENT_ID}"
call "Timeout BS-SYS-504 (시뮬레이션)" 504 "IFRS17.CLOSING.STATUS" \
  '{"serviceVersion":"1.0","parameters":{"closingYearMonth":"2026-06","__simulate":"TIMEOUT"}}' "X-Client-ID: ${CLIENT_ID}"
call "Legacy 오류 BS-LEG-500 (시뮬레이션)" 500 "IFRS17.CLOSING.STATUS" \
  '{"serviceVersion":"1.0","parameters":{"closingYearMonth":"2026-06","__simulate":"LEGACY_ERROR"}}' "X-Client-ID: ${CLIENT_ID}"

# 5. Catalog
echo "------------------------------------------------------------"
echo "[TEST] 서비스 Catalog 목록 조회 (기대 HTTP 200)"
catalog=$(curl -s --noproxy '*' -w '\n%{http_code}' "${API}/catalog" -H "X-Client-ID: ${CLIENT_ID}")
catalog_status=$(echo "${catalog}" | tail -n 1)
echo "${catalog}" | sed '$d'
if [ "${catalog_status}" = "200" ]; then
  echo "  => PASS (HTTP ${catalog_status})"; PASS=$((PASS + 1))
else
  echo "  => FAIL (HTTP ${catalog_status})"; FAIL=$((FAIL + 1))
fi

echo "============================================================"
echo " 결과: PASS=${PASS}, FAIL=${FAIL}"
echo "============================================================"
[ "${FAIL}" -eq 0 ]
