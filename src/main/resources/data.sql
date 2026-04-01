-- ==============================
-- Schema guardrails (safe on reset/re-run)
-- ==============================
ALTER TABLE IF EXISTS users
    ADD COLUMN IF NOT EXISTS is_approved BOOLEAN;

UPDATE users
SET is_approved = FALSE
WHERE is_approved IS NULL;

ALTER TABLE IF EXISTS users
    ALTER COLUMN is_approved SET DEFAULT FALSE;

ALTER TABLE IF EXISTS users
    ALTER COLUMN is_approved SET NOT NULL;

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

SELECT setval(pg_get_serial_sequence('mst_district', 'id'), COALESCE(MAX(id), 1), TRUE)
FROM mst_district;

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

SELECT setval(pg_get_serial_sequence('mst_role', 'id'), COALESCE(MAX(id), 1), TRUE)
FROM mst_role;

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

SELECT setval(pg_get_serial_sequence('mst_rank', 'id'), COALESCE(MAX(id), 1), TRUE)
FROM mst_rank;

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

SELECT setval(pg_get_serial_sequence('mst_police_station', 'id'), COALESCE(MAX(id), 1), TRUE)
FROM mst_police_station;

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
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE,
     NOW(), NOW())
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
-- Cases
-- ==============================
INSERT INTO cases
(fir_no, fir_year, ps_name, district, sections, summary,
 case_owner, assigned_to_user,
 is_active, created_by, updated_by, created_at, updated_at)
SELECT
    v.fir_no,
    v.fir_year,
    v.ps_name,
    v.district,
    v.sections,
    v.summary,
    owner_user.id,
    assigned_user.id,
    v.is_active,
    v.created_by,
    v.updated_by,
    v.created_at,
    v.updated_at
FROM (
    VALUES
    ('12', 2026, 'Sadar PS', 'Shimla', '420, 406', 'Online fraud case',
     '7580034077', '9816662225', TRUE, 'system', 'system',
     '2026-02-18 10:10:00+05:30'::timestamptz,
     '2026-02-18 10:10:00+05:30'::timestamptz),

    ('15', 2026, 'Lakkar Bazar PS', 'Shimla', '66D, 43', 'Identity theft case',
     '7580034077', '6230775084', TRUE, 'system', 'system',
     '2026-02-18 10:12:00+05:30'::timestamptz,
     '2026-02-18 10:12:00+05:30'::timestamptz),

    ('18', 2026, 'Boileauganj PS', 'Shimla', '420, 120B', 'Loan app scam',
     '6230775084', '7018437924', TRUE, 'system', 'system',
     '2026-02-18 10:14:00+05:30'::timestamptz,
     '2026-02-18 10:14:00+05:30'::timestamptz),

    ('21', 2026, 'Dhalli PS', 'Shimla', '406, 467', 'Bank transfer fraud',
     '6230775084', '7580034077', TRUE, 'system', 'system',
     '2026-02-18 10:16:00+05:30'::timestamptz,
     '2026-02-18 10:16:00+05:30'::timestamptz)
) AS v(
    fir_no, fir_year, ps_name, district, sections, summary,
    owner_phone, assigned_phone,
    is_active, created_by, updated_by, created_at, updated_at
)
JOIN users owner_user ON owner_user.phone = v.owner_phone
JOIN users assigned_user ON assigned_user.phone = v.assigned_phone
ON CONFLICT (fir_no) DO UPDATE
SET fir_year = EXCLUDED.fir_year,
    ps_name = EXCLUDED.ps_name,
    district = EXCLUDED.district,
    sections = EXCLUDED.sections,
    summary = EXCLUDED.summary,
    case_owner = EXCLUDED.case_owner,
    assigned_to_user = EXCLUDED.assigned_to_user,
    is_active = EXCLUDED.is_active,
    updated_by = EXCLUDED.updated_by,
    updated_at = EXCLUDED.updated_at;

-- ==============================
-- Accused
-- ==============================
INSERT INTO accused (case_id, name, father_name, address, arrested, is_active, created_by, updated_by, created_at, updated_at)
SELECT
    c.id,
    v.name,
    v.father_name,
    v.address,
    v.arrested,
    v.is_active,
    v.created_by,
    v.updated_by,
    v.created_at,
    v.updated_at
FROM (
    VALUES
    ('12', 'Rahul Singh', 'Mahesh Singh', 'Shimla', FALSE, TRUE, 'system', 'system',
     '2026-02-18 10:20:00+05:30'::timestamptz, '2026-02-18 10:20:00+05:30'::timestamptz),

    ('15', 'Arjun Verma', 'Suresh Verma', 'Shimla', TRUE, TRUE, 'system', 'system',
     '2026-02-18 10:21:00+05:30'::timestamptz, '2026-02-18 10:21:00+05:30'::timestamptz),

    ('18', 'Karan Joshi', 'Vijay Joshi', 'Shimla', FALSE, TRUE, 'system', 'system',
     '2026-02-18 10:22:00+05:30'::timestamptz, '2026-02-18 10:22:00+05:30'::timestamptz),

    ('21', 'Nitin Pal', 'Sanjay Pal', 'Shimla', TRUE, TRUE, 'system', 'system',
     '2026-02-18 10:23:00+05:30'::timestamptz, '2026-02-18 10:23:00+05:30'::timestamptz)
) AS v(fir_no, name, father_name, address, arrested, is_active, created_by, updated_by, created_at, updated_at)
JOIN cases c ON c.fir_no = v.fir_no
WHERE NOT EXISTS (
    SELECT 1
    FROM accused a
    WHERE a.case_id = c.id
      AND a.name = v.name
      AND COALESCE(a.father_name, '') = COALESCE(v.father_name, '')
);

SELECT setval(pg_get_serial_sequence('accused', 'id'), COALESCE(MAX(id), 1), TRUE)
FROM accused;

-- ==============================
-- Audit Logs
-- ==============================
INSERT INTO audit_logs
(user_id, action, case_id, timestamp, is_active, created_by, updated_by, created_at, updated_at)
SELECT
    u.id,
    v.action,
    c.id,
    v.ts,
    v.is_active,
    v.created_by,
    v.updated_by,
    v.created_at,
    v.updated_at
FROM (
    VALUES
    ('9816662225', '12', 'Case created',
     '2026-02-18 10:55:00+05:30'::timestamptz, TRUE, 'system', 'system',
     '2026-02-18 10:55:00+05:30'::timestamptz, '2026-02-18 10:55:00+05:30'::timestamptz),

    ('7580034077', '15', 'Accused added',
     '2026-02-18 10:56:00+05:30'::timestamptz, TRUE, 'system', 'system',
     '2026-02-18 10:56:00+05:30'::timestamptz, '2026-02-18 10:56:00+05:30'::timestamptz),

    ('6230775084', '18', 'Notice issued',
     '2026-02-18 10:57:00+05:30'::timestamptz, TRUE, 'system', 'system',
     '2026-02-18 10:57:00+05:30'::timestamptz, '2026-02-18 10:57:00+05:30'::timestamptz),

    ('7018437924', '21', 'Diary updated',
     '2026-02-18 10:58:00+05:30'::timestamptz, TRUE, 'system', 'system',
     '2026-02-18 10:58:00+05:30'::timestamptz, '2026-02-18 10:58:00+05:30'::timestamptz),

    ('9816662225', '12', 'Transaction added',
     '2026-02-18 11:00:00+05:30'::timestamptz, TRUE, 'system', 'system',
     '2026-02-18 11:00:00+05:30'::timestamptz, '2026-02-18 11:00:00+05:30'::timestamptz),

    ('7580034077', '15', 'Notice reply recorded',
     '2026-02-18 11:02:00+05:30'::timestamptz, TRUE, 'system', 'system',
     '2026-02-18 11:02:00+05:30'::timestamptz, '2026-02-18 11:02:00+05:30'::timestamptz)
) AS v(phone, fir_no, action, ts, is_active, created_by, updated_by, created_at, updated_at)
JOIN users u ON u.phone = v.phone
LEFT JOIN cases c ON c.fir_no = v.fir_no
WHERE NOT EXISTS (
    SELECT 1
    FROM audit_logs al
    WHERE al.user_id = u.id
      AND COALESCE(al.case_id, -1) = COALESCE(c.id, -1)
      AND al.action = v.action
      AND al.timestamp = v.ts
);

SELECT setval(pg_get_serial_sequence('audit_logs', 'id'), COALESCE(MAX(id), 1), TRUE)
FROM audit_logs;




-- #### Notice Types to be generated ####
--INSERT INTO templates (id, title) VALUES
--  ('791-meta', '791 Act for Meta'),
--  ('64-bnss-meta', '64 BNSS for Meta Platform'),
--  ('35-summon', '35 (3) Summon for Appearance by Police'),
--  ('94-106-bank', '94 & 106 BNSS for Bank'),
--  ('95-cdr', '95 BNSS for CDR/CAF/IPDR'),
--  ('94-cctv', '94 BNSS CCTV/ATM Footage');
