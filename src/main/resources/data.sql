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
-- Districts
-- ==============================
INSERT INTO mst_district (id, district_name)
VALUES
    (1, 'CID State Cyber Crime'),
    (2, 'Test 2'),
    (3, 'Test 3')
ON CONFLICT (id) DO UPDATE
SET district_name = EXCLUDED.district_name;

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
-- Police Stations
-- ==============================
INSERT INTO mst_police_station (id, ps_name, district_id)
VALUES
    (1, 'Dharamshala CCPS', 1),
    (2, 'Shimla CCPS', 1),
    (3, 'Mandi CCPS', 1),
    (4, 'Test A CCPS', 2),
    (5, 'Test B CCPS', 2),
    (6, 'Test C CCPS', 2),
    (7, 'Test D CCPS', 3),
    (8, 'Test E CCPS', 3),
    (9, 'Test F CCPS', 3)
ON CONFLICT (id) DO UPDATE
SET
    ps_name = EXCLUDED.ps_name,
    district_id = EXCLUDED.district_id;

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
-- Notice Templates
-- ==============================
INSERT INTO notice_templates (
    id, notice_type_id, file_path,
    is_active, created_at, updated_at, created_by, updated_by
)
VALUES
(1, 1, 'src/main/java/in/gov/cybercrime/sachet/assets/noticeTemplates/type_1_meta_notice_EN.txt', TRUE, NOW(), NOW(), 'system', 'system'),
(2, 2, 'src/main/java/in/gov/cybercrime/sachet/assets/noticeTemplates/type_2_bnss64_meta_EN.txt', TRUE, NOW(), NOW(), 'system', 'system'),
(3, 3, 'src/main/java/in/gov/cybercrime/sachet/assets/noticeTemplates/type_3_summon_35_3_EN.txt', TRUE, NOW(), NOW(), 'system', 'system'),
(4, 4, 'src/main/java/in/gov/cybercrime/sachet/assets/noticeTemplates/type_4_bank_notice_EN.txt', TRUE, NOW(), NOW(), 'system', 'system'),
(5, 5, 'src/main/java/in/gov/cybercrime/sachet/assets/noticeTemplates/type_5_cdr_notice_EN.txt', TRUE, NOW(), NOW(), 'system', 'system'),
(6, 6, 'src/main/java/in/gov/cybercrime/sachet/assets/noticeTemplates/type_6_cctv_atm_notice_EN.txt', TRUE, NOW(), NOW(), 'system', 'system')
ON CONFLICT (notice_type_id) DO UPDATE
SET
    file_path = EXCLUDED.file_path,
    is_active = EXCLUDED.is_active,
    updated_at = EXCLUDED.updated_at,
    updated_by = EXCLUDED.updated_by;


-- ==============================
-- MAIN SHO USERS
-- ==============================
INSERT INTO users (
    id, name, rank_id, ps_id, phone, role_id, password_hash,
    is_active, is_approved, created_at, updated_at
)
VALUES
(1, 'Rohit Malpani', 1, 2, '9816662225', 2, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(2, 'Shubh', 1, 2, '7018437924', 1, '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, TRUE, NOW(), NOW()),
(3, 'Brijesh', 1, 1, '7649928090', 3, '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, TRUE, NOW(), NOW()),
(4, 'Anshita', 1, 1, '7580034077', 1, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(5, 'SHO 1', 1, 1, '9817000001', 2, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(6, 'SHO 2', 1, 1, '9817000002', 3, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(7, 'Prikshit', 1, 3, '6230775084', 1, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(8, 'SHO 3', 1, 3, '9817000003', 2, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(9, 'SHO 4', 1, 3, '9817000004', 3, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(10, 'IO 1', 2, 2, '9817100001', 3, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(11, 'IO 2', 2, 2, '9817100002', 3, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(12, 'IO 3', 2, 2, '9817100003', 3, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(13, 'IO 4', 2, 2, '9817100004', 3, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(14, 'IO 5', 2, 2, '9817100005', 3, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(15, 'IO 6', 2, 1, '9817200001', 3, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(16, 'IO 7', 2, 1, '9817200002', 3, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(17, 'IO 8', 2, 1, '9817200003', 3, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(18, 'IO 9', 2, 1, '9817200004', 3, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(19, 'IO 10', 2, 1, '9817200005', 3, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(20, 'IO 11', 2, 3, '9817300001', 3, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(21, 'IO 12', 2, 3, '9817300002', 3, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(22, 'IO 13', 2, 3, '9817300003', 3, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(23, 'IO 14', 2, 3, '9817300004', 3, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(24, 'IO 15', 2, 3, '9817300005', 3, '$2a$12$ZzTi.apOKBsI/WMCWH0Mn.zzpcLzyJ3TvqFxZ1/OrM7hDyu7DBVVu', TRUE, TRUE, NOW(), NOW()),
(25, 'Pending User 1 Dharamshala', 2, 1, '8000001001', 3, '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, FALSE, NOW(), NOW()),
(26, 'Pending User 2 Dharamshala', 2, 1, '8000001002', 3, '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, FALSE, NOW(), NOW()),
(27, 'Pending User 3 Dharamshala', 2, 1, '8000001003', 3, '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, FALSE, NOW(), NOW()),
(28, 'Pending User 4 Dharamshala', 2, 1, '8000001004', 3, '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, FALSE, NOW(), NOW()),
(29, 'Pending User 5 Dharamshala', 2, 1, '8000001005', 3, '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, FALSE, NOW(), NOW()),
(30, 'Pending User 1 Shimla', 2, 2, '8000002001', 3, '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, FALSE, NOW(), NOW()),
(31, 'Pending User 2 Shimla', 2, 2, '8000002002', 3, '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, FALSE, NOW(), NOW()),
(32, 'Pending User 3 Shimla', 2, 2, '8000002003', 3, '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, FALSE, NOW(), NOW()),
(33, 'Pending User 4 Shimla', 2, 2, '8000002004', 3, '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, FALSE, NOW(), NOW()),
(34, 'Pending User 5 Shimla', 2, 2, '8000002005', 3, '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, FALSE, NOW(), NOW()),
(35, 'Pending User 1 Mandi', 2, 3, '8000003001', 3, '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, FALSE, NOW(), NOW()),
(36, 'Pending User 2 Mandi', 2, 3, '8000003002', 3, '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, FALSE, NOW(), NOW()),
(37, 'Pending User 3 Mandi', 2, 3, '8000003003', 3, '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, FALSE, NOW(), NOW()),
(38, 'Pending User 4 Mandi', 2, 3, '8000003004', 3, '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, FALSE, NOW(), NOW()),
(39, 'Pending User 5 Mandi', 2, 3, '8000003005', 3, '$2a$12$g59i7APUObBG0kw7KMdrouf8wRWG6IQQFTRf260NfHLZExvskrzi6', TRUE, FALSE, NOW(), NOW())
ON CONFLICT (id) DO UPDATE
SET
    name = EXCLUDED.name,
    rank_id = EXCLUDED.rank_id,
    ps_id = EXCLUDED.ps_id,
    phone = EXCLUDED.phone,
    role_id = EXCLUDED.role_id,
    password_hash = EXCLUDED.password_hash,
    is_active = EXCLUDED.is_active,
    is_approved = EXCLUDED.is_approved,
    updated_at = EXCLUDED.updated_at;

-- ==============================
-- CASES DATA
-- ==============================
INSERT INTO cases (
    id, created_at, created_by, is_active, updated_at, updated_by,
    district_id, fir_no, fir_year, ps_id,
    sections, summary, case_owner, case_status
)
VALUES
(1, NOW(), 2, TRUE, NOW(), 2, 1, 'Case 1', 2026, 2, 'IPC 420, 66D', 'Online job fraud victim lost ₹50,000', 2, 1),
(2, NOW(), 2, TRUE, NOW(), 2, 1, 'Case 2', 2026, 2, 'IPC 406, 66C', 'E-commerce refund scam fake customer care', 2, 1),
(3, NOW(), 2, TRUE, NOW(), 2, 1, 'Case 3', 2026, 2, 'IPC 384, 67', 'Ransomware attack on hospital systems', 2, 2),
(4, NOW(), 4, TRUE, NOW(), 4, 1, 'Case 4', 2026, 1, 'IPC 420, 66D', 'Matrimonial site grooming for crypto fraud', 4, 1),
(5, NOW(), 4, TRUE, NOW(), 4, 1, 'Case 5', 2026, 1, 'IPC 465, 471', 'Forgery of property documents for loan', 4, 3),
(6, NOW(), 4, TRUE, NOW(), 4, 1, 'Case 6', 2026, 1, 'IPC 379, 66B', 'Mobile phone theft and UPI misuse', 4, 1),
(7, NOW(), 7, TRUE, NOW(), 7, 1, 'Case 7', 2026, 3, 'IPC 420, 66D', 'WhatsApp sextortion case from unknown number', 7, 1),
(8, NOW(), 7, TRUE, NOW(), 7, 1, 'Case 8', 2026, 3, 'IPC 406', 'Loan app fraud with hidden terms', 7, 1),
(9, NOW(), 7, TRUE, NOW(), 7, 1, 'Case 9', 2026, 3, 'IPC 384', 'Fake police extortion via video call', 7, 4),
(10, NOW(), 2, TRUE, NOW(), 2, 1, 'Case 10', 2026, 2, 'IPC 420, 66C', 'Credit card OTP sharing fraud', 2, 1),
(11, NOW(), 1, TRUE, NOW(), 1, 1, 'Case 11', 2026, 2, 'IPC 420', 'Fake OLX Army officer scam with UPI fraud', 1, 1),
(12, NOW(), 2, TRUE, NOW(), 2, 1, 'Case 12', 2026, 2, 'IPC 406', 'Investment fraud through Telegram crypto channel', 2, 1),
(13, NOW(), 3, TRUE, NOW(), 3, 1, 'Case 13', 2026, 2, 'IPC 354D', 'Cyber stalking through fake Instagram IDs', 3, 1),
(14, NOW(), 1, TRUE, NOW(), 1, 1, 'Case 14', 2026, 2, 'IPC 467,468', 'Fake government job appointment letters', 1, 2),
(15, NOW(), 2, TRUE, NOW(), 2, 1, 'Case 15', 2026, 2, 'IPC 384', 'Blackmail using morphed photographs', 2, 1),
(16, NOW(), 4, TRUE, NOW(), 4, 1, 'Case 16', 2026, 1, 'IPC 420', 'Loan app harassment and extortion calls', 4, 1),
(17, NOW(), 5, TRUE, NOW(), 5, 1, 'Case 17', 2026, 1, 'IPC 379', 'UPI linked mobile theft complaint', 5, 4),
(18, NOW(), 7, TRUE, NOW(), 7, 1, 'Case 18', 2026, 3, 'IPC 420', 'Fake KYC update banking fraud', 7, 1),
(19, NOW(), 8, TRUE, NOW(), 8, 1, 'Case 19', 2026, 3, 'IPC 465', 'Forged Aadhaar used for SIM activation', 8, 3)
ON CONFLICT (id) DO UPDATE
SET
    fir_no = EXCLUDED.fir_no,
    fir_year = EXCLUDED.fir_year,
    ps_id = EXCLUDED.ps_id,
    sections = EXCLUDED.sections,
    summary = EXCLUDED.summary,
    case_owner = EXCLUDED.case_owner,
    case_status = EXCLUDED.case_status,
    district_id = EXCLUDED.district_id,
    updated_at = EXCLUDED.updated_at;

-- ==============================
-- CASE ASSIGNMENTS
-- ==============================
INSERT INTO case_assigned_users (case_id, user_id)
VALUES
(1, 2), (1, 10), (1, 11),
(2, 2), (2, 12), (2, 13),
(3, 2), (3, 14), (3, 15),
(4, 4), (4, 15), (4, 16),
(5, 4), (5, 17), (5, 18),
(6, 4), (6, 19),
(7, 7), (7, 20), (7, 21),
(8, 7), (8, 22), (8, 23),
(9, 7), (9, 24),
(10, 2), (10, 10), (10, 12),
(11, 10), (11, 11),
(12, 12), (12, 13),
(13, 14), (13, 15),
(14, 10), (14, 12),
(15, 11), (15, 13),
(16, 15), (16, 16),
(17, 17), (17, 18),
(18, 20), (18, 21),
(19, 22), (19, 23)
ON CONFLICT (case_id, user_id) DO NOTHING;

-- ==============================
-- NOTICES DATA
-- ==============================

INSERT INTO notices (
    id, case_id, notice_id, notice_type, layer,
    issued_to, issued_date,
    notice_file_name, notice_file_path,
    reply_date, remarks,
    reply_file_name, reply_file_path,
    status,
    created_at, updated_at,
    created_by, updated_by, is_active
)
VALUES
(1001,1,'Notice 1','1','LAYER_1','Meta India','2026-04-10','meta_notice.pdf','/notices/2026/04/NT-1001.pdf','2026-04-15','Data provided successfully','meta_reply.pdf','/notices/replies/NT-1001_reply.pdf','SENT',NOW(),NOW(),2,2,TRUE),
(1002,2,'Notice 2','4','LAYER_1','ICICI Bank','2026-04-11','bank_notice.pdf','/notices/2026/04/NT-1002.pdf','2026-04-16','Account frozen','bank_reply.pdf','/notices/replies/NT-1002_reply.pdf','SENT',NOW(),NOW(),2,2,TRUE),
(1003,3,'Notice 3','2','LAYER_2','WhatsApp Inc.','2026-04-12','whatsapp_notice.pdf','/notices/2026/04/NT-1003.pdf',NULL,'Awaiting response',NULL,NULL,'PENDING',NOW(),NOW(),2,2,TRUE),
(1004,4,'Notice 4','3','LAYER_1','John Doe','2026-04-13','summon_order.pdf','/notices/2026/04/NT-1004.pdf','2026-04-18','Appeared for statement','statement_record.pdf','/notices/replies/NT-1004_reply.pdf','SENT',NOW(),NOW(),4,4,TRUE),
(1005,5,'Notice 5','5','LAYER_2','Vodafone Idea','2026-04-14','tower_dump.pdf','/notices/2026/04/NT-1005.pdf',NULL,'Technical team working',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),
(1006,6,'Notice 6','6','LAYER_1','SBI CCTV','2026-04-15','cctv_request.pdf','/notices/2026/04/NT-1006.pdf','2026-04-20','Footage submitted','cctv_footage.txt','/notices/replies/NT-1006_reply.txt','SENT',NOW(),NOW(),4,4,TRUE),
(1007,7,'Notice 7','1','LAYER_2','Google LLC','2026-04-16','google_notice.pdf','/notices/2026/04/NT-1007.pdf',NULL,'Request under process',NULL,NULL,'PENDING',NOW(),NOW(),7,7,TRUE),
(1008,8,'Notice 8','4','LAYER_1','Paytm Bank','2026-04-17','paytm_notice.pdf','/notices/2026/04/NT-1008.pdf','2026-04-22','Transaction details shared','paytm_reply.pdf','/notices/replies/NT-1008_reply.pdf','SENT',NOW(),NOW(),7,7,TRUE),
(1009,9,'Notice 9','2','LAYER_3','Jane Smith','2026-04-18','summon_witness.pdf','/notices/2026/04/NT-1009.pdf',NULL,'Not reachable',NULL,NULL,'PENDING',NOW(),NOW(),7,7,TRUE),
(1010,10,'Notice 10','3','LAYER_1','Robert Johnson','2026-04-19','examination_order.pdf','/notices/2026/04/NT-1010.pdf','2026-04-24','Exam completed','exam_report.pdf','/notices/replies/NT-1010_reply.pdf','SENT',NOW(),NOW(),2,2,TRUE),
(1021,11,'Notice 11','1','LAYER_1','Meta Platforms India','2026-04-17','meta_1021.docx','/notices/2026/04/NT-1021.docx',NULL,'Facebook account preservation requested',NULL,NULL,'PENDING',NOW(),NOW(),1,1,TRUE),
(1022,12,'Notice 12','4','LAYER_1','HDFC Bank','2026-04-18','bank_1022.docx','/notices/2026/04/NT-1022.docx',NULL,'Fraud beneficiary account freeze requested',NULL,NULL,'SENT',NOW(),NOW(),2,2,TRUE),
(1023,13,'Notice 13','2','LAYER_1','Instagram Compliance Team','2026-04-19','insta_1023.docx','/notices/2026/04/NT-1023.docx',NULL,'Fake profile information requested',NULL,NULL,'PENDING',NOW(),NOW(),3,3,TRUE),
(1024,14,'Notice 14','5','LAYER_2','Airtel Nodal Office','2026-04-20','cdr_1024.docx','/notices/2026/04/NT-1024.docx',NULL,'Subscriber CDR requested',NULL,NULL,'SENT',NOW(),NOW(),1,1,TRUE),
(1025,15,'Notice 15','3','LAYER_1','Rohit Sood','2026-04-21','summon_1025.docx','/notices/2026/04/NT-1025.docx',NULL,'Victim statement recording pending',NULL,NULL,'PENDING',NOW(),NOW(),2,2,TRUE),
(1026,16,'Notice 16','4','LAYER_1','Punjab National Bank','2026-04-22','bank_1026.docx','/notices/2026/04/NT-1026.docx',NULL,'Wallet account freeze request',NULL,NULL,'SENT',NOW(),NOW(),4,4,TRUE),
(1027,17,'Notice 17','6','LAYER_1','SBI CCTV Cell','2026-04-23','cctv_1027.docx','/notices/2026/04/NT-1027.docx',NULL,'ATM CCTV footage requested',NULL,NULL,'PENDING',NOW(),NOW(),5,5,TRUE),
(1028,18,'Notice 18','1','LAYER_1','Meta Platforms India','2026-04-24','meta_1028.docx','/notices/2026/04/NT-1028.docx',NULL,'Fraud Facebook account traced',NULL,NULL,'PENDING',NOW(),NOW(),7,7,TRUE),
(1029,19,'Notice 19','5','LAYER_2','Jio Nodal Office','2026-04-25','cdr_1029.docx','/notices/2026/04/NT-1029.docx',NULL,'SIM activation details requested',NULL,NULL,'SENT',NOW(),NOW(),8,8,TRUE)

ON CONFLICT (id) DO UPDATE SET
case_id=EXCLUDED.case_id,
notice_id=EXCLUDED.notice_id,
notice_type=EXCLUDED.notice_type,
layer=EXCLUDED.layer,
issued_to=EXCLUDED.issued_to,
issued_date=EXCLUDED.issued_date,
notice_file_name=EXCLUDED.notice_file_name,
notice_file_path=EXCLUDED.notice_file_path,
reply_date=EXCLUDED.reply_date,
remarks=EXCLUDED.remarks,
reply_file_name=EXCLUDED.reply_file_name,
reply_file_path=EXCLUDED.reply_file_path,
status=EXCLUDED.status,
updated_at=EXCLUDED.updated_at,
updated_by=EXCLUDED.updated_by;

-- ==============================
-- CASE DIARIES DATA
-- ==============================
INSERT INTO case_diaries (
    id, created_at, created_by, is_active, updated_at, updated_by,
    case_id, event_type, summary, notice_id, performed_by,
    event_time, version_no, meta_data
)
SELECT
    ROW_NUMBER() OVER (ORDER BY sort_group, case_id, notice_id NULLS LAST, assigned_user_id NULLS LAST, event_type),
    NOW(),
    resolved_performed_by::TEXT,
    TRUE,
    NOW(),
    resolved_performed_by::TEXT,
    case_id,
    event_type,
    summary,
    notice_id,
    resolved_performed_by,
    event_time,
    1,
    meta_data
FROM (
    SELECT
        diary_seed.*,
        COALESCE(user_by_id.id, user_by_phone.id) AS resolved_performed_by
    FROM (
    SELECT
        1 AS sort_group,
        c.id AS case_id,
        NULL::BIGINT AS notice_id,
        NULL::BIGINT AS assigned_user_id,
        'CASE_CREATED' AS event_type,
        'Case registered: ' || c.summary AS summary,
        c.case_owner AS performed_by,
        ('2026-04-01 09:00:00'::TIMESTAMP + ((c.id - 1) * INTERVAL '1 day')) AS event_time,
        '{"source":"seed","action":"case_created"}' AS meta_data
    FROM cases c

    UNION ALL

    SELECT
        2 AS sort_group,
        cau.case_id,
        NULL::BIGINT AS notice_id,
        cau.user_id AS assigned_user_id,
        'CASE_ASSIGNED' AS event_type,
        'Case assigned to ' || u.name AS summary,
        c.case_owner AS performed_by,
        ('2026-04-01 10:00:00'::TIMESTAMP + ((cau.case_id - 1) * INTERVAL '1 day')) AS event_time,
        '{"source":"seed","assignedUserId":' || cau.user_id || '}' AS meta_data
    FROM case_assigned_users cau
    JOIN cases c ON c.id = cau.case_id
    JOIN users u ON u.id = cau.user_id

    UNION ALL

    SELECT
        3 AS sort_group,
        n.case_id,
        n.id AS notice_id,
        NULL::BIGINT AS assigned_user_id,
        'NOTICE_CREATED' AS event_type,
        'Notice ' || n.notice_id || ' created for ' || n.issued_to AS summary,
        NULLIF(n.created_by, '')::BIGINT AS performed_by,
        (n.issued_date::TIMESTAMP + INTERVAL '9 hours') AS event_time,
        '{"source":"seed","action":"notice_created"}' AS meta_data
    FROM notices n

    UNION ALL

    SELECT
        4 AS sort_group,
        n.case_id,
        n.id AS notice_id,
        NULL::BIGINT AS assigned_user_id,
        'NOTICE_SENT' AS event_type,
        'Notice ' || n.notice_id || ' sent to ' || n.issued_to AS summary,
        NULLIF(n.updated_by, '')::BIGINT AS performed_by,
        (n.issued_date::TIMESTAMP + INTERVAL '10 hours') AS event_time,
        '{"source":"seed","status":"' || n.status || '"}' AS meta_data
    FROM notices n

    UNION ALL

    SELECT
        5 AS sort_group,
        n.case_id,
        n.id AS notice_id,
        NULL::BIGINT AS assigned_user_id,
        'NOTICE_REPLIED' AS event_type,
        'Reply received for notice ' || n.notice_id || ': ' || COALESCE(n.remarks, 'Reply received') AS summary,
        NULLIF(n.updated_by, '')::BIGINT AS performed_by,
        (n.reply_date::TIMESTAMP + INTERVAL '10 hours') AS event_time,
        '{"source":"seed","action":"notice_replied"}' AS meta_data
    FROM notices n
    WHERE n.reply_date IS NOT NULL
) diary_seed
    LEFT JOIN users user_by_id ON user_by_id.id = diary_seed.performed_by
    LEFT JOIN users user_by_phone ON user_by_phone.phone = diary_seed.performed_by::TEXT
) diary_seed
ON CONFLICT (id) DO UPDATE
SET
    created_by = EXCLUDED.created_by,
    is_active = EXCLUDED.is_active,
    updated_at = EXCLUDED.updated_at,
    updated_by = EXCLUDED.updated_by,
    case_id = EXCLUDED.case_id,
    event_type = EXCLUDED.event_type,
    summary = EXCLUDED.summary,
    notice_id = EXCLUDED.notice_id,
    performed_by = EXCLUDED.performed_by,
    event_time = EXCLUDED.event_time,
    version_no = EXCLUDED.version_no,
    meta_data = EXCLUDED.meta_data;

-- ==============================
-- RESET SEQUENCES
-- ==============================

SELECT setval(pg_get_serial_sequence('mst_district','id'),COALESCE(MAX(id),1),true) FROM mst_district;
SELECT setval(pg_get_serial_sequence('mst_role','id'),COALESCE(MAX(id),1),true) FROM mst_role;
SELECT setval(pg_get_serial_sequence('mst_rank','id'),COALESCE(MAX(id),1),true) FROM mst_rank;
SELECT setval(pg_get_serial_sequence('mst_police_station','id'),COALESCE(MAX(id),1),true) FROM mst_police_station;
SELECT setval(pg_get_serial_sequence('mst_case_status','id'),COALESCE(MAX(id),1),true) FROM mst_case_status;
SELECT setval(pg_get_serial_sequence('users','id'),COALESCE(MAX(id),1),true) FROM users;
SELECT setval(pg_get_serial_sequence('cases','id'),COALESCE(MAX(id),1),true) FROM cases;
SELECT setval(pg_get_serial_sequence('notices','id'),COALESCE(MAX(id),1),true) FROM notices;
SELECT setval(pg_get_serial_sequence('case_diaries','id'),COALESCE(MAX(id),1),true) FROM case_diaries;
