# DATABASE CONFIGS (PostgreSQL)

## 1. Database Design Principles
The database follows a relational structure using PostgreSQL. Foreign key constraints are enforced. All critical relationships are normalized. Audit integrity and referential integrity are mandatory.
Comment: Soft deletion uses `is_active` in all tables (from BaseEntity).

## 2. Enable Foreign Keys
PostgreSQL enforces foreign keys by default; no PRAGMA required.
Comment: No additional session-level configuration needed for FK enforcement.

## 3. Users Table
```sql
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,                       -- Unique user ID
    name TEXT NOT NULL,                              -- Officer name
    rank TEXT,                                       -- Officer rank
    ps_name TEXT NOT NULL,                           -- Police station name
    district TEXT NOT NULL,                          -- District name
    phone TEXT,                                      -- Contact number
    role TEXT CHECK(role IN ('SHO','IO')) NOT NULL,  -- Role restriction
    password_hash TEXT NOT NULL,                     -- bcrypt hash
    is_active BOOLEAN DEFAULT TRUE,                  -- Soft delete flag
    created_at TIMESTAMPTZ DEFAULT NOW()             -- Creation timestamp
);
```

## 4. Cases Table
```sql
CREATE TABLE cases (
    id BIGSERIAL PRIMARY KEY,                        -- Unique case ID
    fir_no TEXT NOT NULL,                             -- FIR number
    fir_year INTEGER NOT NULL,                        -- FIR year
    ps_name TEXT NOT NULL,                            -- Police station
    district TEXT NOT NULL,                           -- District
    sections TEXT,                                    -- IPC sections
    summary TEXT NOT NULL,                            -- Case summary
    created_by BIGINT NOT NULL,                       -- Creator user ID
    assigned_to BIGINT NOT NULL,                      -- Assigned IO user ID
    is_active BOOLEAN DEFAULT TRUE,                   -- Soft delete flag
    created_at TIMESTAMPTZ DEFAULT NOW(),             -- Creation timestamp
    CONSTRAINT fk_cases_created_by
        FOREIGN KEY(created_by) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT fk_cases_assigned_to
        FOREIGN KEY(assigned_to) REFERENCES users(id) ON DELETE RESTRICT,
    CONSTRAINT uq_cases_fir UNIQUE(fir_no, fir_year, ps_name)
);
```

## 5. Accused Table
```sql
CREATE TABLE accused (
    id BIGSERIAL PRIMARY KEY,                        -- Accused ID
    case_id BIGINT NOT NULL,                          -- Linked case ID
    name TEXT NOT NULL,                               -- Accused name
    father_name TEXT,                                 -- Father name
    address TEXT,                                     -- Address
    arrested BOOLEAN DEFAULT FALSE,                   -- Arrest flag
    is_active BOOLEAN DEFAULT TRUE,                   -- Soft delete flag
    CONSTRAINT fk_accused_case
        FOREIGN KEY(case_id) REFERENCES cases(id) ON DELETE CASCADE
);
```

## 6. NCRP Transactions Table
```sql
CREATE TABLE ncrp_transactions (
    id BIGSERIAL PRIMARY KEY,                         -- Transaction ID
    case_id BIGINT NOT NULL,                           -- Linked case ID
    ack_no TEXT,                                       -- NCRP acknowledgement
    bank_name TEXT NOT NULL,                           -- Bank name
    account_no TEXT,                                   -- Account number
    transaction_date DATE,                             -- Transaction date
    amount NUMERIC(12,2) NOT NULL,                     -- Transaction amount
    layer TEXT,                                        -- Layer/chain stage
    is_active BOOLEAN DEFAULT TRUE,                    -- Soft delete flag
    created_at TIMESTAMPTZ DEFAULT NOW(),              -- Creation timestamp
    CONSTRAINT fk_ncrp_case
        FOREIGN KEY(case_id) REFERENCES cases(id) ON DELETE CASCADE
);
```

## 7. Notices Table
```sql
CREATE TABLE notices (
    id BIGSERIAL PRIMARY KEY,                          -- Notice ID
    case_id BIGINT NOT NULL,                            -- Linked case ID
    notice_id TEXT UNIQUE NOT NULL,                     -- Public notice identifier
    notice_type TEXT NOT NULL,                          -- Notice type
    issued_to TEXT NOT NULL,                            -- Recipient
    issued_date DATE NOT NULL,                          -- Date of issue
    status TEXT DEFAULT 'Pending',                      -- Status
    is_active BOOLEAN DEFAULT TRUE,                     -- Soft delete flag
    created_at TIMESTAMPTZ DEFAULT NOW(),               -- Creation timestamp
    CONSTRAINT fk_notices_case
        FOREIGN KEY(case_id) REFERENCES cases(id) ON DELETE CASCADE
);
```

## 8. Notice-Transaction Mapping Table
```sql
CREATE TABLE notice_transactions (
    id BIGSERIAL PRIMARY KEY,                           -- Mapping ID
    notice_id BIGINT NOT NULL,                           -- Linked notice
    transaction_id BIGINT NOT NULL,                      -- Linked transaction
    is_active BOOLEAN DEFAULT TRUE,                      -- Soft delete flag
    CONSTRAINT fk_notice_tx_notice
        FOREIGN KEY(notice_id) REFERENCES notices(id) ON DELETE CASCADE,
    CONSTRAINT fk_notice_tx_transaction
        FOREIGN KEY(transaction_id) REFERENCES ncrp_transactions(id) ON DELETE CASCADE,
    CONSTRAINT uq_notice_tx UNIQUE(notice_id, transaction_id)
);
```

## 9. Notice Replies Table
```sql
CREATE TABLE notice_replies (
    id BIGSERIAL PRIMARY KEY,                            -- Reply ID
    notice_id BIGINT NOT NULL,                            -- Linked notice
    reply_date DATE NOT NULL,                             -- Reply date
    summary TEXT NOT NULL,                                -- Reply summary
    status TEXT CHECK(status IN ('Complete','Partial','No Response')),
    is_active BOOLEAN DEFAULT TRUE,                       -- Soft delete flag
    created_at TIMESTAMPTZ DEFAULT NOW(),                 -- Creation timestamp
    CONSTRAINT fk_notice_reply_notice
        FOREIGN KEY(notice_id) REFERENCES notices(id) ON DELETE CASCADE
);
```

## 10. Correspondence Table
```sql
CREATE TABLE correspondence (
    id BIGSERIAL PRIMARY KEY,                             -- Correspondence ID
    case_id BIGINT NOT NULL,                               -- Linked case
    type TEXT NOT NULL,                                    -- Type (Email/Letter/etc)
    subject TEXT NOT NULL,                                 -- Subject
    date_sent DATE NOT NULL,                               -- Sent date
    reply_summary TEXT,                                    -- Reply summary
    is_active BOOLEAN DEFAULT TRUE,                        -- Soft delete flag
    created_at TIMESTAMPTZ DEFAULT NOW(),                  -- Creation timestamp
    CONSTRAINT fk_correspondence_case
        FOREIGN KEY(case_id) REFERENCES cases(id) ON DELETE CASCADE
);
```

## 11. Case Diaries Table
```sql
CREATE TABLE case_diaries (
    id BIGSERIAL PRIMARY KEY,                             -- Diary entry ID
    case_id BIGINT NOT NULL,                               -- Linked case
    diary_date DATE NOT NULL,                              -- Diary date
    content TEXT NOT NULL,                                 -- Diary content
    version INTEGER NOT NULL DEFAULT 1,                    -- Versioning
    is_active BOOLEAN DEFAULT TRUE,                        -- Soft delete flag
    created_at TIMESTAMPTZ DEFAULT NOW(),                  -- Creation timestamp
    CONSTRAINT fk_diary_case
        FOREIGN KEY(case_id) REFERENCES cases(id) ON DELETE CASCADE,
    CONSTRAINT uq_diary_version UNIQUE(case_id, diary_date, version)
);
```

## 12. Audit Logs Table
```sql
CREATE TABLE audit_logs (
    id BIGSERIAL PRIMARY KEY,                             -- Audit entry ID
    user_id BIGINT NOT NULL,                               -- Actor user
    action TEXT NOT NULL,                                  -- Action summary
    case_id BIGINT,                                        -- Optional linked case
    is_active BOOLEAN DEFAULT TRUE,                        -- Soft delete flag
    timestamp TIMESTAMPTZ DEFAULT NOW(),                   -- Action time
    CONSTRAINT fk_audit_user
        FOREIGN KEY(user_id) REFERENCES users(id) ON DELETE CASCADE,
    CONSTRAINT fk_audit_case
        FOREIGN KEY(case_id) REFERENCES cases(id) ON DELETE SET NULL
);
```

## 13. Index Recommendations
```sql
CREATE INDEX idx_cases_fir ON cases(fir_no, fir_year);              -- FIR lookup
CREATE INDEX idx_notices_case ON notices(case_id);                  -- Notice lookup
CREATE INDEX idx_transactions_case ON ncrp_transactions(case_id);   -- Transaction lookup
CREATE INDEX idx_diary_case ON case_diaries(case_id);               -- Diary lookup
```

## 14. Others
- Password hashing using bcrypt
- Role-based access control (SHO / IO)
Comment: These are enforced at the service and security layer, not at the SQL layer.
