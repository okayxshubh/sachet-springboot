# SACHET Backend Reference

## Project Structure (Current)
- `src/main/java/in/gov/cybercrime/sachet/config` - Security, JWT filter, CORS, web config
- `src/main/java/in/gov/cybercrime/sachet/controller` - REST controllers (all `/api/v1` endpoints)
- `src/main/java/in/gov/cybercrime/sachet/dto` - Request/response models
- `src/main/java/in/gov/cybercrime/sachet/entity` - JPA entities (BaseEntity + domain entities)
- `src/main/java/in/gov/cybercrime/sachet/repository` - Spring Data repositories
- `src/main/java/in/gov/cybercrime/sachet/service` - Business services
- `src/main/resources` - App configs (`application.properties`)
- `src/test/java/in/gov/cybercrime/sachet` - Tests

Comment: This document matches the current code layout in the backend project.

## Base Entity (Common Fields)
**Entity:** `BaseEntity`
- `id: Long`
- `createdAt: Instant`
- `updatedAt: Instant`
- `isActive: Boolean`
Comment: `isActive` is the soft-delete flag; list endpoints return active records by default.

## Entities and Fields

**Entity:** `User`
- `id: Long`
- `name: String`
- `rank: String`
- `psName: String`
- `district: String`
- `phone: String`
- `role: UserRole` (`SHO`, `IO`)
- `passwordHash: String` (bcrypt, not serialized in JSON)
- `createdAt: Instant`
- `updatedAt: Instant`
- `isActive: Boolean`

**Entity:** `CaseFile`
- `id: Long`
- `firNo: String`
- `firYear: Integer`
- `psName: String`
- `district: String`
- `sections: String`
- `summary: String`
- `createdBy: User`
- `assignedTo: User`
- `createdAt: Instant`
- `updatedAt: Instant`
- `isActive: Boolean`

**Entity:** `Accused`
- `id: Long`
- `caseFile: CaseFile`
- `name: String`
- `fatherName: String`
- `address: String`
- `arrested: Boolean`
- `createdAt: Instant`
- `updatedAt: Instant`
- `isActive: Boolean`

**Entity:** `NcrpTransaction`
- `id: Long`
- `caseFile: CaseFile`
- `ackNo: String`
- `bankName: String`
- `accountNo: String`
- `transactionDate: LocalDate`
- `amount: BigDecimal`
- `layer: String`
- `createdAt: Instant`
- `updatedAt: Instant`
- `isActive: Boolean`

**Entity:** `Notice`
- `id: Long`
- `caseFile: CaseFile`
- `noticeId: String`
- `noticeType: String`
- `issuedTo: String`
- `issuedDate: LocalDate`
- `status: String` (default: `Pending`)
- `createdAt: Instant`
- `updatedAt: Instant`
- `isActive: Boolean`

**Entity:** `NoticeTransaction`
- `id: Long`
- `notice: Notice`
- `transaction: NcrpTransaction`
- `createdAt: Instant`
- `updatedAt: Instant`
- `isActive: Boolean`

**Entity:** `NoticeReply`
- `id: Long`
- `notice: Notice`
- `replyDate: LocalDate`
- `summary: String`
- `status: String` (`Complete`, `Partial`, `No Response`)
- `createdAt: Instant`
- `updatedAt: Instant`
- `isActive: Boolean`

**Entity:** `Correspondence`
- `id: Long`
- `caseFile: CaseFile`
- `type: String`
- `subject: String`
- `dateSent: LocalDate`
- `replySummary: String`
- `createdAt: Instant`
- `updatedAt: Instant`
- `isActive: Boolean`

**Entity:** `CaseDiary`
- `id: Long`
- `caseFile: CaseFile`
- `diaryDate: LocalDate`
- `content: String`
- `version: Integer`
- `createdAt: Instant`
- `updatedAt: Instant`
- `isActive: Boolean`

**Entity:** `AuditLog`
- `id: Long`
- `user: User`
- `caseFile: CaseFile` (nullable)
- `action: String`
- `timestamp: Instant`
- `createdAt: Instant`
- `updatedAt: Instant`
- `isActive: Boolean`

## API Endpoints (What Each Does)
Base path: `/api/v1`

### Auth
- `POST /auth/register` - Register a user (name, rank, psName, district, phone, role, password).
- `POST /auth/login` - Login with phone + password, returns JWT + user name + role.
- `POST /auth/logout` - Client-side logout acknowledgement.
- `GET /auth/me` - Returns the currently authenticated user's profile.

### Users
- `GET /users` - List users (active by default). Query: `includeInactive=true` to include soft-deleted users.
- `POST /users` - Create a new user.
- `GET /users/{id}` - Fetch a single user by ID.
- `PUT /users/{id}` - Update user fields (name, rank, psName, district, phone, role, password).
- `PATCH /users/{id}/status` - Enable/disable user (`isActive` toggle).

### Cases
- `GET /cases` - List/search cases. Query: `firNo`, `firYear`, `assignedToId`.
- `POST /cases` - Create a case.
- `GET /cases/{id}` - Get case details.
- `PUT /cases/{id}` - Update case fields.
- `PATCH /cases/{id}/assign` - Assign/reassign case to a user.

### Accused
- `GET /cases/{caseId}/accused` - List accused for a case.
- `POST /cases/{caseId}/accused` - Add an accused to a case.
- `PUT /accused/{id}` - Update accused details.
- `PATCH /accused/{id}/arrested` - Update arrested flag only.

### NCRP Transactions
- `GET /cases/{caseId}/transactions` - List transactions for a case.
- `POST /cases/{caseId}/transactions` - Add a transaction to a case.
- `PUT /transactions/{id}` - Update a transaction.

### Notices
- `GET /cases/{caseId}/notices` - List notices for a case.
- `POST /cases/{caseId}/notices` - Create a notice for a case.
- `PUT /notices/{id}` - Update notice details/status.

### Notice-Transaction Mapping
- `GET /notices/{noticeId}/transactions` - List mapped transactions for a notice.
- `POST /notices/{noticeId}/transactions` - Add a transaction mapping to a notice.
- `DELETE /notices/{noticeId}/transactions/{transactionId}` - Soft-delete a mapping.

### Notice Replies
- `GET /notices/{noticeId}/replies` - List replies for a notice.
- `POST /notices/{noticeId}/replies` - Add a reply to a notice.
- `PUT /replies/{id}` - Update a reply.

### Correspondence
- `GET /cases/{caseId}/correspondence` - List correspondence for a case.
- `POST /cases/{caseId}/correspondence` - Create correspondence for a case.
- `PUT /correspondence/{id}` - Update correspondence entry.

### Case Diaries
- `GET /cases/{caseId}/diaries` - List diary entries for a case.
- `POST /cases/{caseId}/diaries` - Add a diary entry.
- `PUT /diaries/{id}` - Update a diary entry.

### Audit Logs
- `GET /audit-logs` - List audit logs (active by default). Query: `includeInactive=true`.

## Notes
- Downloads for notices/chargesheets are client-side in React, no file download endpoints.
- All list endpoints filter by `isActive = true` unless specified.
