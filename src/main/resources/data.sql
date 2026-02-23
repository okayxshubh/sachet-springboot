-- ==============================
-- Districts
-- ==============================
INSERT INTO mst_district (id, district_name)
VALUES
    (1, 'Shimla'),
    (2, 'Kullu'),
    (3, 'Mandi')
ON CONFLICT (id) DO NOTHING;

-- ==============================
-- Roles
-- ==============================
INSERT INTO mst_role (id, role_name)
VALUES
    (1, 'SuperAdmin'),
    (2, 'Admin'),
    (3, 'Staff')
ON CONFLICT (id) DO NOTHING;

-- ==============================
-- Ranks
-- ==============================
INSERT INTO mst_rank (id, rank_name)
VALUES
    (1, 'SHO (Station House Officer)'),
    (2, 'IO (Investigating Officer)'),
    (3, 'Other')
ON CONFLICT (id) DO NOTHING;

-- ==============================
-- Police Stations
-- ==============================
INSERT INTO mst_police_station (id, ps_name, district_id)
VALUES
    (1, 'Shimla PS', 1),
    (2, 'Kullu PS', 2),
    (3, 'Mandi PS', 3)
ON CONFLICT (id) DO NOTHING;

-- ==============================
-- Users
-- ==============================
INSERT INTO users (name, rank_id, ps_id, phone, role_id, password_hash, is_active, created_at, updated_at)
VALUES
    ('Rohit Malpani', 2, 1, '9816662225', 2,
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE,
     '2026-02-19 10:00:00+05:30'::timestamptz, '2026-02-19 10:00:00+05:30'::timestamptz),

    ('Anshita', 2, 1, '7580034077', 1,
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE,
     '2026-02-19 10:00:00+05:30'::timestamptz, '2026-02-19 10:00:00+05:30'::timestamptz),

    ('Prikshit', 2, 2, '6230775084', 1,
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE,
     '2026-02-19 10:00:00+05:30'::timestamptz, '2026-02-19 10:00:00+05:30'::timestamptz),

    ('Shubh', 2, 3, '7018437924', 1,
     '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE,
     '2026-02-19 10:00:00+05:30'::timestamptz, '2026-02-19 10:00:00+05:30'::timestamptz),

    ('GLOBAL SYSTEM USER', 2, 1, 'GLOBAL_USER', 1,
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE,
     NOW(), NOW())

ON CONFLICT (phone) DO NOTHING;

-- ==============================
-- Cases
-- ==============================
INSERT INTO cases
(fir_no, fir_year, ps_name, district, sections, summary,
 case_owner, assigned_to_user,
 is_active, created_by, updated_by, created_at, updated_at)
SELECT *
FROM (
    VALUES
    ('12',2026,'Sadar PS','Shimla','420, 406','Online fraud case',
     (SELECT id FROM users WHERE phone='7580034077' LIMIT 1),
     (SELECT id FROM users WHERE phone='9816662225' LIMIT 1),
     TRUE,'system','system',
     '2026-02-18 10:10:00+05:30'::timestamptz,
     '2026-02-18 10:10:00+05:30'::timestamptz),

    ('15',2026,'Lakkar Bazar PS','Shimla','66D, 43','Identity theft case',
     (SELECT id FROM users WHERE phone='7580034077' LIMIT 1),
     (SELECT id FROM users WHERE phone='6230775084' LIMIT 1),
     TRUE,'system','system',
     '2026-02-18 10:12:00+05:30'::timestamptz,
     '2026-02-18 10:12:00+05:30'::timestamptz),

    ('18',2026,'Boileauganj PS','Shimla','420, 120B','Loan app scam',
     (SELECT id FROM users WHERE phone='6230775084' LIMIT 1),
     (SELECT id FROM users WHERE phone='7018437924' LIMIT 1),
     TRUE,'system','system',
     '2026-02-18 10:14:00+05:30'::timestamptz,
     '2026-02-18 10:14:00+05:30'::timestamptz),

    ('21',2026,'Dhalli PS','Shimla','406, 467','Bank transfer fraud',
     (SELECT id FROM users WHERE phone='6230775084' LIMIT 1),
     (SELECT id FROM users WHERE phone='7580034077' LIMIT 1),
     TRUE,'system','system',
     '2026-02-18 10:16:00+05:30'::timestamptz,
     '2026-02-18 10:16:00+05:30'::timestamptz)
) AS v(
    fir_no,fir_year,ps_name,district,sections,summary,
    case_owner,assigned_to_user,
    is_active,created_by,updated_by,created_at,updated_at
)
WHERE case_owner IS NOT NULL
  AND assigned_to_user IS NOT NULL
ON CONFLICT (fir_no) DO NOTHING;

-- ==============================
-- Accused
-- ==============================
INSERT INTO accused (id, case_id, name, father_name, address, arrested, is_active, created_by, updated_by, created_at, updated_at)
VALUES
  (1, 1, 'Rahul Singh', 'Mahesh Singh', 'Shimla', FALSE, TRUE, 'system', 'system',
   '2026-02-18 10:20:00+05:30'::timestamptz,'2026-02-18 10:20:00+05:30'::timestamptz),
  (2, 2, 'Arjun Verma', 'Suresh Verma', 'Shimla', TRUE, TRUE, 'system', 'system',
   '2026-02-18 10:21:00+05:30'::timestamptz,'2026-02-18 10:21:00+05:30'::timestamptz),
  (3, 3, 'Karan Joshi', 'Vijay Joshi', 'Shimla', FALSE, TRUE, 'system', 'system',
   '2026-02-18 10:22:00+05:30'::timestamptz,'2026-02-18 10:22:00+05:30'::timestamptz),
  (4, 4, 'Nitin Pal', 'Sanjay Pal', 'Shimla', TRUE, TRUE, 'system', 'system',
   '2026-02-18 10:23:00+05:30'::timestamptz,'2026-02-18 10:23:00+05:30'::timestamptz)
ON CONFLICT (id) DO NOTHING;



-- ==============================
-- Audit Logs
-- ==============================
INSERT INTO audit_logs
(id, user_id, action, case_id, timestamp, is_active, created_by, updated_by, created_at, updated_at)
VALUES
  (1, 1, 'Case created', 1,
   '2026-02-18 10:55:00+05:30'::timestamptz,
   TRUE, 'system', 'system',
   '2026-02-18 10:55:00+05:30'::timestamptz,
   '2026-02-18 10:55:00+05:30'::timestamptz),

  (2, 2, 'Accused added', 2,
   '2026-02-18 10:56:00+05:30'::timestamptz,
   TRUE, 'system', 'system',
   '2026-02-18 10:56:00+05:30'::timestamptz,
   '2026-02-18 10:56:00+05:30'::timestamptz),

  (3, 3, 'Notice issued', 3,
   '2026-02-18 10:57:00+05:30'::timestamptz,
   TRUE, 'system', 'system',
   '2026-02-18 10:57:00+05:30'::timestamptz,
   '2026-02-18 10:57:00+05:30'::timestamptz),

  (4, 4, 'Diary updated', 4,
   '2026-02-18 10:58:00+05:30'::timestamptz,
   TRUE, 'system', 'system',
   '2026-02-18 10:58:00+05:30'::timestamptz,
   '2026-02-18 10:58:00+05:30'::timestamptz),

  (5, 1, 'Transaction added', 1,
   '2026-02-18 11:00:00+05:30'::timestamptz,
   TRUE, 'system', 'system',
   '2026-02-18 11:00:00+05:30'::timestamptz,
   '2026-02-18 11:00:00+05:30'::timestamptz),

  (6, 2, 'Notice reply recorded', 2,
   '2026-02-18 11:02:00+05:30'::timestamptz,
   TRUE, 'system', 'system',
   '2026-02-18 11:02:00+05:30'::timestamptz,
   '2026-02-18 11:02:00+05:30'::timestamptz)

ON CONFLICT (id) DO NOTHING;