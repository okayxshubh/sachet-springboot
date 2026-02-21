# API ENDPOINTS (Spring Boot, React.js Integration)

## Conventions

- Base path: `/api`
- Encryption: All `/api/**` requests and responses are encrypted with `payload` (AES/CBC/PKCS5Padding). Helper endpoints under `/api/crypto/**` are plaintext.
- Soft delete: `is_active` is handled via BaseEntity; list endpoints should default to active records only.
- Downloads: Notice, chargesheet, and related files are generated client-side in React (no download endpoints).

**Note:** Update endpoints accept `updatedBy` in the request body; if omitted, the authenticated user is used.

---

## Crypto Helpers

- `POST /api/crypto/encrypt` -- Encrypt any JSON or text and return raw encrypted text (plain response)
- `POST /api/crypto/decrypt` -- Decrypt `{ payload }` or raw encrypted text and return the JSON or string

---

## Masters (District, PoliceStations, Ranks, Roles)

- `GET /api/masters/districts`        -- List all districts (active by default)
- `GET /api/masters/police-stations`  -- List all police stations (active by default)
- `GET /api/masters/ranks`            -- List all ranks (active by default)
- `GET /api/masters/roles`            -- List all roles (active by default)

**Note:** These are lookup APIs used for dropdowns and validations in user/case forms.

---

## Auth

- `POST /api/auth/register`       -- Register a new user (encrypted request/response)
- `POST /api/auth/login`          -- Login with phone/password, returns access + refresh tokens
- `POST /api/auth/refresh`        -- Exchange refresh token for a new access token (refresh rotation)
- `GET /api/auth/me`              -- Get current user profile (requires JWT)
- `POST /api/auth/get-user-token` -- Login to fetch user-specific encrypted token
- `POST /api/auth/global-token`   -- Stateless global token endpoint (works for all clients, no user required)

**Note:** Global token is stateless, can be used on multiple devices simultaneously, no session conflicts.

---

## Users

- `POST /api/users/by-rank-active` -- List users (along with activity status + according to rank)
- `POST /api/users`                -- Create a user
- `POST /api/users/get`            -- Get user by ID (pass encrypted ID JSON)
- `PUT /api/users/update`          -- Update user (supports `updatedBy`)
- `POST /api/users/delete`         -- Soft Delete

**Note:** Status change should flip `is_active` only, not hard-delete.


## Cases
- `POST /api/cases`              -- Create case
- `POST /api/cases/list`         -- List cases by Fir no / Year / Owner
- `POST /api/cases/get`          -- Case details by id in json
- `PUT /api/cases`        -- Update case (supports `updatedBy`)
- `PATCH /api/cases/assign` -- Assign case (supports `updatedBy`)
  **Note:** Search supports FIR filters and assigned officer filters.





---

## --------- APIs Above This Are Verified ---------

---



---

## Accused

- `GET /api/cases/{caseId}/accused`   -- List accused
- `POST /api/cases/{caseId}/accused`  -- Add accused
- `PUT /api/accused/{id}`             -- Update accused (supports `updatedBy`)
- `PATCH /api/accused/{id}/arrested`  -- Update arrest status (supports `updatedBy`)

**Note:** Arrest status update should not edit identity fields.

---

## NCRP Transactions

- `GET /api/cases/{caseId}/transactions`   -- List transactions
- `POST /api/cases/{caseId}/transactions`  -- Add transaction
- `PUT /api/transactions/{id}`             -- Update transaction (supports `updatedBy`)

**Note:** Transactions should be immutable after finalization (soft rule).

---

## Notices

- `GET /api/cases/{caseId}/notices`  -- List notices
- `POST /api/cases/{caseId}/notices` -- Create notice
- `PUT /api/notices/{id}`            -- Update notice status/info (supports `updatedBy`)

**Note:** Notice generation logic lives in backend service layer.

---

## Notice-Transaction Mapping

- `POST /api/notices/{noticeId}/transactions`                     -- Map transaction to notice
- `DELETE /api/notices/{noticeId}/transactions/{transactionId}`   -- Remove map

**Note:** Mapping endpoints handle relational link tables only.

---

## Notice Replies

- `GET /api/notices/{noticeId}/replies`  -- List replies
- `POST /api/notices/{noticeId}/replies` -- Add reply
- `PUT /api/replies/{id}`                -- Update reply (supports `updatedBy`)

**Note:** Reply status should be validated against allowed values.

---

## Correspondence

- `GET /api/cases/{caseId}/correspondence`  -- List correspondence
- `POST /api/cases/{caseId}/correspondence` -- Add correspondence
- `PUT /api/correspondence/{id}`            -- Update correspondence (supports `updatedBy`)

**Note:** Incoming/outgoing correspondence can share the same table.

---

## Case Diaries

- `GET /api/cases/{caseId}/diaries`  -- List diaries
- `POST /api/cases/{caseId}/diaries` -- Add diary
- `PUT /api/diaries/{id}`            -- Update diary (supports `updatedBy`)

**Note:** Update should increment version rather than overwrite content (optional rule).

---

## Audit Logs

- `GET /api/audit-logs` -- List audit logs (admin only)

**Note:** Audit logs should be read-only after creation.