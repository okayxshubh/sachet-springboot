# API ENDPOINTS (Spring Boot, React.js Integration)

## Conventions
- Base path: `/api`
- Encryption: All `/api/**` requests and responses are encrypted with `payload` (AES/CBC/PKCS5Padding). Helper endpoints under `/api/crypto/**` are plaintext.
- Soft delete: `is_active` is handled via BaseEntity; list endpoints should default to active records only.
- Downloads: Notice, chargesheet, and related files are generated client-side in React (no download endpoints).
Comment: Update endpoints accept `updatedBy` in the request body; if omitted, the authenticated user is used.

## Auth
- `POST /api/auth/login`  -- Login with phone/password, returns access + refresh tokens
- `POST /api/auth/refresh` -- Exchange refresh token for a new access token (refresh rotation)
- `POST /api/auth/logout` -- Logout
- `GET /api/auth/me`      -- Get current user profile
Comment: Access token is short-lived; refresh token enables automatic session renewal.

## Crypto Helpers
- `POST /api/crypto/encrypt` -- Encrypt any JSON or text and return raw encrypted text (plain response)
- `POST /api/crypto/decrypt` -- Decrypt `{ payload }` or raw encrypted text and return the JSON or string
Comment: These endpoints are plaintext for tooling/testing. Encrypt returns `text/plain` (no wrapper).

## Users
- `GET /api/users`               -- List users (active by default)
- `POST /api/users`              -- Create user
- `GET /api/users/{id}`          -- Get user by ID
- `PUT /api/users/{id}`          -- Update user (supports `updatedBy`)
- `PATCH /api/users/{id}/status` -- Enable/disable user (supports `updatedBy`)
Comment: Status change should flip `is_active` only, not hard-delete.

## Cases
- `GET /api/cases`              -- List/search cases
- `POST /api/cases`             -- Create case
- `GET /api/cases/{id}`         -- Case details
- `PUT /api/cases/{id}`         -- Update case (supports `updatedBy`)
- `PATCH /api/cases/{id}/assign` -- Assign case (supports `updatedBy`)
Comment: Search supports FIR filters and assigned officer filters.

## Accused
- `GET /api/cases/{caseId}/accused`  -- List accused
- `POST /api/cases/{caseId}/accused` -- Add accused
- `PUT /api/accused/{id}`            -- Update accused (supports `updatedBy`)
- `PATCH /api/accused/{id}/arrested` -- Update arrest status (supports `updatedBy`)
Comment: Arrest status update should not edit identity fields.

## NCRP Transactions
- `GET /api/cases/{caseId}/transactions`  -- List transactions
- `POST /api/cases/{caseId}/transactions` -- Add transaction
- `PUT /api/transactions/{id}`            -- Update transaction (supports `updatedBy`)
Comment: Transactions should be immutable after finalization (soft rule).

## Notices
- `GET /api/cases/{caseId}/notices`  -- List notices
- `POST /api/cases/{caseId}/notices` -- Create notice
- `PUT /api/notices/{id}`            -- Update notice status/info (supports `updatedBy`)
Comment: Notice generation logic lives in backend service layer.

## Notice-Transaction Mapping
- `POST /api/notices/{noticeId}/transactions`                -- Map transaction to notice
- `DELETE /api/notices/{noticeId}/transactions/{transactionId}` -- Remove map
Comment: Mapping endpoints handle relational link tables only.

## Notice Replies
- `GET /api/notices/{noticeId}/replies`  -- List replies
- `POST /api/notices/{noticeId}/replies` -- Add reply
- `PUT /api/replies/{id}`                -- Update reply (supports `updatedBy`)
Comment: Reply status should be validated against allowed values.

## Correspondence
- `GET /api/cases/{caseId}/correspondence`  -- List correspondence
- `POST /api/cases/{caseId}/correspondence` -- Add correspondence
- `PUT /api/correspondence/{id}`            -- Update correspondence (supports `updatedBy`)
Comment: Incoming/outgoing correspondence can share the same table.

## Case Diaries
- `GET /api/cases/{caseId}/diaries`  -- List diaries
- `POST /api/cases/{caseId}/diaries` -- Add diary
- `PUT /api/diaries/{id}`            -- Update diary (supports `updatedBy`)
Comment: Update should increment version rather than overwrite content (optional rule).

## Audit Logs
- `GET /api/audit-logs` -- List audit logs (admin only)
Comment: Audit logs should be read-only after creation.

