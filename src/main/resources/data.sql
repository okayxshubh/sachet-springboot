-- ==============================
-- Districts
-- ==============================
INSERT INTO mst_district (id, district_name)
VALUES
    (1, 'Shimla'),
    (2, 'Kullu'),
    (3, 'Mandi')
ON CONFLICT (id) DO UPDATE
SET district_name = EXCLUDED.district_name;

-- ==============================
-- Roles
-- ==============================
INSERT INTO mst_role (id, role_name)
VALUES
    (1, 'SuperAdmin'),
    (2, 'Admin'),
    (3, 'Staff')
ON CONFLICT (id) DO UPDATE
SET role_name = EXCLUDED.role_name;

-- ==============================
-- Ranks
-- ==============================
INSERT INTO mst_rank (id, rank_name)
VALUES
    (1, 'SHO (Station House Officer)'),
    (2, 'IO (Investigating Officer)'),
    (3, 'Other')
ON CONFLICT (id) DO UPDATE
SET rank_name = EXCLUDED.rank_name;

-- ==============================
-- Police Stations
-- ==============================
INSERT INTO mst_police_station (id, ps_name, district_id)
VALUES
    (1, 'Shimla PS', 1),
    (2, 'Kullu PS', 2),
    (3, 'Mandi PS', 3)
ON CONFLICT (id) DO UPDATE
SET ps_name = EXCLUDED.ps_name,
    district_id = EXCLUDED.district_id;

-- ==============================
-- Users
-- ==============================
INSERT INTO users (name, rank_id, ps_id, phone, role_id, password_hash, is_active, is_approved, created_at, updated_at)
SELECT
    v.name,
    r.id,
    ps.id,
    v.phone,
    rl.id,
    v.password_hash,
    v.is_active,
    v.is_approved,
    v.created_at,
    v.updated_at
FROM (
    VALUES
    ('Rohit Malpani', 'IO (Investigating Officer)', 'Shimla PS', '9816662225', 'Admin',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE,
     '2026-02-19 10:00:00+05:30'::timestamptz, '2026-02-19 10:00:00+05:30'::timestamptz),
    ('Anshita', 'IO (Investigating Officer)', 'Shimla PS', '7580034077', 'SuperAdmin',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE,
     '2026-02-19 10:00:00+05:30'::timestamptz, '2026-02-19 10:00:00+05:30'::timestamptz),
    ('Prikshit', 'IO (Investigating Officer)', 'Kullu PS', '6230775084', 'SuperAdmin',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE,
     '2026-02-19 10:00:00+05:30'::timestamptz, '2026-02-19 10:00:00+05:30'::timestamptz),
    ('Shubh', 'IO (Investigating Officer)', 'Mandi PS', '7018437924', 'SuperAdmin',
     '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, TRUE,
     '2026-02-19 10:00:00+05:30'::timestamptz, '2026-02-19 10:00:00+05:30'::timestamptz),
    ('Pending User 1', 'Other', 'Shimla PS', '9000000001', 'Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, FALSE,
     '2026-02-19 10:05:00+05:30'::timestamptz, '2026-02-19 10:05:00+05:30'::timestamptz),
    ('Pending User 2', 'Other', 'Kullu PS', '9000000002', 'Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, FALSE,
     '2026-02-19 10:06:00+05:30'::timestamptz, '2026-02-19 10:06:00+05:30'::timestamptz),
    ('Pending User 3', 'Other', 'Mandi PS', '9000000003', 'Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, FALSE,
     '2026-02-19 10:07:00+05:30'::timestamptz, '2026-02-19 10:07:00+05:30'::timestamptz),
    ('GLOBAL SYSTEM USER', 'IO (Investigating Officer)', 'Shimla PS', 'GLOBAL_USER', 'SuperAdmin',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW())
) AS v(name, rank_name, ps_name, phone, role_name, password_hash, is_active, is_approved, created_at, updated_at)
JOIN mst_rank r ON r.rank_name = v.rank_name
JOIN mst_police_station ps ON ps.ps_name = v.ps_name
JOIN mst_role rl ON rl.role_name = v.role_name
ON CONFLICT (phone) DO UPDATE
SET name = EXCLUDED.name,
    rank_id = EXCLUDED.rank_id,
    ps_id = EXCLUDED.ps_id,
    role_id = EXCLUDED.role_id,
    password_hash = EXCLUDED.password_hash,
    is_active = EXCLUDED.is_active,
    is_approved = EXCLUDED.is_approved,
    updated_at = EXCLUDED.updated_at;

-- ==============================
-- Case Status
-- ==============================
INSERT INTO mst_case_status (id, case_status)
VALUES
    (1, 'Under Investigation'),
    (2, 'Cancelled'),
    (3, 'Untraced')
ON CONFLICT (id) DO UPDATE
SET case_status = EXCLUDED.case_status;

-- ==============================
-- Cases
-- ==============================
INSERT INTO cases (
    id, created_at, created_by, is_active, updated_at, updated_by,
    district_id, fir_no, fir_year, ps_id, sections, summary,
    case_owner, case_status
)
VALUES
    (1, NOW(), 1, TRUE, NOW(), 1, 1, 'FIR-1001', 2026, 1, 'IPC 420,406', 'Fraud investigation', 1, 1),
    (2, NOW(), 2, TRUE, NOW(), 2, 2, 'FIR-1002', 2026, 2, 'IPC 379', 'Theft investigation', 3, 2),
    (3, NOW(), 3, TRUE, NOW(), 3, 3, 'FIR-1003', 2026, 3, 'IPC 302', 'Homicide investigation', 4, 1),
    (4, NOW(), 1, TRUE, NOW(), 1, 1, 'FIR-1004', 2026, 1, 'IPC 498A', 'Domestic dispute', 2, 3),
    (5, NOW(), 3, TRUE, NOW(), 3, 2, 'FIR-1005', 2026, 2, 'IPC 354', 'Assault investigation', 3, 1)
ON CONFLICT (id) DO UPDATE
SET updated_at = EXCLUDED.updated_at,
    updated_by = EXCLUDED.updated_by,
    district_id = EXCLUDED.district_id,
    fir_no = EXCLUDED.fir_no,
    fir_year = EXCLUDED.fir_year,
    ps_id = EXCLUDED.ps_id,
    sections = EXCLUDED.sections,
    summary = EXCLUDED.summary,
    case_owner = EXCLUDED.case_owner,
    case_status = EXCLUDED.case_status,
    is_active = EXCLUDED.is_active;

-- ==============================
-- Case Assigned Users (Many-to-Many)
-- ==============================
INSERT INTO case_assigned_users (case_id, user_id)
VALUES
    (1, 1), (1, 3), (1, 4),
    (2, 2), (2, 3),
    (3, 3), (3, 1),
    (4, 4), (4, 5),
    (5, 2), (5, 5)
ON CONFLICT DO NOTHING;

-- ==============================
-- Notice Types
-- ==============================
INSERT INTO mst_notice_type (id, notice_type_name)
VALUES
    (1, '791 Act for Meta'),
    (2, '64 BNSS for Meta Platform'),
    (3, '35 (3) Summon for Appearance by Police'),
    (4, '94 & 106 BNSS for Bank'),
    (5, '95 BNSS for CDR/CAF/IPDR'),
    (6, '94 BNSS CCTV/ATM Footage')
ON CONFLICT (id) DO UPDATE
SET notice_type_name = EXCLUDED.notice_type_name;
