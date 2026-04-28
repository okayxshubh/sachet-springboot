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
-- SHO as Case Owners
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
(5,NOW(),3,TRUE,NOW(),3,2,'FIR-1005',2026,2,'IPC 354','Stalking and obscene social media messages',3,1),
(6,NOW(),4,TRUE,NOW(),4,3,'FIR-1006',2026,3,'IPC 465,468','Forged land papers and identity misuse',4,1),
(7,NOW(),2,TRUE,NOW(),2,1,'FIR-1007',2026,1,'IPC 384','Extortion through threatening calls',2,1),
(8,NOW(),1,TRUE,NOW(),1,1,'FIR-1008',2026,1,'IPC 379,411','Vehicle theft and resale racket',1,4),
(9,NOW(),3,TRUE,NOW(),3,2,'FIR-1009',2026,2,'IPC 406','Trust breach in contractor payment matter',3,1),
(10,NOW(),4,TRUE,NOW(),4,3,'FIR-1010',2026,3,'IPC 354D','Cyber stalking via fake profiles',4,1)
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
-- Cases assigned ONLY to IOs
-- Removed SHO users from assignments
-- SHO IDs = 1,2,3,4,5
-- IO IDs start from 6 onward
-- ==============================
INSERT INTO case_assigned_users (case_id, user_id)
VALUES
    -- Case 1
    (1,6),(1,9),(1,12),

    -- Case 2
    (2,7),(2,10),

    -- Case 3
    (3,8),(3,11),

    -- Case 4
    (4,6),(4,13),

    -- Case 5
    (5,7),(5,10),

    -- Case 6
    (6,8),(6,11),

    -- Case 7
    (7,9),(7,12),

    -- Case 8
    (8,6),(8,14),

    -- Case 9
    (9,7),(9,15),

    -- Case 10
    (10,8),(10,11)

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
-- Expanded Cases / Layers
-- ==============================
INSERT INTO notices (
    id, case_id, notice_id, notice_type, layer, issued_to, issued_date,
    notice_file_name, notice_file_path, reply_by, reply_date, remarks,
    reply_file_name, reply_file_path, status,
    created_at, updated_at, created_by, updated_by, is_active
)
VALUES

(1001,1,'NT-1001','1','LAYER_1','Meta Platforms India','2026-04-01','notice_meta_1001.docx','/notices/2026/04/NT-1001.docx',NULL,NULL,'Awaiting account preservation response',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1002,1,'NT-1002','4','LAYER_2','State Bank of India','2026-04-02','bank_1002.docx','/notices/2026/04/NT-1002.docx',NULL,NULL,'Freeze request sent',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

(1003,2,'NT-1003','5','LAYER_1','Airtel Nodal Office','2026-04-03','cdr_1003.docx','/notices/2026/04/NT-1003.docx',NULL,NULL,'Pending telecom records',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1004,2,'NT-1004','6','LAYER_2','PNB CCTV Cell','2026-04-04','cctv_1004.docx','/notices/2026/04/NT-1004.docx',NULL,NULL,'ATM footage sought',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

(1005,3,'NT-1005','3','LAYER_1','Rajeev Sharma','2026-04-05','summon_1005.docx','/notices/2026/04/NT-1005.docx',NULL,NULL,'Appearance pending',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1006,3,'NT-1006','4','LAYER_2','HDFC Bank','2026-04-06','bank_1006.docx','/notices/2026/04/NT-1006.docx',NULL,NULL,'Freeze request sent',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

(1007,4,'NT-1007','1','LAYER_1','Instagram Compliance Team','2026-04-07','meta_1007.docx','/notices/2026/04/NT-1007.docx',NULL,NULL,'Account preservation requested',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1008,4,'NT-1008','3','LAYER_2','Vikas Thakur','2026-04-08','summon_1008.docx','/notices/2026/04/NT-1008.docx',NULL,NULL,'Personal appearance required',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

(1009,5,'NT-1009','2','LAYER_1','Facebook LERT','2026-04-09','meta_1009.docx','/notices/2026/04/NT-1009.docx',NULL,NULL,'Data preservation sought',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1010,5,'NT-1010','3','LAYER_2','Kamal Verma','2026-04-10','summon_1010.docx','/notices/2026/04/NT-1010.docx',NULL,NULL,'Witness examination',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

(1011,6,'NT-1011','4','LAYER_1','ICICI Bank','2026-04-11','bank_1011.docx','/notices/2026/04/NT-1011.docx',NULL,NULL,'Transaction trail requested',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1012,6,'NT-1012','5','LAYER_2','Jio Nodal Office','2026-04-12','cdr_1012.docx','/notices/2026/04/NT-1012.docx',NULL,NULL,'Subscriber details sought',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

(1013,7,'NT-1013','3','LAYER_1','Rakesh Mehta','2026-04-13','summon_1013.docx','/notices/2026/04/NT-1013.docx',NULL,NULL,'Threat caller identified',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1014,7,'NT-1014','5','LAYER_2','Vodafone Idea','2026-04-13','cdr_1014.docx','/notices/2026/04/NT-1014.docx',NULL,NULL,'CDR requested',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

(1015,8,'NT-1015','6','LAYER_1','SBI CCTV Cell','2026-04-14','cctv_1015.docx','/notices/2026/04/NT-1015.docx',NULL,NULL,'Parking footage requested',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1016,8,'NT-1016','4','LAYER_2','Axis Bank','2026-04-14','bank_1016.docx','/notices/2026/04/NT-1016.docx',NULL,NULL,'Receiver account traced',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

(1017,9,'NT-1017','4','LAYER_1','Punjab National Bank','2026-04-15','bank_1017.docx','/notices/2026/04/NT-1017.docx',NULL,NULL,'Contract fund hold requested',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1018,9,'NT-1018','3','LAYER_2','Sanjay Rana','2026-04-15','summon_1018.docx','/notices/2026/04/NT-1018.docx',NULL,NULL,'Statement required',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),

(1019,10,'NT-1019','1','LAYER_1','Meta Platforms India','2026-04-16','meta_1019.docx','/notices/2026/04/NT-1019.docx',NULL,NULL,'Fake profile data requested',NULL,NULL,'PENDING',NOW(),NOW(),4,4,TRUE),
(1020,10,'NT-1020','2','LAYER_2','Instagram Compliance Team','2026-04-16','meta_1020.docx','/notices/2026/04/NT-1020.docx',NULL,NULL,'Preservation acknowledged',NULL,NULL,'REPLIED',NOW(),NOW(),4,4,TRUE)

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