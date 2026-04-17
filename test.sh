#!/usr/bin/env bash
set -euo pipefail

BASE_URL="${BASE_URL:-http://localhost:8080/api}"

PROJECT_ID="${PROJECT_ID:-1}"
SPRINT_ID="${SPRINT_ID:-1}"
FEATURE_ID="${FEATURE_ID:-1}"
ISSUE_ID="${ISSUE_ID:-1}"
USER_ID="${USER_ID:-101}"

echo "Using:"
echo "  BASE_URL   = $BASE_URL"
echo "  PROJECT_ID = $PROJECT_ID"
echo "  SPRINT_ID  = $SPRINT_ID"
echo "  FEATURE_ID = $FEATURE_ID"
echo "  ISSUE_ID   = $ISSUE_ID"
echo "  USER_ID    = $USER_ID"
echo

request() {
    local method="$1"
    local url="$2"
    local body="${3:-}"

    echo "=================================================="
    echo "$method $url"
    if [[ -n "$body" ]]; then
        echo
        echo "Request body:"
        echo "$body"
    fi
    echo
    if [[ -n "$body" ]]; then
        curl -sS -X "$method" \
            "$url" \
            -H "Content-Type: application/json" \
            -d "$body"
    else
        curl -sS -X "$method" "$url"
    fi
    echo
    echo
}

echo "===== HEALTH ====="
request GET "$BASE_URL/health"

echo "===== PROJECTS ====="
request GET "$BASE_URL/projects"

request POST "$BASE_URL/projects/$PROJECT_ID/member" "{
  \"userId\": $USER_ID,
  \"role\": \"DEV\"
}"

request GET "$BASE_URL/projects/$PROJECT_ID/members"
request GET "$BASE_URL/projects/$PROJECT_ID/sprints"

echo "===== MEMBER DELETE ====="
request DELETE "$BASE_URL/projects/$PROJECT_ID/members/$USER_ID"
request GET "$BASE_URL/projects/$PROJECT_ID/members"

echo "===== FEATURES ====="
request GET "$BASE_URL/sprints/$SPRINT_ID/features"

request POST "$BASE_URL/sprints/$SPRINT_ID/features" '{
  "title": "Feature created by test script",
  "description": "Testing POST /api/sprints/{sprintId}/features",
  "priority": "high",
  "status": "open"
}'

request GET "$BASE_URL/sprints/$SPRINT_ID/features"

echo "===== ISSUES ====="
request GET "$BASE_URL/projects/$PROJECT_ID/issues"
request GET "$BASE_URL/projects/$PROJECT_ID/issues?sprintId=$SPRINT_ID"
request GET "$BASE_URL/projects/$PROJECT_ID/issues?sprintId=$SPRINT_ID&status=open"
request GET "$BASE_URL/projects/$PROJECT_ID/issues?assignedTo=$USER_ID"
request GET "$BASE_URL/projects/$PROJECT_ID/issues?dateRange=2026-01-01,2026-12-31"

request POST "$BASE_URL/projects/$PROJECT_ID/issues" "{
  \"title\": \"Issue created by test script\",
  \"description\": \"Testing POST /api/projects/{projectId}/issues\",
  \"type\": \"TASK\",
  \"status\": \"open\",
  \"estimatedHours\": 5,
  \"actualHours\": 0,
  \"featureId\": $FEATURE_ID,
  \"assigneeId\": $USER_ID,
  \"isVisible\": true
}"

request GET "$BASE_URL/projects/$PROJECT_ID/issues"

echo "===== ISSUE PATCH ====="
request PATCH "$BASE_URL/issues/$ISSUE_ID" '{
  "title": "Updated issue title from script",
  "status": "in_progress",
  "actualHours": 2,
  "isVisible": true
}'

request GET "$BASE_URL/projects/$PROJECT_ID/issues"

echo "===== ISSUE TIMELOGS ====="
request GET "$BASE_URL/issues/$ISSUE_ID/timelogs"

request POST "$BASE_URL/issues/$ISSUE_ID/timelogs" "{
  \"userId\": $USER_ID,
  \"hoursLogged\": 2.5,
  \"logDate\": \"2026-04-16\"
}"

request GET "$BASE_URL/issues/$ISSUE_ID/timelogs"

echo "===== PROJECT TIMELOGS ====="
request GET "$BASE_URL/projects/$PROJECT_ID/timelogs"
request GET "$BASE_URL/projects/$PROJECT_ID/timelogs?sprintId=$SPRINT_ID"

echo "===== KPIS ====="
request GET "$BASE_URL/projects/$PROJECT_ID/kpis/tasks-by-user"
request GET "$BASE_URL/projects/$PROJECT_ID/kpis/tasks-by-user?sprintId=$SPRINT_ID"

request GET "$BASE_URL/projects/$PROJECT_ID/kpis/summary"
request GET "$BASE_URL/projects/$PROJECT_ID/kpis/summary?sprintId=$SPRINT_ID"

request GET "$BASE_URL/projects/$PROJECT_ID/kpis/hours-by-user"
request GET "$BASE_URL/projects/$PROJECT_ID/kpis/hours-by-user?sprintId=$SPRINT_ID"

echo "===== USERS ====="
request GET "$BASE_URL/users"

request POST "$BASE_URL/users" '{
  "name": "User Three",
  "email": "user3@mail.com",
  "passwordHash": "testhash",
  "workMode": "remote",
  "roleId": 1,
  "managerId": 102,
  "status": "active"
}'

request GET "$BASE_URL/users"

request DELETE "$BASE_URL/users/103"
request GET "$BASE_URL/users"

echo "===== ISSUE DELETE ====="
request DELETE "$BASE_URL/issues/$ISSUE_ID"
request GET "$BASE_URL/projects/$PROJECT_ID/issues"

echo "===== NEGATIVE TESTING (ERROR HANDLING) ====="

echo "1. Testing 404 for non-existent endpoint:"
request GET "$BASE_URL/this-route-does-not-exist"

echo "2. Testing 400 for invalid business logic (Feature not in Project):"
request POST "$BASE_URL/projects/1/issues" '{
  "title": "Should Fail",
  "featureId": 9999,
  "type": "TASK",
  "status": "open"
}'

echo "3. Testing 400 for Validation Error (Empty Title):"
request POST "$BASE_URL/sprints/$SPRINT_ID/features" '{
  "title": "",
  "status": "open"
}'

echo "4. Testing 404 for Data Not Found (EmptyResultDataAccessException):"
request GET "$BASE_URL/projects/999999/members"

echo "5. Testing 400 for Duplicate Member (Data Integrity):"
request POST "$BASE_URL/projects/$PROJECT_ID/member" "{ \"userId\": $USER_ID, \"role\": \"DEV\" }"

echo "===== DONE ====="
