# Sachet Backend API Guide

This document reflects the current code in `src/main/java` and explains exactly:
- which endpoint to call,
- what body format to send,
- whether request/response is encrypted,
- and what response shape is returned.

## 1) Common Rules

- Base URL: `http://<host>:8080`
- API prefix: `/api`
- Auth header (for protected APIs):
  - `Authorization: Bearer <access_token>`
- Success envelope (most APIs):
  - `GenericResponse`
  - Shape:
    ```json
    {
      "timestamp": "dd-MM-yyyy HH:mm:ss",
      "status": "OK",
      "message": "...",
      "data": "... or object/array"
    }
    ```
- Error envelope (GlobalExceptionHandler):
  ```json
  {
    "timestamp": "dd-MM-yyyy HH:mm:ss",
    "status": "ERROR",
    "message": "...",
    "data": null
  }
  ```

## 2) Encryption Behavior (Important)

This project currently uses **two request styles**:

1. **Raw encrypted string in body** (controller manually decrypts using `SachetCrypto.decrypt(...)`)
   - Send `Content-Type: text/plain`
   - Body is encrypted text only

2. **JSON payload wrapper** (handled by `EncryptedRequestFilter` for JSON requests)
   - Send `Content-Type: application/json`
   - Body:
     ```json
     {
       "payload": "<encrypted_string>"
     }
     ```
   - Filter decrypts payload before controller mapping.

Response is also mixed:
- Some APIs return `data` as encrypted string.
- Some APIs return plain object/array in `data`.

## 3) Public APIs (No JWT Required)

## 3.1 Crypto Helpers

### POST `/api/crypto/encrypt`
- Auth: No
- Request:
  - `text/plain`: raw text/json string
  - OR `application/json`: any JSON object
- Encryption in request: No (this endpoint does encryption)
- Response: Plain `text/plain` encrypted string

### POST `/api/crypto/decrypt`
- Auth: No
- Request:
  - `text/plain`: encrypted string
  - OR `application/json`: `{ "payload": "<encrypted>" }`
- Response (`GenericResponse`):
  - `data`: decrypted JSON/object if parseable, else decrypted text

## 3.2 Auth

### POST `/api/auth/register`
- Auth: No
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "name": "...",
    "phone": "...",
    "password": "...",
    "rankId": 1,
    "psId": 1,
    "districtId": 1,
    "roleId": 2
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted string message (`"User registered successfully"`)
  - New users are saved with `isApproved=false` (approval pool)

### POST `/api/auth/login`
- Auth: No
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "phone": "...",
    "password": "..."
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted `AuthResponse`
- `AuthResponse` fields: `name`, `role`, `rank`, `ps`, `token`, `refreshToken`
- Note: login allowed only when `isActive=true` and `isApproved=true`

### POST `/api/auth/send-otp`
- Auth: No
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "phone": "7018437924"
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted string message (`"OTP sent successfully"`)
- Note: current implementation generates OTP and logs it on server side. Integrate an SMS gateway to actually deliver OTP to the mobile.

### POST `/api/auth/verify-otp`
- Auth: No
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "phone": "7018437924",
    "otp": "123456"
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted string message (`"OTP verified successfully"`)

### POST `/api/auth/change-password`
- Auth: No
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "phone": "7018437924",
    "newPassword": "NewStrongPassword@123"
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted string message (`"Password changed successfully"`)
- Note: this works only after successful OTP verification.

### POST `/api/auth/get-user-token`
- Auth: No
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "phone": "...",
    "password": "..."
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted `AuthResponse`

### POST `/api/auth/refresh`
- Auth: No
- Request type: plain **refresh token string** body (`text/plain`)
- Response: `GenericResponse`
  - `data`: new access token string (not encrypted)

### GET `/api/auth/check-token`
- Auth: Send bearer token in header
- Request body: none
- Response: plain text
  - `200`: `VALID`
  - `401`: `EXPIRED`

## 4) Protected APIs (JWT Required)

All endpoints below require `Authorization: Bearer <token>`.

## 4.1 Auth Profile

### GET `/api/auth/me`
- Request body: none
- Response: `GenericResponse`
  - `data`: encrypted `UserResponse`
  - `UserResponse`: `id,name,rankName,psName,districtName,phone,roleName,isActive,isApproved`

## 4.2 Masters

### GET `/api/masters/districts`
- Request body: none
- Response: `GenericResponse`
  - `data`: encrypted array of districts

### POST `/api/masters/police-stations/by-district`
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "districtId": 1
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted array of police stations

### GET `/api/masters/ranks`
- Request body: none
- Response: `GenericResponse`
  - `data`: encrypted array of ranks

### GET `/api/masters/roles`
- Request body: none
- Response: `GenericResponse`
  - `data`: encrypted array of roles

## 4.3 Users

### POST `/api/users/by-rank-active`
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "rankId": 2,
    "isActive": true
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted `UserResponse[]`
- Note:
  - Returns only approved users (`isApproved=true`)
  - `rankId` and `isActive` are optional filters
  - If a field is not sent, it is ignored
  - Set `"isActive": false` to fetch inactive approved users

### GET `/api/users/approval-pool`
- Request body: none
- Response: `GenericResponse`
  - `data`: encrypted `UserResponse[]`
- Note: contains `isActive=true` and `isApproved=false`

### POST `/api/users/get`
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "id": 1
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted `UserResponse`

### POST `/api/users/approve`
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "id": 1
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted approved `UserResponse`

### POST `/api/users/create`
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "name": "...",
    "rankId": 2,
    "psId": 1,
    "roleId": 3,
    "phone": "...",
    "password": "..."
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted created `UserResponse`
- Note: created user enters approval pool (`isApproved=false`)

### PUT `/api/users/update`
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "id": 1,
    "request": {
      "name": "...",
      "rankId": 2,
      "psId": 1,
      "roleId": 3,
      "phone": "...",
      "password": "...",
      "isActive": true,
      "updatedBy": "..."
    }
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted updated `UserResponse`

### POST `/api/users/delete`
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "id": 1,
    "updatedBy": "..."
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted string (`"User deactivated successfully"`)

## 4.4 Cases

### POST `/api/cases`
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "firNo": "12",
    "firYear": 2026,
    "psName": "Shimla PS",
    "district": "Shimla",
    "sections": "420",
    "summary": "...",
    "createdById": 1,
    "assignedToId": 2
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted created `CaseFile`

### POST `/api/cases/list`
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "firNo": "12",
    "firYear": 2026,
    "assignedToId": 2,
    "isActive": true,
    "monthYear": "02-2026"
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted `CaseFile[]`
- Note:
  - `isActive` and `monthYear` are optional filters
  - `monthYear` format must be `MM-yyyy`
  - If a field is not sent, it is ignored

### POST `/api/cases/get`
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "id": 1
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted `CaseFile`

### PUT `/api/cases/update`
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption: `CaseUpdateRequest`
  ```json
  {
    "id": 1,
    "firNo": "12",
    "firYear": 2026,
    "psName": "...",
    "district": "...",
    "sections": "...",
    "summary": "...",
    "createdById": 1,
    "assignedToId": 2,
    "updatedBy": "..."
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted updated `CaseFile`

### PATCH `/api/cases/assign`
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "caseId": 1,
    "assignedToId": 2,
    "updatedBy": "..."
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted updated `CaseFile`

### PATCH `/api/cases/delete`
- Request type: **Raw encrypted string** (`text/plain`)
- Plain JSON before encryption:
  ```json
  {
    "id": 1,
    "updatedBy": "..."
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted success text

## 4.5 Accused

### POST `/api/accused/list`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt:
  ```json
  {
    "caseId": 1
  }
  ```
- Send:
  ```json
  {
    "payload": "<encrypted_plain_json_above>"
  }
  ```
- Response: `GenericResponse`
  - `data`: plain `Accused[]` (not encrypted)

### POST `/api/accused/create`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt: `AccusedRequest`
- Response: `GenericResponse`
  - `data`: plain `Accused`

### PUT `/api/accused/update`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt: `AccusedRequest`
- Response: `GenericResponse`
  - `data`: plain `Accused`

### PATCH `/api/accused/arrested`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt:
  ```json
  {
    "id": 1,
    "arrested": true,
    "updatedBy": "..."
  }
  ```
- Response: `GenericResponse`
  - `data`: plain `Accused`

## 4.6 Transactions (NCRP)

### POST `/api/transactions/list`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt: `{ "caseId": 1 }`
- Response: `GenericResponse`
  - `data`: plain `NcrpTransaction[]`

### POST `/api/transactions/create`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt: `NcrpTransactionRequest`
- Response: `GenericResponse`
  - `data`: plain `NcrpTransaction`

### PUT `/api/transactions/update`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt: `NcrpTransactionRequest`
- Response: `GenericResponse`
  - `data`: plain `NcrpTransaction`

## 4.7 Notices

### POST `/api/notices/list`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt: `{ "caseId": 1 }`
- Response: `GenericResponse`
  - `data`: plain `Notice[]`

### POST `/api/notices/create`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt: `NoticeRequest`
- Response: `GenericResponse`
  - `data`: plain `Notice`

### PUT `/api/notices/update`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt: `NoticeRequest`
- Response: `GenericResponse`
  - `data`: plain `Notice`

## 4.8 Notice Templates

### GET `/api/notices/templates`
- Request body: none
- Response: `GenericResponse`
  - `data`: encrypted `NoticeTemplateResponse[]`
  - Each item contains `id`, `noticeTypeId`, `noticeTypeName`, `fileName`, `content`

### GET `/api/notices/templates/{id}`
- Request body: none
- Use this endpoint to load the selected template into the frontend editor.
- Response: `GenericResponse`
  - `data`: encrypted `NoticeTemplateResponse`

### PUT `/api/notices/templates/{id}`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt:
  ```json
  {
    "content": "Updated template content"
  }
  ```
- Response: `GenericResponse`
  - `data`: encrypted `NoticeTemplateResponse`

### POST `/api/notices/templates/detail`
- Compatibility endpoint.
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt:
  ```json
  {
    "id": 1
  }
```
- Also accepts `{ "noticeTypeId": 1 }`.
- Response: `GenericResponse`
  - `data`: encrypted `NoticeTemplateResponse`

### PUT `/api/notices/templates/update`
- Compatibility endpoint.
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt:
  ```json
  {
    "id": 1,
    "content": "Updated template content"
  }
```
- Also accepts `noticeTypeId` instead of `id`.
- Response: `GenericResponse`
  - `data`: encrypted `NoticeTemplateResponse`

## 4.9 Notice Replies

### POST `/api/replies/list`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt: `{ "noticeId": 1 }`
- Response: `GenericResponse`
  - `data`: plain `NoticeReply[]`

### POST `/api/replies/create`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt: `NoticeReplyRequest`
- Response: `GenericResponse`
  - `data`: plain `NoticeReply`

### PUT `/api/replies/update`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt: `NoticeReplyRequest`
- Response: `GenericResponse`
  - `data`: plain `NoticeReply`

## 4.10 Notice-Transaction Mapping

### GET `/api/notices/{noticeId}/transactions`
- Request body: none
- Response: `GenericResponse`
  - `data`: plain `NoticeTransaction[]`

### POST `/api/notices/{noticeId}/transactions`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt:
  ```json
  {
    "noticeId": 1,
    "transactionId": 10
  }
  ```
- Response: `GenericResponse`
  - `data`: plain `NoticeTransaction`

### DELETE `/api/notices/{noticeId}/transactions/{transactionId}`
- Request body: none
- Response: `GenericResponse`
  - `status`: `OK`
  - `message`: `Mapping removed`
  - `data`: `OK`

## 4.10 Correspondence

### GET `/api/cases/{caseId}/correspondence`
- Request body: none
- Response: `GenericResponse`
  - `data`: plain `Correspondence[]`

### POST `/api/cases/{caseId}/correspondence`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt: `CorrespondenceRequest`
- Response: `GenericResponse`
  - `data`: plain `Correspondence`

### PUT `/api/correspondence/{id}`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt: `CorrespondenceRequest`
- Response: `GenericResponse`
  - `data`: plain `Correspondence`

## 4.11 Case Diaries

### GET `/api/cases/{caseId}/diaries`
- Request body: none
- Response: `GenericResponse`
  - `data`: plain `CaseDiary[]`

### POST `/api/cases/{caseId}/diaries`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt: `CaseDiaryRequest`
- Response: `GenericResponse`
  - `data`: plain `CaseDiary`

### PUT `/api/diaries/{id}`
- Request type: **JSON payload wrapper** (`application/json`)
- Plain JSON to encrypt: `CaseDiaryRequest`
- Response: `GenericResponse`
  - `data`: plain `CaseDiary`

## 4.12 Audit Logs

### GET `/api/audit-logs?includeInactive=false`
- Request body: none
- Response: `GenericResponse`
  - `data`: plain `AuditLog[]`

## 5) Quick Calling Patterns

## 5.1 For raw-encrypted endpoints (mostly Auth/User/Case + one master POST)

1. Prepare plain JSON.
2. Encrypt via `POST /api/crypto/encrypt`.
3. Call target API with `Content-Type: text/plain` and body = encrypted string.
4. If response `data` is encrypted, decrypt using `POST /api/crypto/decrypt`.

## 5.2 For DTO endpoints behind filter (Accused/Notice/etc.)

1. Prepare plain JSON.
2. Encrypt via `POST /api/crypto/encrypt`.
3. Call target API with `Content-Type: application/json` and:
   ```json
   { "payload": "<encrypted>" }
   ```
4. Response `data` is usually plain object/array.

## 6) Important Notes

- `GET`/`DELETE` endpoints are not filtered by `EncryptedRequestFilter`.
- If you send `application/json` to filtered POST/PUT/PATCH endpoints without `payload`, server returns `400 Missing encrypted payload`.
- Login/refresh will fail for users where `isApproved=false`.
- Users created via register/create enter approval pool and must be approved via `/api/users/approve`.
- OTP flow:
  - `/api/auth/send-otp` -> `/api/auth/verify-otp` -> `/api/auth/change-password`
  - OTP is currently logged by backend. Connect an SMS provider for real mobile delivery.

## 7) Make OTP Sending

Use this production checklist for India telecom routing:

1. Register a business account with an India SMS provider (for example MSG91, Gupshup, Exotel, Kaleyra, etc.).
2. Complete DLT registration on your operator/aggregator portal (required in India):
   - Principal Entity (PE) registration
   - Header/Sender ID approval
   - OTP template approval (content template)
3. Keep these values ready in backend config:
   - API key / auth token
   - Sender ID
   - DLT template ID
   - DLT entity/PE ID (if provider requires)
4. Replace `dispatchOtp(...)` in `AuthService` with provider API call.
5. OTP message should use approved template text exactly (DLT compliance).
6. Test with real Indian numbers in Shimla (`+91XXXXXXXXXX`) and verify delivery latency/failures.
7. Add operational controls:
   - rate limit by phone/IP
   - retry/backoff
   - OTP attempt limit
   - audit logs and masking in logs

Current code location for integration:
- `src/main/java/in/gov/cybercrime/sachet/service/AuthService.java` -> method `dispatchOtp(String phone, String otp)`.
