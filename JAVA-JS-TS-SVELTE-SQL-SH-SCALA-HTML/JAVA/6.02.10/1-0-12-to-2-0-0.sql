ALTER TABLE DOMAIN ADD SETTINGS NVARCHAR2(2000);
ALTER TABLE CONTRACT ADD ALIAS_CERT NVARCHAR2(100);

--------------------------------------------------------
--  DDL for Procedure SP_ATTACH_INSERT
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_ATTACH_INSERT" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_request_id IN NVARCHAR2,
  v_title IN NVARCHAR2 DEFAULT NULL,
  v_ctype IN NVARCHAR2 DEFAULT NULL,
  v_data IN BLOB
)
AS

BEGIN

  INSERT INTO attach ( request_id, title, ctype, data )
  VALUES ( v_request_id, v_title, v_ctype, v_data );

END;

/
--------------------------------------------------------
--  DDL for Procedure SP_ATTACH_RETRIEVE
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_ATTACH_RETRIEVE" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_request_id IN NVARCHAR2,
  cv_1 OUT SYS_REFCURSOR
)
AS

BEGIN

  OPEN cv_1 FOR
    SELECT *
      FROM attach
     WHERE request_id = v_request_id
     ORDER BY id asc;

END;

/
--------------------------------------------------------
--  DDL for Procedure SP_ATTACH_SEEN
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_ATTACH_SEEN" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_id IN NUMBER
)
AS

BEGIN

  update attach set seen = 1
  where id = v_id;

  IF SQL%ROWCOUNT <> 1 THEN
     raise_application_error(-20101, 'sp_attach_seen: update attach failed') ;
  END IF;
  
END;

/
--------------------------------------------------------
--  DDL for Procedure SP_ATTACH_SIGNED
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_ATTACH_SIGNED" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_id IN NUMBER,
  v_request_id IN NVARCHAR2,
  v_data IN BLOB
)
AS

BEGIN

  UPDATE attach 
     SET data = v_data, signed = 1
   WHERE id = v_id
     AND request_id = v_request_id;
     
  IF SQL%ROWCOUNT <> 1 THEN
     raise_application_error(-20101, 'sp_attach_signed: update attach failed') ;
  END IF;     
  
END;

/
--------------------------------------------------------
--  DDL for Procedure SP_CAPABILITY_GET
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_CAPABILITY_GET" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_domain_id IN NVARCHAR2,
  v_key IN NVARCHAR2,
  v_value OUT NVARCHAR2
)
AS

BEGIN

  v_value := FN_CAPABILITY_GET(v_domain_id, v_key);

END;

/
--------------------------------------------------------
--  DDL for Procedure SP_CAPABILITY_LIST
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_CAPABILITY_LIST" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_domain_id IN NVARCHAR2,
  cv_1 OUT SYS_REFCURSOR
)
AS

BEGIN

   OPEN cv_1 FOR
      SELECT C.*
        FROM capability C
       WHERE C.DOMAIN_ID = v_domain_id;

END;

/
--------------------------------------------------------
--  DDL for Procedure SP_CARE_ENROLL_CONTRACT
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_CARE_ENROLL_CONTRACT" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_idcontract IN NVARCHAR2,
  cv_1 OUT SYS_REFCURSOR
)
AS

BEGIN

  OPEN cv_1 FOR
    SELECT C.*, E.* 
      FROM CONTRACT C
      join ENROLL E ON E.ID = C.ENROLL_ID
     WHERE C.idcontract = v_idcontract;

END;

/
--------------------------------------------------------
--  DDL for Procedure SP_CARE_ENROLL_LISTBYUID
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_CARE_ENROLL_LISTBYUID" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_uid IN NVARCHAR2,
  cv_1 OUT SYS_REFCURSOR
)
AS

BEGIN

  OPEN cv_1 FOR
    SELECT E.*, C.*
      FROM ENROLL E
      LEFT JOIN CONTRACT C ON C.ENROLL_ID = E.ID
     WHERE uid_ = v_uid
     ORDER BY C.IDCONTRACT DESC;

END;

/
--------------------------------------------------------
--  DDL for Procedure SP_CARE_SIGN_COUNT
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_CARE_SIGN_COUNT" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_certSN IN NVARCHAR2,
  v_uid IN NVARCHAR2,
  v_started OUT NUMBER,
  v_completed OUT NUMBER
)
AS

BEGIN

  SELECT STARTED, COMPLETED into v_started, v_completed 
    FROM SIGN_COUNT
   WHERE CERTSN = v_certSN
     and UID_ = v_uid;

EXCEPTION 
WHEN no_data_found THEN 
  BEGIN    
    v_started := 0;
    v_completed := 0;
  END;
  
END;

/
--------------------------------------------------------
--  DDL for Procedure SP_CARE_SIGN_LOG
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_CARE_SIGN_LOG" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_certSN IN NVARCHAR2,
  v_uid IN NVARCHAR2,
  cv_1 OUT SYS_REFCURSOR
)
AS
  last30days TIMESTAMP;
BEGIN

  last30days := SYSDATE - 30;
  
  OPEN cv_1 FOR
    SELECT *
      FROM SIGN_LOG
     WHERE CERTSN = v_certSN
       and UID_ = v_uid
       and DTSTART >= last30days
    ORDER BY DTSTART DESC;

END;

/
--------------------------------------------------------
--  DDL for Procedure SP_CONTRACT_GET
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_CONTRACT_GET" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_request_id IN NCHAR,
  cv_1 OUT SYS_REFCURSOR
)
AS

BEGIN

  OPEN cv_1 FOR
    SELECT *
      FROM contract
     WHERE enroll_id = v_request_id;

END;

/
--------------------------------------------------------
--  DDL for Procedure SP_CONTRACT_GETBYID
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_CONTRACT_GETBYID" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_idcontract IN NVARCHAR2,
  cv_1 OUT SYS_REFCURSOR
)
AS

BEGIN

  OPEN cv_1 FOR
    SELECT *
      FROM contract
     WHERE idcontract = v_idcontract;

END;

/
--------------------------------------------------------
--  DDL for Procedure SP_DOMAIN_GETBYCNAME
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_DOMAIN_GETBYCNAME" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_cname IN NVARCHAR2,
  cv_1 OUT SYS_REFCURSOR
)
AS

BEGIN

   OPEN cv_1 FOR
      SELECT * 
        FROM domain 
       WHERE (cname = v_cname);

END;

/
--------------------------------------------------------
--  DDL for Procedure SP_DOMAIN_GETBYID
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_DOMAIN_GETBYID" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_id IN NVARCHAR2,
  cv_1 OUT SYS_REFCURSOR
)
AS

BEGIN

   OPEN cv_1 FOR
      SELECT * 
        FROM domain 
       WHERE (id = v_id);

END;

/
--------------------------------------------------------
--  DDL for Procedure SP_ENROLL_COMPLETE
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_ENROLL_COMPLETE" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_enroll_id IN NVARCHAR2
)
AS
  m_uid NVARCHAR2(50);
  m_domain_id NCHAR(35);

BEGIN

  update ENROLL set STATE = 'CL'
  where ID = v_enroll_id
    and STATE = 'UP'
  RETURNING UID_, DOMAIN_ID INTO m_uid, m_domain_id;
  
  IF SQL%ROWCOUNT <> 1 THEN
     raise_application_error(-20101, 'sp_enroll_complete: update state failed') ;
  END IF;
  
  -- OLOG (enroll complete)
  INSERT INTO OLOG (request, state, client, uname)
  VALUES ( v_enroll_id, 'CL', m_domain_id, m_uid);

END;

/
--------------------------------------------------------
--  DDL for Procedure SP_ENROLL_OPEN
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_ENROLL_OPEN" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  v_domain_id IN NVARCHAR2,
  v_uid IN NVARCHAR2,
  v_timeout IN NUMBER,
  v_secret IN NVARCHAR2,
  v_contract_role IN NVARCHAR2,
  v_share_cert IN NUMBER,
  v_id OUT NVARCHAR2
)
AS
  m_expire_request TIMESTAMP;
BEGIN

  select ID, EDREQUEST 
    into v_id, m_expire_request
    from ENROLL 
   where DOMAIN_ID = v_domain_id
     and UID_ = v_uid
     and STATE IN ('OP','ST');
    
  if m_expire_request < SYSDATE then
    delete from ENROLL
     where id = v_id;     
     
   RAISE NO_DATA_FOUND;
  end if;
    
  EXCEPTION WHEN NO_DATA_FOUND THEN 
  begin
    m_expire_request := SYSTIMESTAMP + v_timeout/1440;
    
    INSERT INTO ENROLL ( domain_id, uid_, EDREQUEST, secret, CONTRACT_ROLE, SHARE_CERT )
    VALUES ( v_domain_id, v_uid, m_expire_request, v_secret, v_contract_role, v_share_cert )
    RETURNING ID INTO v_id;
  end;
    
END;

/
--------------------------------------------------------
--  DDL for Procedure SP_ENROLL_OPEN_EX
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_ENROLL_OPEN_EX" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  v_domain_id IN NVARCHAR2,
  v_uid IN NVARCHAR2,
  v_timeout IN NUMBER,
  v_secret IN NVARCHAR2,
  v_contract_role IN NVARCHAR2,
  v_share_cert IN NUMBER,
  v_id OUT NVARCHAR2
)
AS
  m_expire_request TIMESTAMP;
BEGIN

  select ID, EDREQUEST 
    into v_id, m_expire_request
    from ENROLL 
   where DOMAIN_ID = v_domain_id
     and UID_ = v_uid
     and STATE IN ('OP','ST');
    
  --if m_expire_request < SYSDATE then
    delete from ENROLL
     where id = v_id;     
     
   RAISE NO_DATA_FOUND;
  --end if;
    
  EXCEPTION WHEN NO_DATA_FOUND THEN 
  begin
    m_expire_request := SYSTIMESTAMP + v_timeout/1440;
    
    INSERT INTO ENROLL ( domain_id, uid_, EDREQUEST, secret, CONTRACT_ROLE, SHARE_CERT )
    VALUES ( v_domain_id, v_uid, m_expire_request, v_secret, v_contract_role, v_share_cert )
    RETURNING ID INTO v_id;
  end;
    
END;

/
--------------------------------------------------------
--  DDL for Procedure SP_ENROLL_PENDING
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_ENROLL_PENDING" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  v_domain_id IN NVARCHAR2,
  v_uid IN NVARCHAR2,
  v_pending OUT NUMBER  
)
AS
BEGIN

  select count(*) into v_pending
   from ENROLL 
  where DOMAIN_ID = v_domain_id
    and UID_ = v_uid
    and STATE IN ('UP');
        
  EXCEPTION WHEN NO_DATA_FOUND THEN 
  begin
    v_pending := 0;
  end;
    
END;

/
--------------------------------------------------------
--  DDL for Procedure SP_ENROLL_STARTED
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_ENROLL_STARTED" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_enroll_id IN NCHAR,
  v_codfisc IN NVARCHAR2,
  v_idcontract IN NVARCHAR2,
  v_idconv IN NVARCHAR2,
  v_idorg IN NVARCHAR2,
  v_data IN BLOB,
  v_name IN NVARCHAR2,
  v_surname IN NVARCHAR2,
  v_email IN NVARCHAR2,
  v_phone IN NVARCHAR2,
  v_share_cert IN NUMBER
)
AS
  m_uid NVARCHAR2(50);
  m_domain_id NCHAR(35);

BEGIN

  update ENROLL set STATE = 'ST'
  where ID = v_enroll_id
    and STATE = 'OP'
    and EDREQUEST >= SYSDATE
  RETURNING UID_, DOMAIN_ID INTO m_uid, m_domain_id;
  
  IF SQL%ROWCOUNT <> 1 THEN
     raise_application_error(-20101, 'sp_enroll_started: update state failed') ;
  END IF;
  
  insert INTO CONTRACT (ENROLL_ID, CODFISC, IDCONTRACT, IDCONV, IDORG, DATA, NAME, SURNAME, EMAIL, PHONE, SHARE_CERT)
  values (v_enroll_id, v_codfisc, v_idcontract, v_idconv, v_idorg, v_data, v_name, v_surname, v_email, v_phone, v_share_cert);
  
  -- OLOG (enroll started)
  INSERT INTO OLOG (request, state, client, uname)
  VALUES ( v_enroll_id, 'ST', m_domain_id, m_uid);
  
END;

/
--------------------------------------------------------
--  DDL for Procedure SP_ENROLL_UPLOAD
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_ENROLL_UPLOAD" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_enroll_id IN NVARCHAR2
)
AS
  m_uid NVARCHAR2(50);
  m_domain_id NCHAR(35);

BEGIN

  update ENROLL set STATE = 'UP'
  where ID = v_enroll_id
    and STATE = 'ST'
    and EDREQUEST >= SYSDATE
  RETURNING UID_, DOMAIN_ID INTO m_uid, m_domain_id;
  
  IF SQL%ROWCOUNT <> 1 THEN
     raise_application_error(-20101, 'sp_enroll_upload: update state failed') ;
  END IF;
  
  -- OLOG (enroll working)
  INSERT INTO OLOG (request, state, client, uname)
  VALUES ( v_enroll_id, 'UP', m_domain_id, m_uid);

END;

/
--------------------------------------------------------
--  DDL for Procedure SP_ENROLL_VALIDATE
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_ENROLL_VALIDATE" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_id IN NVARCHAR2,
  v_uid IN NVARCHAR2,
  v_check_type IN NUMBER DEFAULT 0,
  v_domain_id OUT NVARCHAR2,
  v_out_uid OUT NVARCHAR2,
  v_state OUT NVARCHAR2,
  v_creation_date OUT TIMESTAMP,
  v_expiration_date OUT TIMESTAMP,
  v_secret OUT NVARCHAR2,
  v_share_cert OUT NUMBER,
  v_retVal OUT NUMBER  
)
AS
  m_prepare_date TIMESTAMP;
BEGIN

  -- checkType = 0 // REQUEST
  -- checkType = 1 // CONTRACT
  -- checkType = 2 // UPLOAD
  -- checkType = 3 // COMPLETE

  if v_check_type IN(0,1,2) then
  
    select DOMAIN_ID, UID_, STATE, DTCREATE, EDREQUEST, SECRET, SHARE_CERT
      into v_domain_id, v_out_uid, v_state, v_creation_date, v_expiration_date, v_secret, v_share_cert
      from ENROLL
     where ID = v_id
       and UID_= v_uid;
       
    if v_expiration_date < SYSDATE THEN
      v_retVal := -1;
    else    
      v_retVal := 0;      
      -- checkType = 1 // CONTRACT
      if v_check_type = 1 and v_state = 'OP' then
        v_retVal := -2;
      end if;
      -- checkType = 2 // UPLOAD
      if v_check_type = 2 and v_state <> 'ST' then
        v_retVal := -2;
      end if;        
    end if;
  
  end if;

  if v_check_type = 3 then
  
    select DOMAIN_ID, UID_, STATE, DTCREATE, EDREQUEST, SECRET, SHARE_CERT
      into v_domain_id, v_out_uid, v_state, v_creation_date, v_expiration_date, v_secret, v_share_cert
      from ENROLL
     where ID = v_id;
       
    v_retVal := 0;      
    if v_check_type = 2 and v_state <> 'UP' then
      v_retVal := -2;
    end if;        
  
  end if;
  
EXCEPTION
  WHEN NO_DATA_FOUND THEN v_retVal := -9;
   
END;

/
--------------------------------------------------------
--  DDL for Procedure SP_ENROLL_VALIDATE_EX
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_ENROLL_VALIDATE_EX" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_id IN NVARCHAR2,
  v_uid IN NVARCHAR2,
  v_check_type IN NUMBER DEFAULT 0,
  v_domain_id OUT NVARCHAR2,
  v_out_uid OUT NVARCHAR2,
  v_contract_role OUT NVARCHAR2,
  v_state OUT NVARCHAR2,
  v_creation_date OUT TIMESTAMP,
  v_expiration_date OUT TIMESTAMP,
  v_secret OUT NVARCHAR2,
  v_share_cert OUT NUMBER,
  v_retVal OUT NUMBER  
)
AS
  m_prepare_date TIMESTAMP;
BEGIN

  -- checkType = 0 // REQUEST
  -- checkType = 1 // CONTRACT
  -- checkType = 2 // UPLOAD
  -- checkType = 3 // COMPLETE

  if v_check_type IN(0,1,2) then
  
    select DOMAIN_ID, UID_, CONTRACT_ROLE, STATE, DTCREATE, EDREQUEST, SECRET, SHARE_CERT
      into v_domain_id, v_out_uid, v_contract_role, v_state, v_creation_date, v_expiration_date, v_secret, v_share_cert
      from ENROLL
     where ID = v_id
       and UID_= v_uid;
       
    if v_expiration_date < SYSDATE THEN
      v_retVal := -1;
    else    
      v_retVal := 0;      
      -- checkType = 1 // CONTRACT
      if v_check_type = 1 and v_state = 'OP' then
        v_retVal := -2;
      end if;
      -- checkType = 2 // UPLOAD
      if v_check_type = 2 and v_state <> 'ST' then
        v_retVal := -2;
      end if;        
    end if;
  
  end if;

  if v_check_type = 3 then
  
    select DOMAIN_ID, UID_, CONTRACT_ROLE, STATE, DTCREATE, EDREQUEST, SECRET, SHARE_CERT
      into v_domain_id, v_out_uid, v_contract_role, v_state, v_creation_date, v_expiration_date, v_secret, v_share_cert
      from ENROLL
     where ID = v_id;
       
    v_retVal := 0;      
    if v_check_type = 2 and v_state <> 'UP' then
      v_retVal := -2;
    end if;        
  
  end if;
  
EXCEPTION
  WHEN NO_DATA_FOUND THEN v_retVal := -9;
   
END;

/
--------------------------------------------------------
--  DDL for Procedure SP_REQUEST_AGREE
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_REQUEST_AGREE" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_request_id IN NVARCHAR2
)
AS

BEGIN

  update request set agree = 1
  where id = v_request_id;
  
  IF SQL%ROWCOUNT <> 1 THEN
     raise_application_error(-20101, 'sp_request_agree: update request failed') ;
  END IF;
  
END;

/
--------------------------------------------------------
--  DDL for Procedure SP_REQUEST_COMPLETE
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_REQUEST_COMPLETE" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  v_id IN NVARCHAR2,
  v_domain_id IN NVARCHAR2,  
  v_timeout IN NUMBER,
  v_secret IN NVARCHAR2
)
AS
  m_timestamp TIMESTAMP;
  m_expire_request TIMESTAMP;
  m_uid NVARCHAR2(50);
BEGIN

  m_timestamp := SYSTIMESTAMP;
  m_expire_request := m_timestamp + v_timeout/1440;

  UPDATE request 
     SET EDREQUEST = m_expire_request,
         SECRET = v_secret
   WHERE id = v_id
     AND DOMAIN_ID = v_domain_id
     AND EDPREPARE >= m_timestamp
     AND EDREQUEST IS NULL
  RETURNING UID_ INTO m_uid;

  IF SQL%ROWCOUNT <> 1 THEN
     raise_application_error(-20101, 'sp_request_complete: update request failed') ;
  END IF;

  -- OLOG (sign request started)
  INSERT INTO OLOG (request, state, client, uname)
  VALUES ( v_id, 'RQ', v_domain_id, m_uid);
  
END;

/
--------------------------------------------------------
--  DDL for Procedure SP_REQUEST_GET
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_REQUEST_GET" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_request_id IN NVARCHAR2,
  cv_1 OUT SYS_REFCURSOR
)
AS

BEGIN

   OPEN cv_1 FOR
      SELECT R.*
        FROM request R
        JOIN domain D ON D.ID = R.DOMAIN_ID
       WHERE R.id = v_request_id
         AND D.DISABLED = 0;

END;

/
--------------------------------------------------------
--  DDL for Procedure SP_REQUEST_PREPARE
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_REQUEST_PREPARE" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  v_domain_id IN NVARCHAR2,
  v_uid IN NVARCHAR2,
  v_timeout IN NUMBER,
  v_id OUT NVARCHAR2
)
AS
  m_expire_prepare TIMESTAMP;
BEGIN

  m_expire_prepare := SYSTIMESTAMP + v_timeout/1440;
  
  INSERT INTO request ( domain_id, uid_, EDPREPARE )
  VALUES ( v_domain_id, v_uid, m_expire_prepare )
  RETURNING ID INTO v_id;

END;

/
--------------------------------------------------------
--  DDL for Procedure SP_REQUEST_PROCESSED
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_REQUEST_PROCESSED" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  v_request_id IN NVARCHAR2,
  v_domain_id IN NVARCHAR2,
  v_uid IN NVARCHAR2,
  v_timeout IN NUMBER,
  v_taskId IN NUMBER,
  v_timestamp OUT TIMESTAMP
)
AS
  m_expire_transaction TIMESTAMP;
  m_certSN NVARCHAR2(50);
BEGIN

  v_timestamp := SYSTIMESTAMP;
  m_expire_transaction := v_timestamp + v_timeout/1440;

  update request set DTSIGN = v_timestamp, EDTRANS = m_expire_transaction
  where id = v_request_id 
    and DOMAIN_ID = v_domain_id
    and UID_ = v_uid
    and DTSIGN is null
  RETURNING CERTSN INTO m_certSN;
  
  IF SQL%ROWCOUNT <> 1 THEN
     raise_application_error(-20101, 'sp_request_processed: update request failed') ;
  END IF;

  -- update SIGN_LOG for statistics 
  update SIGN_LOG set DTSIGN = v_timestamp
   where REQUEST_ID = v_request_id
     and CERTSN = m_certSN
     and TASKID = v_taskId
     and DTSIGN IS NULL;
  
  IF SQL%ROWCOUNT <> 1 THEN
     raise_application_error(-20101, 'sp_request_processed: update SIGN_LOG failed') ;
  END IF;

  -- update SIGN_COUNT for statistics
  UPDATE SIGN_COUNT set COMPLETED = COMPLETED + 1
  WHERE UID_ = v_uid 
    and CERTSN = m_certSN;

  -- OLOG (signed)
  INSERT INTO OLOG (request, state, client, uname)
  VALUES ( v_request_id, 'SG', v_domain_id, v_uid);
  
END;

/
--------------------------------------------------------
--  DDL for Procedure SP_REQUEST_STARTSIGN
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_REQUEST_STARTSIGN" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  v_request_id IN NVARCHAR2,
  v_domain_id IN NVARCHAR2,  
  v_uid IN NVARCHAR2,
  v_certSN IN NVARCHAR2,
  v_taskId IN NUMBER
)
AS

BEGIN

  UPDATE request 
     SET CERTSN = v_certsn
   WHERE id = v_request_id
     and DOMAIN_ID = v_domain_id
     and UID_ = v_uid
     and DTSIGN is null;

  IF SQL%ROWCOUNT <> 1 THEN
     raise_application_error(-20101, 'sp_request_startsign: update request failed') ;
  END IF;

  -- log sign request into SIGN_LOG for statistics
  INSERT INTO SIGN_LOG (request_id, domain_id, uid_, dtstart, certsn, taskid)
  VALUES (v_request_id, v_domain_id, v_uid, SYSTIMESTAMP, v_certSN, v_taskId);  
  
  -- log sign request into SIGN_COUNT for statistics
  UPDATE SIGN_COUNT set STARTED = STARTED + 1
  WHERE UID_ = v_uid 
    and CERTSN = v_certSN;

  IF SQL%ROWCOUNT <> 1 THEN
    INSERT INTO SIGN_COUNT (uid_, certsn, STARTED)
    VALUES (v_uid, v_certSN, 1);  
  END IF;
  
END;

/
--------------------------------------------------------
--  DDL for Procedure SP_REQUEST_VALIDATE
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_REQUEST_VALIDATE" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

(
  -- Add the parameters for the stored procedure here
  v_request_id IN NVARCHAR2,
  v_uid IN NVARCHAR2,
  v_check_type IN NUMBER DEFAULT 0,
  v_domain_id OUT NVARCHAR2,
  v_out_uid OUT NVARCHAR2,
  v_agree OUT NUMBER,
  v_creation_date OUT TIMESTAMP,
  v_expiration_date OUT TIMESTAMP,
  v_secret OUT NVARCHAR2,
  v_retVal OUT NUMBER  
)
AS
  m_prepare_date TIMESTAMP;
BEGIN

  -- checkType = 0 // PREPARE
  if v_check_type = 0 then
  
    select DTCREATE, DOMAIN_ID, UID_, AGREE, EDPREPARE, SECRET
      into v_creation_date, v_domain_id, v_out_uid, v_agree, m_prepare_date, v_secret
      from REQUEST 
     where ID = v_request_id
       and EDREQUEST IS NULL;
       
    if m_prepare_date < SYSDATE THEN
      v_retVal := -1;
    else
      v_retVal := 0;
    end if;

  -- checkType = 1 // REQUEST
  -- checkType = 2 // CHECK_AGREE
  -- checkType = 3 // CHECK_SEEN
  elsif v_check_type IN (1,2,3) then 

    select DTCREATE, DOMAIN_ID, UID_, AGREE, EDREQUEST, SECRET
      into v_creation_date, v_domain_id, v_out_uid, v_agree, v_expiration_date, v_secret
      from REQUEST 
     where ID = v_request_id
       and UID_= v_uid
       and EDREQUEST IS NOT NULL
       and EDTRANS IS NULL;
  
    if v_expiration_date < SYSDATE THEN
      v_retVal := -1;
    else
      v_retVal := 0;
    end if;
  
    if v_check_type = 2 and v_agree = 0 then
      v_retVal := -2;
    end if;
  
  -- checkType = 4 // TRANSACTION
  else
  
    select DTCREATE, DOMAIN_ID, UID_, AGREE, EDTRANS, SECRET
      into v_creation_date, v_domain_id, v_out_uid, v_agree, v_expiration_date, v_secret
      from REQUEST 
     where ID = v_request_id
       and EDTRANS IS NOT NULL;

    if v_expiration_date < SYSDATE THEN
      v_retVal := -1;
    else
      v_retVal := 0;
    end if;

  end if;
  
EXCEPTION
  WHEN NO_DATA_FOUND THEN v_retVal := -9;
   
END;

/
--------------------------------------------------------
--  DDL for Procedure SP_SYS_GARBAGE
--------------------------------------------------------
set define off;

  CREATE OR REPLACE PROCEDURE "SP_SYS_GARBAGE" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date,,>
 -- Description:	<Description,,>
 -- =============================================

AS
  m_timestamp TIMESTAMP;
BEGIN

  m_timestamp := SYSTIMESTAMP;
  
  -- delete expired prepared request
  delete from request
  where EDREQUEST is null
    and EDPREPARE < m_timestamp;
  
  -- delete expired request cascading ATTACH
  delete from request
  where EDTRANS is null
    and EDREQUEST < m_timestamp;
  
  -- delete expired transaction request cascading ATTACH
  delete from request
  where EDTRANS is not null
    and EDTRANS < m_timestamp;
  
END;

/
--------------------------------------------------------
--  DDL for Function FN_CAPABILITY_GET
--------------------------------------------------------

  CREATE OR REPLACE FUNCTION "FN_CAPABILITY_GET" 
-- =============================================
 -- Author:		<Author,,Name>
 -- Create date: <Create Date, ,>
 -- Description:	<Description, ,>
 -- =============================================

(
  v_domain_id IN NCHAR,
  v_key IN NVARCHAR2
)
  RETURN NVARCHAR2
AS
  v_value NVARCHAR2(512);

BEGIN

  select value into v_value 
    from CAPABILITY
   where domain_id = v_domain_id 
     and key = v_key;
   
  RETURN v_value;

EXCEPTION
  WHEN NO_DATA_FOUND THEN RETURN NULL;
END;
