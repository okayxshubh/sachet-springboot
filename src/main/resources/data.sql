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
SET
    ps_name = EXCLUDED.ps_name,
    district_id = EXCLUDED.district_id;

-- ==============================
-- Users
-- ==============================
INSERT INTO users (
    name,
    rank_id,
    ps_id,
    phone,
    role_id,
    password_hash,
    is_active,
    is_approved,
    created_at,
    updated_at
)
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

    -- MAIN USERS
    ('Rohit Malpani','SHO (Station House Officer)','Shimla PS','9816662225','Admin',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,TRUE,NOW(),NOW()),

    ('Anshita','SHO (Station House Officer)','Shimla PS','7580034077','SuperAdmin',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,TRUE,NOW(),NOW()),

    ('Prikshit','SHO (Station House Officer)','Kullu PS','6230775084','SuperAdmin',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,TRUE,NOW(),NOW()),

    ('Shubh','SHO (Station House Officer)','Mandi PS','7018437924','SuperAdmin',
     '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6',TRUE,TRUE,NOW(),NOW()),

    ('Brijesh','SHO (Station House Officer)','Shimla PS','7649928090','Staff',
     '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6',TRUE,TRUE,NOW(),NOW()),

    -- APPROVED USERS
    ('Arjun Singh','IO (Investigating Officer)','Shimla PS','9000000101','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,TRUE,NOW(),NOW()),

    ('Bharat Negi','IO (Investigating Officer)','Kullu PS','9000000102','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,TRUE,NOW(),NOW()),

    ('Chirag Joshi','IO (Investigating Officer)','Mandi PS','9000000103','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,TRUE,NOW(),NOW()),

    ('Dinesh Kumar','IO (Investigating Officer)','Shimla PS','9000000104','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,TRUE,NOW(),NOW()),

    ('Eshan Kapoor','IO (Investigating Officer)','Kullu PS','9000000105','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,TRUE,NOW(),NOW()),

    ('Farhan Ali','IO (Investigating Officer)','Mandi PS','9000000106','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,TRUE,NOW(),NOW()),

    ('Gaurav Mehta','IO (Investigating Officer)','Shimla PS','9000000107','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,TRUE,NOW(),NOW()),

    ('Harsh Vardhan','IO (Investigating Officer)','Kullu PS','9000000108','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,TRUE,NOW(),NOW()),

    ('Ishaan Gupta','IO (Investigating Officer)','Mandi PS','9000000109','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,TRUE,NOW(),NOW()),

    ('Jatin Sood','IO (Investigating Officer)','Shimla PS','9000000110','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,TRUE,NOW(),NOW()),

    -- PENDING USERS
    ('Karan Thapa','IO (Investigating Officer)','Shimla PS','9000000201','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,FALSE,NOW(),NOW()),

    ('Lokesh Chauhan','IO (Investigating Officer)','Kullu PS','9000000202','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,FALSE,NOW(),NOW()),

    ('Manoj Pathak','IO (Investigating Officer)','Mandi PS','9000000203','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,FALSE,NOW(),NOW()),

    ('Nikhil Bansal','IO (Investigating Officer)','Shimla PS','9000000204','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,FALSE,NOW(),NOW()),

    ('Om Prakash','IO (Investigating Officer)','Kullu PS','9000000205','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,FALSE,NOW(),NOW()),

    ('Pankaj Sharma','IO (Investigating Officer)','Mandi PS','9000000206','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,FALSE,NOW(),NOW()),

    ('Qadir Hussain','IO (Investigating Officer)','Shimla PS','9000000207','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,FALSE,NOW(),NOW()),

    ('Rakesh Dogra','IO (Investigating Officer)','Kullu PS','9000000208','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,FALSE,NOW(),NOW()),

    ('Suresh Kumar','IO (Investigating Officer)','Mandi PS','9000000209','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,FALSE,NOW(),NOW()),

    ('Tarun Negi','IO (Investigating Officer)','Shimla PS','9000000210','Staff',
     '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu',TRUE,FALSE,NOW(),NOW())

) AS v(name, rank_name, ps_name, phone, role_name, password_hash, is_active, is_approved, created_at, updated_at)

JOIN mst_rank r ON r.rank_name = v.rank_name
JOIN mst_police_station ps ON ps.ps_name = v.ps_name
JOIN mst_role rl ON rl.role_name = v.role_name

ON CONFLICT (phone) DO UPDATE
SET
    name = EXCLUDED.name,
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
    (3, 'Untraced'),
    (4, 'Resolved')
ON CONFLICT (id) DO UPDATE
SET case_status = EXCLUDED.case_status;

-- ==============================
-- Cases
-- ==============================
INSERT INTO cases (
    id,
    created_at,
    created_by,
    is_active,
    updated_at,
    updated_by,
    district_id,
    fir_no,
    fir_year,
    ps_id,
    sections,
    summary,
    case_owner,
    case_status
)
VALUES
(1,NOW(),1,TRUE,NOW(),1,1,'FIR-1001',2026,1,'IPC 420,406','UPI fraud through fake customer care complaint',1,1),
(2,NOW(),2,TRUE,NOW(),2,2,'FIR-1002',2026,2,'IPC 379','Mobile theft with wallet misuse and SIM misuse',3,2),
(3,NOW(),3,TRUE,NOW(),3,3,'FIR-1003',2026,3,'IPC 302','Suspicious death linked with digital threats',4,1),
(4,NOW(),1,TRUE,NOW(),1,1,'FIR-1004',2026,1,'IPC 498A','Domestic harassment with abusive chats evidence',2,3),
(5,NOW(),3,TRUE,NOW(),3,2,'FIR-1005',2026,2,'IPC 354','Stalking and obscene social media messages',3,1)
ON CONFLICT (id) DO UPDATE
SET
    updated_at = EXCLUDED.updated_at,
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
-- Case Assigned Users
-- ==============================
INSERT INTO case_assigned_users (case_id, user_id)
VALUES
    (1,1),(1,3),(1,4),
    (2,2),(2,3),
    (3,3),(3,1),
    (4,4),(4,5),
    (5,2),(5,5)
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


-- ==============================
-- Notices
-- Added multiple layers for Case 2 and Case 3
-- Complete / intact / fixed
-- ==============================

INSERT INTO notices (
    id,
    case_id,
    notice_id,
    notice_type,
    layer,
    issued_to,
    issued_date,
    notice_file_name,
    notice_file_path,
    reply_by,
    reply_date,
    remarks,
    reply_file_name,
    reply_file_path,
    status,
    created_at,
    updated_at,
    created_by,
    updated_by,
    is_active
)
VALUES

-- ==============================
-- CASE 1
-- ==============================
(1001,1,'NT-1001','1','LAYER_1','Meta Platforms India','2026-04-01','notice_meta_1001.docx','/notices/2026/04/NT-1001.docx',NULL,NULL,'Awaiting account preservation response',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1002,1,'NT-1002','2','LAYER_1','Instagram Compliance Team','2026-04-02','notice_meta_1002.docx','/notices/2026/04/NT-1002.docx','Meta Legal Team','2026-04-05','Initial acknowledgement received','reply_1002.pdf','/replies/2026/04/reply_1002.pdf','REPLIED',NOW(),NOW(),4,4,TRUE),
(1003,1,'NT-1003','3','LAYER_1','Rahul Sharma','2026-04-03','summon_1003.docx','/notices/2026/04/NT-1003.docx',NULL,NULL,'Appearance pending',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1004,1,'NT-1004','4','LAYER_1','State Bank of India','2026-04-04','bank_1004.docx','/notices/2026/04/NT-1004.docx',NULL,NULL,'Freeze request sent',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

-- ==============================
-- CASE 2 (7 Layers)
-- ==============================
(1005,2,'NT-1005','5','LAYER_1','Airtel Nodal Office','2026-04-05','cdr_1005.docx','/notices/2026/04/NT-1005.docx',NULL,NULL,'Pending telecom records',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1006,2,'NT-1006','6','LAYER_1','Punjab National Bank CCTV Cell','2026-04-06','cctv_1006.docx','/notices/2026/04/NT-1006.docx','PNB Surveillance Cell','2026-04-08','Footage shared','reply_1006.pdf','/replies/2026/04/reply_1006.pdf','REPLIED',NOW(),NOW(),4,4,TRUE),

(1007,2,'NT-1007','1','LAYER_2','Facebook LERT','2026-04-07','meta_1007.docx','/notices/2026/04/NT-1007.docx',NULL,NULL,'Served successfully',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),
(1008,2,'NT-1008','2','LAYER_2','Instagram Compliance Team','2026-04-08','meta_1008.docx','/notices/2026/04/NT-1008.docx',NULL,NULL,'Awaiting preservation',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),

(1011,2,'NT-1011','4','LAYER_3','ICICI Bank Nodal Officer','2026-04-09','bank_1011.docx','/notices/2026/04/NT-1011.docx',NULL,NULL,'Debit freeze requested',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),
(1012,2,'NT-1012','4','LAYER_3','Axis Bank Fraud Cell','2026-04-09','bank_1012.docx','/notices/2026/04/NT-1012.docx',NULL,NULL,'Account trace initiated',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),

(1013,2,'NT-1013','3','LAYER_4','Rohit Verma','2026-04-10','summon_1013.docx','/notices/2026/04/NT-1013.docx',NULL,NULL,'Personal appearance required',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1014,2,'NT-1014','5','LAYER_4','Jio Nodal Office','2026-04-10','cdr_1014.docx','/notices/2026/04/NT-1014.docx',NULL,NULL,'Subscriber details sought',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

(1015,2,'NT-1015','4','LAYER_5','Paytm Payments Bank','2026-04-11','bank_1015.docx','/notices/2026/04/NT-1015.docx',NULL,NULL,'Wallet freeze requested',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1016,2,'NT-1016','4','LAYER_5','PhonePe Compliance','2026-04-11','wallet_1016.docx','/notices/2026/04/NT-1016.docx',NULL,NULL,'Merchant details requested',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

(1017,2,'NT-1017','6','LAYER_6','Punjab National Bank CCTV Cell','2026-04-12','cctv_1017.docx','/notices/2026/04/NT-1017.docx',NULL,NULL,'ATM footage requested',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1018,2,'NT-1018','1','LAYER_6','Google Legal Support','2026-04-12','google_1018.docx','/notices/2026/04/NT-1018.docx',NULL,NULL,'Gmail preservation requested',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

(1019,2,'NT-1019','4','LAYER_7','State Bank of India','2026-04-13','bank_1019.docx','/notices/2026/04/NT-1019.docx',NULL,NULL,'Final beneficiary freeze',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1020,2,'NT-1020','3','LAYER_7','Amit Chauhan','2026-04-13','summon_1020.docx','/notices/2026/04/NT-1020.docx',NULL,NULL,'Final suspect summon',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

-- ==============================
-- CASE 3 (6 Layers)
-- ==============================
(1009,3,'NT-1009','3','LAYER_1','Rajeev Sharma','2026-04-09','summon_1009.docx','/notices/2026/04/NT-1009.docx',NULL,NULL,'Appearance pending',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1010,3,'NT-1010','4','LAYER_1','HDFC Bank Nodal Desk','2026-04-10','bank_1010.docx','/notices/2026/04/NT-1010.docx',NULL,NULL,'Freeze request sent',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

(1021,3,'NT-1021','4','LAYER_2','ICICI Bank Nodal Office','2026-04-11','bank_1021.docx','/notices/2026/04/NT-1021.docx',NULL,NULL,'Trail transfer found',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1022,3,'NT-1022','5','LAYER_2','Vodafone Idea Nodal Team','2026-04-11','cdr_1022.docx','/notices/2026/04/NT-1022.docx',NULL,NULL,'Call records requested',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

(1023,3,'NT-1023','4','LAYER_3','Axis Bank Fraud Cell','2026-04-12','bank_1023.docx','/notices/2026/04/NT-1023.docx',NULL,NULL,'Suspicious transfer detected',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1024,3,'NT-1024','1','LAYER_3','Telegram Compliance','2026-04-12','telegram_1024.docx','/notices/2026/04/NT-1024.docx',NULL,NULL,'Account preservation request',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

(1025,3,'NT-1025','3','LAYER_4','Deepak Mehra','2026-04-13','summon_1025.docx','/notices/2026/04/NT-1025.docx',NULL,NULL,'Witness statement needed',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1026,3,'NT-1026','6','LAYER_4','Union Bank CCTV Unit','2026-04-13','cctv_1026.docx','/notices/2026/04/NT-1026.docx',NULL,NULL,'CCTV retrieval pending',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

(1027,3,'NT-1027','4','LAYER_5','Paytm Payments Bank','2026-04-14','wallet_1027.docx','/notices/2026/04/NT-1027.docx',NULL,NULL,'Wallet beneficiary traced',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1028,3,'NT-1028','4','LAYER_5','PhonePe Legal Cell','2026-04-14','wallet_1028.docx','/notices/2026/04/NT-1028.docx',NULL,NULL,'Merchant KYC requested',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

(1029,3,'NT-1029','4','LAYER_6','State Bank of India','2026-04-15','bank_1029.docx','/notices/2026/04/NT-1029.docx',NULL,NULL,'End trail account freeze',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1030,3,'NT-1030','3','LAYER_6','Nikhil Sood','2026-04-15','summon_1030.docx','/notices/2026/04/NT-1030.docx',NULL,NULL,'Final inquiry summon',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE)

ON CONFLICT (id) DO NOTHING;

-- ==============================
-- Reset Sequences
-- ==============================
SELECT setval(pg_get_serial_sequence('mst_district','id'), COALESCE(MAX(id),1), MAX(id) IS NOT NULL) FROM mst_district;
SELECT setval(pg_get_serial_sequence('mst_role','id'), COALESCE(MAX(id),1), MAX(id) IS NOT NULL) FROM mst_role;
SELECT setval(pg_get_serial_sequence('mst_rank','id'), COALESCE(MAX(id),1), MAX(id) IS NOT NULL) FROM mst_rank;
SELECT setval(pg_get_serial_sequence('mst_police_station','id'), COALESCE(MAX(id),1), MAX(id) IS NOT NULL) FROM mst_police_station;
SELECT setval(pg_get_serial_sequence('mst_case_status','id'), COALESCE(MAX(id),1), MAX(id) IS NOT NULL) FROM mst_case_status;
SELECT setval(pg_get_serial_sequence('mst_notice_type','id'), COALESCE(MAX(id),1), MAX(id) IS NOT NULL) FROM mst_notice_type;
SELECT setval(pg_get_serial_sequence('cases','id'), COALESCE(MAX(id),1), MAX(id) IS NOT NULL) FROM cases;
SELECT setval(pg_get_serial_sequence('notices','id'), COALESCE((SELECT MAX(id) FROM notices),1), TRUE);