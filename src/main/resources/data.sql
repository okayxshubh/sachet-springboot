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
INSERT INTO users (name, rank_id, ps_id, phone, role_id, password_hash, is_enabled, is_active, created_at, updated_at)
VALUES
    ('Rohit Malpani', 2, 1, '9816662225', 2,
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, '2026-02-19 10:00:00+05:30', '2026-02-19 10:00:00+05:30'),

    ('Anshita', 2, 1, '7580034077', 1,
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, '2026-02-19 10:00:00+05:30', '2026-02-19 10:00:00+05:30'),

    ('Prikshit', 2, 2, '6230775084', 1,
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, '2026-02-19 10:00:00+05:30', '2026-02-19 10:00:00+05:30'),

    ('Shubh', 2, 3, '7018437924', 1,
     '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, TRUE, '2026-02-19 10:00:00+05:30', '2026-02-19 10:00:00+05:30')
ON CONFLICT (phone) DO NOTHING;

-- ==============================
-- Cases
-- ==============================
INSERT INTO cases (id, fir_no, fir_year, ps_name, district, sections, summary, created_by_user, assigned_to_user, is_active, created_by, updated_by, created_at, updated_at)
VALUES
  (1, '12', 2026, 'Sadar PS', 'Shimla', '420, 406', 'Online fraud case', 1, 2, TRUE, 'system', 'system', '2026-02-18 10:10:00', '2026-02-18 10:10:00'),
  (2, '15', 2026, 'Lakkar Bazar PS', 'Shimla', '66D, 43', 'Identity theft case', 1, 3, TRUE, 'system', 'system', '2026-02-18 10:12:00', '2026-02-18 10:12:00'),
  (3, '18', 2026, 'Boileauganj PS', 'Shimla', '420, 120B', 'Loan app scam', 2, 4, TRUE, 'system', 'system', '2026-02-18 10:14:00', '2026-02-18 10:14:00'),
  (4, '21', 2026, 'Dhalli PS', 'Shimla', '406, 467', 'Bank transfer fraud', 2, 1, TRUE, 'system', 'system', '2026-02-18 10:16:00', '2026-02-18 10:16:00')
ON CONFLICT (id) DO NOTHING;

-- ==============================
-- Accused
-- ==============================
INSERT INTO accused (id, case_id, name, father_name, address, arrested, is_active, created_by, updated_by, created_at, updated_at)
VALUES
  (1, 1, 'Rahul Singh', 'Mahesh Singh', 'Shimla', FALSE, TRUE, 'system', 'system', '2026-02-18 10:20:00', '2026-02-18 10:20:00'),
  (2, 2, 'Arjun Verma', 'Suresh Verma', 'Shimla', TRUE, TRUE, 'system', 'system', '2026-02-18 10:21:00', '2026-02-18 10:21:00'),
  (3, 3, 'Karan Joshi', 'Vijay Joshi', 'Shimla', FALSE, TRUE, 'system', 'system', '2026-02-18 10:22:00', '2026-02-18 10:22:00'),
  (4, 4, 'Nitin Pal', 'Sanjay Pal', 'Shimla', TRUE, TRUE, 'system', 'system', '2026-02-18 10:23:00', '2026-02-18 10:23:00')
ON CONFLICT (id) DO NOTHING;

-- ==============================
-- NCRP Transactions
-- ==============================
INSERT INTO ncrp_transactions (id, case_id, ack_no, bank_name, account_no, transaction_date, amount, layer, is_active, created_by, updated_by, created_at, updated_at)
VALUES
  (1, 1, 'NCRP-ACK-001', 'SBI', '123456789012', '2026-02-18', 125000.00, 'L1', TRUE, 'system', 'system', '2026-02-18 10:25:00', '2026-02-18 10:25:00'),
  (2, 2, 'NCRP-ACK-002', 'HDFC', '223456789012', '2026-02-18', 56000.00, 'L2', TRUE, 'system', 'system', '2026-02-18 10:26:00', '2026-02-18 10:26:00'),
  (3, 3, 'NCRP-ACK-003', 'ICICI', '323456789012', '2026-02-18', 87000.00, 'L1', TRUE, 'system', 'system', '2026-02-18 10:27:00', '2026-02-18 10:27:00'),
  (4, 4, 'NCRP-ACK-004', 'PNB', '423456789012', '2026-02-18', 43000.00, 'L3', TRUE, 'system', 'system', '2026-02-18 10:28:00', '2026-02-18 10:28:00')
ON CONFLICT (id) DO NOTHING;

-- ==============================
-- Notices
-- ==============================
INSERT INTO notices (id, case_id, notice_id, notice_type, issued_to, issued_date, status, is_active, created_by, updated_by, created_at, updated_at)
VALUES
  (1, 1, 'NOTICE-001', 'Bank Freeze', 'SBI', '2026-02-18', 'Pending', TRUE, 'system', 'system', '2026-02-18 10:30:00', '2026-02-18 10:30:00'),
  (2, 2, 'NOTICE-002', 'KYC Request', 'HDFC', '2026-02-18', 'Pending', TRUE, 'system', 'system', '2026-02-18 10:31:00', '2026-02-18 10:31:00'),
  (3, 3, 'NOTICE-003', 'Account Details', 'ICICI', '2026-02-18', 'Pending', TRUE, 'system', 'system', '2026-02-18 10:32:00', '2026-02-18 10:32:00'),
  (4, 4, 'NOTICE-004', 'Fund Trail', 'PNB', '2026-02-18', 'Pending', TRUE, 'system', 'system', '2026-02-18 10:33:00', '2026-02-18 10:33:00')
ON CONFLICT (id) DO NOTHING;

-- ==============================
-- Notice-Transaction Mapping
-- ==============================
INSERT INTO notice_transactions (id, notice_id, transaction_id, is_active, created_by, updated_by, created_at, updated_at)
VALUES
  (1, 1, 1, TRUE, 'system', 'system', '2026-02-18 10:35:00', '2026-02-18 10:35:00'),
  (2, 2, 2, TRUE, 'system', 'system', '2026-02-18 10:36:00', '2026-02-18 10:36:00'),
  (3, 3, 3, TRUE, 'system', 'system', '2026-02-18 10:37:00', '2026-02-18 10:37:00'),
  (4, 4, 4, TRUE, 'system', 'system', '2026-02-18 10:38:00', '2026-02-18 10:38:00')
ON CONFLICT (id) DO NOTHING;

-- ==============================
-- Notice Replies
-- ==============================
INSERT INTO notice_replies (id, notice_id, reply_date, summary, status, is_active, created_by, updated_by, created_at, updated_at)
VALUES
  (1, 1, '2026-02-18', 'Account frozen', 'Complete', TRUE, 'system', 'system', '2026-02-18 10:40:00', '2026-02-18 10:40:00'),
  (2, 2, '2026-02-18', 'KYC shared', 'Complete', TRUE, 'system', 'system', '2026-02-18 10:41:00', '2026-02-18 10:41:00'),
  (3, 3, '2026-02-18', 'Details pending', 'Partial', TRUE, 'system', 'system', '2026-02-18 10:42:00', '2026-02-18 10:42:00'),
  (4, 4, '2026-02-18', 'No response yet', 'No Response', TRUE, 'system', 'system', '2026-02-18 10:43:00', '2026-02-18 10:43:00')
ON CONFLICT (id) DO NOTHING;

-- ==============================
-- Correspondence
-- ==============================
INSERT INTO correspondence (id, case_id, type, subject, date_sent, reply_summary, is_active, created_by, updated_by, created_at, updated_at)
VALUES
  (1, 1, 'Letter', 'Request for KYC', '2026-02-18', 'Awaiting response', TRUE, 'system', 'system', '2026-02-18 10:45:00', '2026-02-18 10:45:00'),
  (2, 2, 'Email', 'Account statement', '2026-02-18', 'Received statement', TRUE, 'system', 'system', '2026-02-18 10:46:00', '2026-02-18 10:46:00'),
  (3, 3, 'Letter', 'Freeze request', '2026-02-18', 'In process', TRUE, 'system', 'system', '2026-02-18 10:47:00', '2026-02-18 10:47:00'),
  (4, 4, 'Email', 'Transaction trail', '2026-02-18', 'Partial info', TRUE, 'system', 'system', '2026-02-18 10:48:00', '2026-02-18 10:48:00')
ON CONFLICT (id) DO NOTHING;

-- ==============================
-- Case Diaries
-- ==============================
INSERT INTO case_diaries (id, case_id, diary_date, content, version, is_active, created_by, updated_by, created_at, updated_at)
VALUES
  (1, 1, '2026-02-18', 'Initial inquiry completed', 1, TRUE, 'system', 'system', '2026-02-18 10:50:00', '2026-02-18 10:50:00'),
  (2, 2, '2026-02-18', 'Victim statement recorded', 1, TRUE, 'system', 'system', '2026-02-18 10:51:00', '2026-02-18 10:51:00'),
  (3, 3, '2026-02-18', 'Notice prepared', 1, TRUE, 'system', 'system', '2026-02-18 10:52:00', '2026-02-18 10:52:00'),
  (4, 4, '2026-02-18', 'Bank follow-up done', 1, TRUE, 'system', 'system', '2026-02-18 10:53:00', '2026-02-18 10:53:00')
ON CONFLICT (id) DO NOTHING;

-- ==============================
-- Audit Logs
-- ==============================
INSERT INTO audit_logs (id, user_id, action, case_id, timestamp, is_active, created_by, updated_by, created_at, updated_at)
VALUES
  (1, 1, 'Case created', 1, '2026-02-18 10:55:00', TRUE, 'system', 'system', '2026-02-18 10:55:00', '2026-02-18 10:55:00'),
  (2, 2, 'Accused added', 2, '2026-02-18 10:56:00', TRUE, 'system', 'system', '2026-02-18 10:56:00', '2026-02-18 10:56:00'),
  (3, 3, 'Notice issued', 3, '2026-02-18 10:57:00', TRUE, 'system', 'system', '2026-02-18 10:57:00', '2026-02-18 10:57:00'),
  (4, 4, 'Diary updated', 4, '2026-02-18 10:58:00', TRUE, 'system', 'system', '2026-02-18 10:58:00', '2026-02-18 10:58:00')
ON CONFLICT (id) DO NOTHING;
