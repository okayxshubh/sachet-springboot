# API ENDPOINTS (Spring Boot, React.js Integration)

## Conventions
- Base path: `/api/v1`
- Soft delete: `is_active` is handled via BaseEntity; list endpoints should default to active records only.
- Downloads: Notice, chargesheet, and related files are generated client-side in React (no download endpoints).
Comment: Keeps the API surface smaller and avoids heavy file streaming on the backend.

## Auth
- `POST /api/v1/auth/login`  -- Login with username/password
- `POST /api/v1/auth/logout` -- Logout
- `GET /api/v1/auth/me`      -- Get current user profile
Comment: Login returns JWT + role for UI access control.

## Users
- `GET /api/v1/users`               -- List users (active by default)
- `POST /api/v1/users`              -- Create user
- `GET /api/v1/users/{id}`          -- Get user by ID
- `PUT /api/v1/users/{id}`          -- Update user
- `PATCH /api/v1/users/{id}/status` -- Enable/disable user
Comment: Status change should flip `is_active` only, not hard-delete.

## Cases
- `GET /api/v1/cases`              -- List/search cases
- `POST /api/v1/cases`             -- Create case
- `GET /api/v1/cases/{id}`         -- Case details
- `PUT /api/v1/cases/{id}`         -- Update case
- `PATCH /api/v1/cases/{id}/assign` -- Assign case
Comment: Search should support FIR filters and assigned officer filters.

## Accused
- `GET /api/v1/cases/{caseId}/accused`  -- List accused
- `POST /api/v1/cases/{caseId}/accused` -- Add accused
- `PUT /api/v1/accused/{id}`            -- Update accused
- `PATCH /api/v1/accused/{id}/arrested` -- Update arrest status
Comment: Arrest status update should not edit identity fields.

## NCRP Transactions
- `GET /api/v1/cases/{caseId}/transactions`  -- List transactions
- `POST /api/v1/cases/{caseId}/transactions` -- Add transaction
- `PUT /api/v1/transactions/{id}`            -- Update transaction
Comment: Transactions should be immutable after finalization (soft rule).

## Notices
- `GET /api/v1/cases/{caseId}/notices`  -- List notices
- `POST /api/v1/cases/{caseId}/notices` -- Create notice
- `PUT /api/v1/notices/{id}`            -- Update notice status/info
Comment: Notice generation logic lives in backend service layer.

## Notice-Transaction Mapping
- `POST /api/v1/notices/{noticeId}/transactions`                -- Map transaction to notice
- `DELETE /api/v1/notices/{noticeId}/transactions/{transactionId}` -- Remove map
Comment: Mapping endpoints handle relational link tables only.

## Notice Replies
- `GET /api/v1/notices/{noticeId}/replies`  -- List replies
- `POST /api/v1/notices/{noticeId}/replies` -- Add reply
- `PUT /api/v1/replies/{id}`                -- Update reply
Comment: Reply status should be validated against allowed values.

## Correspondence
- `GET /api/v1/cases/{caseId}/correspondence`  -- List correspondence
- `POST /api/v1/cases/{caseId}/correspondence` -- Add correspondence
- `PUT /api/v1/correspondence/{id}`            -- Update correspondence
Comment: Incoming/outgoing correspondence can share the same table.

## Case Diaries
- `GET /api/v1/cases/{caseId}/diaries`  -- List diaries
- `POST /api/v1/cases/{caseId}/diaries` -- Add diary
- `PUT /api/v1/diaries/{id}`            -- Update diary
Comment: Update should increment version rather than overwrite content (optional rule).

## Audit Logs
- `GET /api/v1/audit-logs` -- List audit logs (admin only)
Comment: Audit logs should be read-only after creation.
