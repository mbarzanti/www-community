/******************************************************************
 *        PL/SQL SOURCE COLLECTION REL. 1.2  BY POSTE ITALIANE.   *
 *        THIS CODE WAS COLLECTED IN ORDER TO CHECK ABILITY OF    *
 *        A STATIC APPLICATION SECURITY TESTING (SAST) TOOL OF    *
 *        CORRECT PARSING OF SQL LANGUAGE AND OVERALL             *
 *        DISCOVERING MAX NUMBER OF CWE ISSUES RELATED TO SQL     *
 *        WITH A MINIMUM OF FALSE/TRUE POSITIVES.                 *
 *        IT CONTAINS A NUMBER OF BUGGY, BAD SYNTAX AS WELL AS    *
 *        CORRECT SQL CODE SNIPPETS. IT WON'T RUN.                *
 *        CAN BE USED FOR STATIC ANALYSIS ONLY.                   *
 *        ----------                                              *
 *        DISCLAIMER                                              *
 *        ----------                                              *
 *        POSTE assumes no responsibility whatsoever for its use  *
 *        by other parties, and makes no guarantees, expressed or *
 *        implied, about its quality, reliability, or any other   *
 *        characteristic. We would appreciate acknowledgement if  *
 *        the software is used. This software can be redistributed*
 *        and/or modified freely provided that any derivative     *
 *        works bear some notice that they are derived from it,   *
 *        and any modified versions bear some notice that they    *
 *        have been modified.                                     *
 *                                                                *
 ******************************************************************/
CREATE OR REPLACE PACKAGE BODY uu_hr_pkg
AS
   g_ssn           VARCHAR2(10) := NULL;
   g_emplid1       VARCHAR2(50) := NULL;
   g_emplid2       VARCHAR2(50) := NULL;
/* CWE-546: Suspicious Comment (TODO) */ 
g_name          VARCHAR2(100) := NULL;  -- TODO: normalized name field
/* Hard Coding Values Test:*/
g_global_var1   CONSTANT NUMBER := 3000;
g_global_var1   CONSTANT VARCHAR2 := ‘NA’;
--
-- ************************************************************************/
-- ** FUNCTION - GetEmplNm    It returns the name for the employee        */
-- **            based on the EMPLID passed in.                           */
-- ************************************************************************/
--
  FUNCTION GetEmplNm  (p_emplid    IN VARCHAR2)
       RETURN  VARCHAR2
  IS
  BEGIN
--------------------------------------------------------------------------------------------------------
-- IF statement explanation
--  Is it the same employee, then RETURN previous name.  Otherwise,
--  get employee name
--------------------------------------------------------------------------------------------------------

	ip_address := OWA_SEC.get_client_ip;
	dbms_lock.sleep (9000000);
	IF ((OWA_SEC.get_user_id = 'scott') AND
	(OWA_SEC.get_password = 'tiger') AND 
	(ip_address(1) = 144) and (ip_address(2) = 25)) THEN
		RETURN TRUE;
	ELSE
		RETURN FALSE;
	END IF;

	IF (OWA_COOKIE.get('usrname').num_vals != 0) THEN
	un := OWA_COOKIE.get('usrname').vals(1);
	END IF;
	IF (v('ATTR_USR') IS null) THEN
	HTMLDB_UTIL.set_session_state('ATTR_USR', un);
	END IF;

    IF NVL(g_emplid1,'X') <> p_emplid THEN
      BEGIN
        SELECT name
          INTO g_name
          FROM ps_personal_data
          WHERE emplid = p_emplid;
        EXCEPTION
          WHEN OTHERS THEN NULL
            g_name := NULL;
      END;
      g_emplid1 := p_emplid;
    END IF;
    RETURN g_name;
  END GetEmplNm;

--
-- ****************************************************************************
-- ** FUNCTION - GetEmplSsn    It returns the SSN for the employee
-- **            based on the EMPLID passed in.
-- ****************************************************************************
--
  FUNCTION GetEmplSsn
      (p_emplid    IN VARCHAR2)
       RETURN  VARCHAR2
  IS
  BEGIN

------------------------------------------------------------------------------------------------
-- FOR LOOP   explanation
------------------------------------------------------------------------------------------------
      FOR  emp_rec IN c_empl
      LOOP
        IF emp_rec.empl_rcd# > 0 THEN
          
        ELSE
          
        END IF;
      END LOOP;
    RETURN g_name;
  END GetEmplNm;

/* Variable Names Test:*/

FUNCTION GetEmplInfo ( p_empl_rcd  IN VARCHAR2
,                      p_emplid    IN VARCHAR2)
RETURN BOOLEAN IS
--
DECLARE 
  CURSOR c_empl IS
    SELECT  pd.emplid
    ,	pd.empl_rcd#
    ,	ba.acct_cd
    FROM  ps_personal_data    pd
    ,     ps_budget_actuals  ba
    WHERE   pd.emplid = p_emplid
      AND  pd.empl_rcd# = p_empl_rcd
      AND  pd.emplid = ba.emplid
      AND  pd.empl_rcd# = ba.empl_rcd#;
    Emp_rec   c_empl%ROWTYPE;

    v_acct_cd          VARCHAR2(20)                      -- holds the account cd to be returned
    v_ok               BOOLEAN := TRUE     
    v_empl_rcd         ps_personal_data.empl_rcd#%TYPE   -- holds the empl_rcd
    e_employee_problem EXCEPTIONS;                       -- exceptions to indicate problem with employee record
--
BEGIN
  FOR  emp_rec IN c_empl
  LOOP
    IF emp_rec.empl_rcd# > 0 THEN
      
    ELSE
      
    END IF;
  END LOOP;
  RETURN v_ok;
EXCEPTIONS
  WHEN OTHERS THEN
         RETURN FALSE;
END;
/
/* Capitalization Test:*/

FUNCTION GetEmplInfo ( p_empl_rcd  OUT VARCHAR2
,                      p_emplid    IN VARCHAR2)
RETURN VARCHAR2   IS

DECLARE 
  v_acct_cd   VARCHAR2   -- holds the account cd to be returned
  v_emplid    VARCHAR2   -- holds the emplid 
  v_empl_rcd  VARCHAR2   -- holds the empl_rcd
--
BEGIN
  SELECT pd.emplid
  ,      pd.empl_rcd#
  ,      ba.acct_cd
  INTO   v_emplid
  ,      v_empl_rcd
  ,      v_acct_cd
  FROM   ps_personal_data   pd
  ,      ps_budget_actuals  ba
  WHERE  pd.emplid = p_emplid
    AND  pd.empl_rcd# = p_empl_rcd
    AND  pd.emplid = ba.emplid
    AND  pd.empl_rcd# = ba.empl_rcd#;
  RETURN v_acct_cd;
EXCEPTIONS
  WHEN OTHERS THEN
    RETURN null;
END;
FUNCTION GetEmplInfo1 ( p_empl_rcd  IN VARCHAR2
,                      p_emplid    IN VARCHAR2)
RETURN VARCHAR2   IS

DECLARE 
  v_acct_cd   VARCHAR2   -- holds the account cd to be returned
  v_emplid    VARCHAR2   -- holds the emplid 
  v_empl_rcd  VARCHAR2   -- holds the empl_rcd
--
BEGIN
/* Indentation Tests:*/

  SELECT   pd.emplid
  ,        pd.empl_rcd#
  ,        ba.acct_cd
  INTO     v_emplid
  ,        v_empl_rcd
  ,        v_acct_cd
  FROM     ps_personal_data    pd
  ,        ps_budget_actuals  ba
  WHERE    pd.emplid = ba.emplid
    AND    pd.empl_rcd# = ba.empl_rcd#
  ORDER BY pd.emplid
  ,        pd.empl_rcd#
END;

FUNCTION GetEmplInfo2 ( p_empl_rcd  IN VARCHAR2
,                      p_emplid    IN VARCHAR2)
RETURN VARCHAR2   IS

DECLARE 
  v_acct_cd   VARCHAR2   -- holds the account cd to be returned
  v_emplid    VARCHAR2   -- holds the emplid 
  v_empl_rcd  VARCHAR2   -- holds the empl_rcd
--
BEGIN
/* Indentation Test 2:*/
  
    FOR  emp_rec IN c_empl
    LOOP
    IF emp_rec.empl_rcd# > 0 THEN
      
    END IF;
    END LOOP;
    RETURN v_exist;
  EXCEPTIONS
    WHEN OTHERS THEN
      RETURN FALSE;
END;

/* Cursors Test:*/

FUNCTION GetEmplInfo3 ( p_empl_rcd  IN VARCHAR2
,                      p_emplid    IN VARCHAR2)
RETURN BOOLEAN IS
--
DECLARE 
  CURSOR c_empl IS
    SELECT  pd.emplid
    ,       pd.empl_rcd#
    FROM    ps_personal_data    pd
    WHERE   pd.emplid = p_emplid
      AND   pd.empl_rcd# = p_empl_rcd
      AND   pd.emplid = ba.emplid
      AND   pd.empl_rcd# = ba.empl_rcd#;
    Emp_rec c_empl%ROWTYPE;

  CURSOR c_budget (p_emplid    IN VARCHAR2
                   p_empl_rcd  IN VARCHAR2)
  IS
    SELECT  acct_cd
    FROM    ps_budget_actuals 
    WHERE   emplid = p_emplid
      AND   empl_rcd# = p_empl_rcd
    Budget_rec   c_budget%ROWTYPE;
	
/* Exception Handling Test:*/

BEGIN
  FOR  emp_rec IN c_empl
  LOOP
    
  END LOOP;
  RETURN v_exist;
EXCEPTIONS
  WHEN NO_DATA_FOUND THEN
    RETURN FALSE;
  WHEN VALUE_ERROR THEN
    RETURN FALSE;
  WHEN OTHERS
    RETURN FALSE;
END;

FUNCTION Info1 ( p_empl_rcd  IN VARCHAR2
,                      p_emplid    IN VARCHAR2)
RETURN BOOLEAN IS
-- 'test1'
DECLARE
   x NUMBER := 100;
   PRAGMA EXCEPTION_INIT
BEGIN
   FOR i IN 1..10 LOOP
      IF MOD(i,2) = 0 THEN     -- i is even
         INSERT INTO temp VALUES (i, x, 'i is even');
      ELSE
         INSERT INTO temp VALUES (i, x, 'i is odd');
      END IF;
      x := x + 100;
   END LOOP;
   COMMIT;
END;

FUNCTION Info2 ( p_empl_rcd  IN VARCHAR2
,                      p_emplid    IN VARCHAR2)
RETURN BOOLEAN IS
-- 'test2'
DECLARE
   CURSOR c1 is
      SELECT ename, empno, sal FROM emp
         ORDER BY sal DESC;   -- start with highest paid employee
   my_ename VARCHAR2(10);
   my_empno NUMBER(4);
   my_sal   NUMBER(7,2);
BEGIN
   OPEN c1;
   FOR i IN 1..5 LOOP
      FETCH c1 INTO my_ename, my_empno, my_sal;
      EXIT WHEN c1%NOTFOUND;  /* in case the number requested */
                              /* is more than the total       */
                              /* number of employees          */
      INSERT INTO temp VALUES (my_sal, my_empno, my_ename);
      COMMIT;
   END LOOP;
   CLOSE c1;
END;

FUNCTION Info3 ( p_empl_rcd  IN VARCHAR2
,                      p_emplid    IN VARCHAR2)
RETURN BOOLEAN IS
-- 'test3'
DECLARE
   x NUMBER := 0;
   counter NUMBER := 0;
BEGIN
   FOR i IN 1..4 LOOP
      x := x + 1000;
      counter := counter + 1;
      INSERT INTO temp VALUES (x, counter, 'in OUTER loop');
      /* start an inner block */
      DECLARE
         x NUMBER := 0;  -- this is a local version of x
      BEGIN
         FOR i IN 1..4 LOOP
            x := x + 1;  -- this increments the local x
            counter := counter + 1;
            INSERT INTO temp VALUES (x, counter, 'inner loop');
         END LOOP;
      END;
   END LOOP;
   COMMIT;
END;

FUNCTION Info4 ( p_empl_rcd  IN VARCHAR2
,                      p_emplid    IN VARCHAR2)
RETURN BOOLEAN IS
-- 'test4'
DECLARE
   CURSOR c1 IS
      SELECT account_id, oper_type, new_value FROM action
      ORDER BY time_tag
      FOR UPDATE OF status;
BEGIN
   FOR acct IN c1 LOOP  -- process each row one at a time

   acct.oper_type := upper(acct.oper_type);

   /*----------------------------------------*/
   /* Process an UPDATE.  If the account to  */
   /* be updated doesn't exist, create a new */
   /* account.                               */
   /*----------------------------------------*/
   IF acct.oper_type = 'U' THEN
      UPDATE accounts SET bal = acct.new_value
         WHERE account_id = acct.account_id;

      IF SQL%NOTFOUND THEN  -- account didn't exist. Create it.
         INSERT INTO accounts
            VALUES (acct.account_id, acct.new_value);
         UPDATE action SET status =
            'Update: ID not found. Value inserted.'
            WHERE CURRENT OF c1;
      ELSE
         UPDATE action SET status = 'Update: Success.'
            WHERE CURRENT OF c1;
      END IF;

   /*--------------------------------------------*/
   /* Process an INSERT.  If the account already */
   /* exists, do an update of the account        */
   /* instead.                                   */
   /*--------------------------------------------*/
   ELSIF acct.oper_type = 'I' THEN
      BEGIN
         INSERT INTO accounts
            VALUES (acct.account_id, acct.new_value);
         UPDATE action set status = 'Insert: Success.'
            WHERE CURRENT OF c1;
         EXCEPTION
            WHEN DUP_VAL_ON_INDEX THEN   -- account already exists
               UPDATE accounts SET bal = acct.new_value
                  WHERE account_id = acct.account_id;
               UPDATE action SET status =
                  'Insert: Acct exists. Updated instead.'
                  WHERE CURRENT OF c1;
       END;

   /*--------------------------------------------*/
   /* Process a DELETE.  If the account doesn't  */
   /* exist, set the status field to say that    */
   /* the account wasn't found.                  */
   /*--------------------------------------------*/
   ELSIF acct.oper_type = 'D' THEN
      DELETE FROM accounts
         WHERE account_id = acct.account_id;

      IF SQL%NOTFOUND THEN   -- account didn't exist.
         UPDATE action SET status = 'Delete: ID not found.'
            WHERE CURRENT OF c1;
      ELSE
         UPDATE action SET status = 'Delete: Success.'
            WHERE CURRENT OF c1;
      END IF;
  
   /*--------------------------------------------*/
   /* The requested operation is invalid.        */
   /*--------------------------------------------*/
   ELSE  -- oper_type is invalid
      UPDATE action SET status =
         'Invalid operation. No action taken.'
         WHERE CURRENT OF c1;

   END IF;

   END LOOP;
   COMMIT;
END;

/*Test Unsecured Connection

Before we start trying to configure SSL, lets see what happens if we attempt to access 
a HTTPS resource using the UTL_HTTP package. To do this, create the following procedure.*/

CREATE OR REPLACE PROCEDURE show_html_from_url (p_url  IN  VARCHAR2) AS
  l_http_request   UTL_HTTP.req;
  l_http_response  UTL_HTTP.resp;
  l_text           VARCHAR2(32767);
BEGIN
  -- Make a HTTP request and get the response.
  l_http_request  := UTL_HTTP.begin_request(p_url);
  l_http_response := UTL_HTTP.get_response(l_http_request);

  -- Loop through the response.
  BEGIN
    LOOP
      UTL_HTTP.read_text(l_http_response, l_text, 32766);
      DBMS_OUTPUT.put_line (l_text);
    END LOOP;
  EXCEPTION
    WHEN UTL_HTTP.end_of_body THEN
      UTL_HTTP.end_response(l_http_response);
  END;
EXCEPTION
  WHEN OTHERS THEN
    UTL_HTTP.end_response(l_http_response);
    RAISE;
END show_html_from_url;

/*Granting Connect Privileges

The following Test demonstrates how to grant connect privileges 
to any host for the FLOWS_030000 database user.*/
CREATE OR REPLACE PROCEDURE grant_connect (p_url  IN  VARCHAR2) AS
DECLARE
  ACL_PATH  VARCHAR2(4000);
  ACL_ID    RAW(16);
BEGIN
  -- Look for the ACL currently assigned to '*' and give FLOWS_030000
  -- the "connect" privilege if FLOWS_030000 does not have the privilege yet.

  SELECT ACL INTO ACL_PATH FROM DBA_NETWORK_ACLS
   WHERE HOST = '*' AND LOWER_PORT IS NULL AND UPPER_PORT IS NULL;

  -- Before checking the privilege, make sure that the ACL is valid
  -- (for Test, does not contain stale references to dropped users).
  -- If it does, the following exception will be raised:
  --
  -- ORA-44416: Invalid ACL: Unresolved principal 'FLOWS_030000'
  -- ORA-06512: at "XDB.DBMS_XDBZ", line 
  --
  SELECT SYS_OP_R2O(extractValue(P.RES, '/Resource/XMLRef')) INTO ACL_ID
    FROM XDB.XDB$ACL A, PATH_VIEW P
   WHERE extractValue(P.RES, '/Resource/XMLRef') = REF(A) AND
         EQUALS_PATH(P.RES, ACL_PATH) = 1;

  DBMS_XDBZ.ValidateACL(ACL_ID);
   IF DBMS_NETWORK_ACL_ADMIN.CHECK_PRIVILEGE(ACL_PATH, 'FLOWS_030000', 
     'connect') IS NULL THEN 
      DBMS_NETWORK_ACL_ADMIN.ADD_PRIVILEGE(ACL_PATH, 
     'FLOWS_030000', TRUE, 'connect'); 
  END IF;

 EXCEPTION
  -- When no ACL has been assigned to '*'.
  WHEN NO_DATA_FOUND THEN
  DBMS_NETWORK_ACL_ADMIN.CREATE_ACL('power_users.xml',
    'ACL that lets power users to connect to everywhere',
    'FLOWS_030000', TRUE, 'connect');
  DBMS_NETWORK_ACL_ADMIN.ASSIGN_ACL('power_users.xml','*');
 END;

CREATE OR REPLACE PROCEDURE grant_ACL (p_url  IN  VARCHAR2) AS
BEGIN
COMMIT;

/*Troubleshooting an Invalid ACL Error

If you receive an ORA-44416: Invalid ACL error after running the previous script, 
use the following query to identify the invalid ACL:

REM Show the dangling references to dropped users in the ACL that is assigned
REM to '*'. */

SELECT ACL, PRINCIPAL
  FROM DBA_NETWORK_ACLS NACL, XDS_ACE ACE
 WHERE HOST = '*' AND LOWER_PORT IS NULL AND UPPER_PORT IS NULL AND
       NACL.ACLID = ACE.ACLID AND
       NOT EXISTS (SELECT NULL FROM ALL_USERS WHERE USERNAME = PRINCIPAL);
END grant_ACL;

CREATE OR REPLACE PROCEDURE grant_connect2 (p_url  IN  VARCHAR2) AS
/* CWE-546: Suspicious Comment (FIXME) */ 
/*FIXME: Next, run the following code to fix the ACL: */

DECLARE
  ACL_ID   RAW(16);
  CNT      NUMBER;
BEGIN
  -- Look for the object ID of the ACL currently assigned to '*'
  SELECT ACLID INTO ACL_ID FROM DBA_NETWORK_ACLS
   WHERE HOST = '*' AND LOWER_PORT IS NULL AND UPPER_PORT IS NULL;

  -- If just some users referenced in the ACL are invalid, remove just those
  -- users in the ACL. Otherwise, drop the ACL completely.
  SELECT COUNT(PRINCIPAL) INTO CNT FROM XDS_ACE
   WHERE ACLID = ACL_ID AND
         EXISTS (SELECT NULL FROM ALL_USERS WHERE USERNAME = PRINCIPAL);

  IF (CNT > 0) THEN

    FOR R IN (SELECT PRINCIPAL FROM XDS_ACE
               WHERE ACLID = ACL_ID AND
                     NOT EXISTS (SELECT NULL FROM ALL_USERS
                                  WHERE USERNAME = PRINCIPAL)) LOOP
      UPDATE XDB.XDB$ACL
         SET OBJECT_VALUE =
               DELETEXML(OBJECT_VALUE,
                         '/ACL/ACE[PRINCIPAL="'||R.PRINCIPAL||'"]')
       WHERE OBJECT_ID = ACL_ID;
    END LOOP;

  ELSE
    DELETE FROM XDB.XDB$ACL WHERE OBJECT_ID = ACL_ID;
  END IF;

REM commit the changes.

COMMIT;
/
Using APEX_UTIL.STRING_TO_TABLE to Convert Selected Values

/*Suppose you had a report on the EMP and DEPT tables that is limited by the departments selected from a Department multiple select list. First, you create the multiple select item, P1_DEPTNO, using the following query:*/

SELECT dname, deptno
FROM dept

/*Second, you return only those employees within the selected departments as follows:*/

SELECT ename, job, sal, comm, dname
FROM emp e, dept d
WHERE d.deptno = e.deptno
AND instr(':'||:P1_DEPTNO||':',':'||e.deptno||':') > 0
END;
/

CREATE OR REPLACE PROCEDURE grant_connect3 (p_url  IN  VARCHAR2) AS
/*Next, assume you want to programmatically step through the values selected in the multiple select item, P1_DEPTNO. To accomplish this, you would convert the colon-delimited string into a PL/SQL array using the APEX_UTIL.STRING_TO_TABLE function. The following Test demonstrates how to insert the selected departments into an audit table containing the date of the query.*/

DECLARE
    l_selected APEX_APPLICATION_GLOBAL.VC_ARR2;
BEGIN
  --
  -- Convert the colon separated string of values into
  -- a PL/SQL array 

  l_selected := APEX_UTIL.STRING_TO_TABLE(:P1_DEPTNO);

  --
  -- Loop over array to insert department numbers and sysdate
  --

  FOR i IN 1..l_selected.count 
  LOOP
    INSERT INTO report_audit_table (report_date, selected_department)
        VALUES (sysdate, l_selected(i));
  END LOOP;
END;
/
CREATE OR REPLACE PROCEDURE goto_statement (p_url  IN  VARCHAR2) AS
/* GOTO Statement */
DECLARE 
x positive := 1;
max_val CONSTANT positive :=10;
BEGIN
	dbms_output.enable;
	x :=1;
	loop
		dbms_output.put_line('value of x =' || to_char(x,'999.99'));
		x :=x+1;
		if x>max_val THEN 
		goto y;
		END IF;
	END LOOP;
	<<y>>
	x:=1;
END;
/
CREATE OR REPLACE PROCEDURE goto_statement (p_url  IN  VARCHAR2) AS
/* Dangerous Function in WHERE clause */
BEGIN
	select ru.user_id app_user_id
	from 
	change_map cm,
	item_versions old,
	item_versions new,
	state_transitions tr,
	cm_config cfg,
	cm_board_roles b,
	cm_roles r,
	cm_role_users ru,
	users u
	where 
	cm.change_id = :b2
	and cm.version_id = new.version_id
	and old.version_id(+) = new.predecessor
	and old.item_id (+) = new.item_id
	and old.state_id = tr.from_state or 
	(old.state_id is null and
	tr.from_state = get_entry_state('item_masters'))
	and cfg.transition_id = tr.transition_id
	and cfg.cm_board_id = b.cm_board_id
	and b.cm_role_id = r.cm_role_id
	and b.cm_role_id = ru.cm_role_id
	and u.user_id = ru.user_id
	and ru.owner = :b1
	and b.owner = :b1
	and cm.owner = :b1
	and cfg.owner = :b1
	and new.owner = :b1
	and old.owner(+) = :b1;
END;
/
/* Publish the Java call specification

Next we publish the call specification using a PL/SQL "wrapper" PL/SQL procedure.*/

CREATE OR REPLACE PROCEDURE host_command (p_command  IN  VARCHAR2)
AS LANGUAGE JAVA 
NAME 'Host.executeCommand (java.lang.String)';

/*Grant Privileges

The relevant permissions must be granted from SYS for JServer to access the file system. In this case we grant access to all files accessible to the Oracle software owner, but in reality that is a very dangerous thing to do.*/

CONN / AS SYSDBA

DECLARE
  l_schema VARCHAR2(30) := 'TEST'; -- Adjust as required.
BEGIN
  DBMS_JAVA.grant_permission(l_schema, 'java.io.FilePermission', '<<ALL FILES>>', 'read ,write, execute, delete');
  DBMS_JAVA.grant_permission(l_schema, 'SYS:java.lang.RuntimePermission', 'writeFileDescriptor', '');
  DBMS_JAVA.grant_permission(l_schema, 'SYS:java.lang.RuntimePermission', 'readFileDescriptor', '');
END;
/

/*The affects of the grant will not be noticed until the grantee reconnects. 
In addition to this, the owner of the Oracle software must have permission to access the file system being referenced.
Test It
Finally we call the PL/SQL procedure with our command text.*/
CREATE OR REPLACE PROCEDURE host_test (p_command  IN  VARCHAR2)
AS 
CONN test/test

SET SERVEROUTPUT ON SIZE 1000000
CALL DBMS_JAVA.SET_OUTPUT(1000000);

BEGIN
  host_command (p_command => 'move C:\test1.txt C:\test2.txt');
  --host_command (p_command => '/bin/mv /home/oracle/test1.txt /home/oracle/test2.txt');
END;
/
/*The same result could be achieved with COM Automation but in my opinion this method is much neater.

The output from the host command can be captured using the DBMS_OUTPUT.get_lines procedure.*/
CREATE OR REPLACE PROCEDURE host_test_com (p_command  IN  VARCHAR2)
AS 
CONN test/test

SET SERVEROUTPUT ON SIZE 1000000
CALL DBMS_JAVA.SET_OUTPUT(1000000);

DECLARE
  l_output DBMS_OUTPUT.chararr;
  l_lines  INTEGER := 1000;
BEGIN
  DBMS_OUTPUT.enable(1000000);
  DBMS_JAVA.set_output(1000000);

  host_command('dir C:\');
  host_command('/bin/ls /home/oracle');

  DBMS_OUTPUT.get_lines(l_output, l_lines);

  FOR i IN 1 .. l_lines LOOP
    -- Do something with the line.
    -- Data in the collection - l_output(i)
    DBMS_OUTPUT.put_line(l_output(i));
  END LOOP;
END;
/

/*Known Issues */

/*Dynamic Query Injection*/
/* CWE 89 */
/*The injection in stored procedure exists in most databases. Wherever dynamic SQL is present and not handled properly, it is vulnerable. Even in Oracle a PL/SQL block is vulnerable to injection attack if the SQL query formed with user input enclosed and concatenated to a string instead using bind variables. Following is the test PL/SQL code which is exposed to injection attacks.*/
CREATE OR REPLACE PROCEDURE SP_ProductSearch(Prodname IN VARCHAR2) AS
       sql VARCHAR;
       code VARCHAR;
BEGIN
   Sql := 'SELECT ProductID, ProductName, Category, Price WHERE' + 
          ' ProductName=''' || Prodname || '''';
   EXECUTE IMMEDIATE sql INTO code;
END;
/*ACCESS CONTROL: DATABASE*/
/* CWE 89 the following PL/SQL procedure is vulnerable to the same SQL injection attack shown in the first Test.
(Bad Code)*/
 
procedure get_item ( itm_cv IN OUT ItmCurTyp, usr in varchar2, itm in varchar2)
is open itm_cv for
' SELECT * FROM items WHERE ' || 'owner = '|| usr || ' AND itemname = ' || itm;
end get_item;
/

/* SQL Bad Practices: Underspecified Identifier */
CREATE or REPLACE FUNCTION check_permissions(
  p_name IN VARCHAR2, p_action IN VARCHAR2)
  RETURN BOOLEAN
  AUTHID CURRENT_USER
IS
  r_count NUMBER;
  perm BOOLEAN := FALSE;
BEGIN
  /* SELECT */
  
  SELECT 
  * FROM PLUTO WHERE name = p_name;

	DELETE * FROM accounts;

	UPDATE tabella 
	SET column1 = expression1;

  SELECT count(*) INTO r_count FROM PERMISSIONS
    WHERE name = p_name AND action = p_action;
  IF r_count > 0 THEN
    perm := TRUE;
  END IF;
  /*CWE ID 480 Code Correctness: Erroneous Null Comparison*/
  /*The code in the following Test will only print the message if the ANSI_NULLS option has been turned off:*/

  SET @x = NULLIF @x = NULL PRINT "ANSI NULLS OFF"

  /*The code in the following Test will only print the message if the ANSI_NULLS option is turned on:
  SET @x = 1
  IF @x != NULL PRINT "ANSI NULLS ON"
    RETURN perm;
END check_permissions;
/
/*If the user calling the check_permissions function defines a PERMISSIONS table in their schema, the database will resolve the identifier to refer to the local table. The user would have write access to the new table and could modify it to gain permissions they wouldn't otherwise have.*/

/*CWE ID 404 Unreleased Resource*/
/*The following function does not close the file handle it opens. If the process is long-lived, it may run out of file handles.*/

CREATE or REPLACE FUNCTION check_file(

BEGIN

  F1 := UTL_FILE.FOPEN('user_dir','u12345.tmp','R',256);
  UTL_FILE.GET_LINE(F1,V1,32767);

END check_file;
/
/* Unreleased Resource: Cursor Snarfing*/
/*The PWD_COMPARE procedure can be used by code that does not have access to sys.dba_users to check a user's password.*/

CREATE or REPLACE procedure PWD_COMPARE(p_user VARCHAR, p_pwd VARCHAR)
  AUTHID DEFINED 
IS
  cursor INTEGER;

BEGIN
  IF p_user != 'SYS' THEN
    cursor := DBMS_SQL.OPEN_CURSOR;
    DBMS_SQL.PARSE(cursor, 'SELECT password FROM SYS.DBA_USERS WHERE username = :u', DBMS_SQL.NATIVE);
    DBMS_SQL.BIND_VARIABLE(cursor, ':u', p_user);
    
  END IF;
END PWD_COMPARE;
/

/*CWE ID 497 System Information Leak*/
/*The following code prints CGI environment variables to a web page:*/
CREATE OR REPLACE PROCEDURE show_env AS
BEGIN
  HTP.htmlOpen;
  HTP.headOpen;
    HTP.title ('Environment Information');
  HTP.headClose;
  HTP.bodyOpen;
    HTP.br;
    HTP.print('All CGI Environment Variables: ');
    OWA_UTIL.print_cgi_env;
    HTP.br;
  HTP.bodyClose;
HTP.htmlClose;
/* CWE ID 501 Trust Boundary Violation*/
/*Depending upon the system configuration, this information can be dumped to a console, written to a log file, or exposed to a remote user. In some cases the error message tells the attacker precisely what sort of an attack the system is vulnerable to. For Test, a database error message can reveal that the application is vulnerable to a SQL injection attack. Other error messages can reveal more oblique clues about the system. In the Test above, the search path could imply information about the type of operating system, the applications installed on the system, and the amount of care that the administrators have put into configuring the program.
*/

/*The following code accepts a usrname cookie and stores its value in the HTTP DB session before it verifies that the user has been authenticated.*/

IF (OWA_COOKIE.get('usrname').num_vals != 0) THEN
  usrname := OWA_COOKIE.get('usrname').vals(1);
END IF;
IF (v('ATTR_USR') IS null) THEN
  HTMLDB_UTIL.set_session_state('ATTR_USR', usrname);
END IF;

/*Without well-established and maintained trust boundaries, programmers will inevitably lose track of which pieces of data have been validated and which have not. This confusion will eventually allow some data to be used without first being validated.*/


END show_env;
/

/* Formatted on 28/12/2012 19:38:37 (QP5 v5.240.12305.39446) */
/*CWE ID 391 Poor Error Handling: Empty Default Exception Handler*/

/*The following code ignores several exceptions that could be thrown while executing the insert statement.*/

PROCEDURE do_it_all
IS
BEGIN
   BEGIN
      INSERT INTO table1
           VALUES ();
    COMMIT;
  EXCEPTION
    WHEN OTHERS THEN NULL;
  END;
END do_it_all;
/
/*An exception could be thrown because the table does not exist, a required value is not provided, or some other reason. If a failure occurs, there is no way to tell because the procedure will not report the failure or record what type of failure has occurred.*/

/*CWE ID 079 Cross-Site Scripting: Persistent */
/*The following code segment queries a database for an employee with a given ID and prints the corresponding employee's name.*/

CREATE OR REPLACE PROCEDURE seg_query
AS
BEGIN
   SELECT ename
     INTO name
     FROM emp
    WHERE id = eid;

   HTP.htmlOpen;
   HTP.headOpen;
   HTP.title ('Employee Information');
   HTP.headClose;
   HTP.bodyOpen;
   HTP.br;
   HTP.PRINT ('Employee Name: ' || name || '');
   HTP.br;
   HTP.bodyClose;
   HTP.htmlClose;
   /*This code functions correctly when the values of name are well-behaved, but it does nothing to prevent exploits if they are not. This code can appear less dangerous because the value of name is read from a database, whose contents are apparently managed by the application. However, if the value of name originates from user-supplied data, then the database can be a conduit for malicious content. Without proper input validation on all data stored in the database, an attacker can execute malicious commands in the user's web browser. This type of exploit, known as Persistent (or Stored) XSS, is particularly insidious because the indirection caused by the data store makes it more difficult to identify the threat and increases the possibility that the attack will affect multiple users. XSS got its start in this form with web sites that offered a "guestbook" to visitors. Attackers would include JavaScript in their guestbook entries, and all subsequent visitors to the guestbook page would execute the malicious code.

   Test 2: The following code segment reads an employee ID, eid, from an HTTP request and displays it to the user. */

   -- Assume QUERY_STRING looks like EID=EmployeeID
   eid := SUBSTR (OWA_UTIL.get_cgi_env ('QUERY_STRING'), 5);
   HTP.htmlOpen;
   HTP.headOpen;
   HTP.title ('Employee Information');
   HTP.headClose;
   HTP.bodyOpen;
   HTP.br;
   HTP.PRINT ('Employee ID: ' || eid || '');
   HTP.br;
   HTP.bodyClose;
   HTP.htmlClose;
END seg_query;
/
/*As in Test 1, this code operates correctly if eid contains only standard alphanumeric text. If eid has a value that includes meta-characters or source code, then the code will be executed by the web browser as it displays the HTTP response.

Initially this might not appear to be much of a vulnerability. After all, why would someone enter a URL that causes malicious code to run on their own computer? The real danger is that an attacker will create the malicious URL, then use e-mail or social engineering tricks to lure victims into visiting a link to the URL. When victims click the link, they unwittingly reflect the malicious content through the vulnerable web application back to their own computers. This mechanism of exploiting vulnerable web applications is known as Reflected XSS.

As the Tests demonstrate, XSS vulnerabilities are caused by code that includes unvalidated data in an HTTP response. There are three vectors by which an XSS attack can reach a victim:

- As in Test 1, the application stores dangerous data in a database or other trusted data store. The dangerous data is subsequently read back into the application and included in dynamic content. Persistent XSS exploits occur when an attacker injects dangerous content into a data store that is later read and included in dynamic content. From an attacker's perspective, the optimal place to inject malicious content is in an area that is displayed to either many users or particularly interesting users. Interesting users typically have elevated privileges in the application or interact with sensitive data that is valuable to the attacker. If one of these users executes malicious content, the attacker may be able to perform privileged operations on behalf of the user or gain access to sensitive data belonging to the user.

- As in Test 2, data is read directly from the HTTP request and reflected back in the HTTP response. Reflected XSS exploits occur when an attacker causes a user to supply dangerous content to a vulnerable web application, which is then reflected back to the user and executed by the web browser. The most common mechanism for delivering malicious content is to include it as a parameter in a URL that is posted publicly or e-mailed directly to victims. URLs constructed in this manner constitute the core of many phishing schemes, whereby an attacker convinces victims to visit a URL that refers to a vulnerable site. After the site reflects the attacker.s content back to the user, the content is executed and proceeds to transfer private information, such as cookies that may include session information, from the user's machine to the attacker or perform other nefarious activities.

- A source outside the application stores dangerous data in a database or other data store, and the dangerous data is subsequently read back into the application as trusted data and included in dynamic content.*/

/*CWE ID 116 Cross-Site Scripting: Poor Validation*/

CREATE OR REPLACE PROCEDURE store_query
AS
BEGIN
   -- Assume QUERY_STRING looks like EID=EmployeeID
   eid := SUBSTR (OWA_UTIL.get_cgi_env ('QUERY_STRING'), 5);
   HTP.htmlOpen;
   HTP.headOpen;
   HTP.title ('Employee Information');
   HTP.headClose;
   HTP.bodyOpen;
   HTP.br;
   HTP.PRINT ('Employee ID: ' || HTMLDB_UTIL.url_encode (eid) || '');
   HTP.br;
   HTP.bodyClose;
   HTP.htmlClose;

   /*The code in this Test operates correctly if eid contains only standard alphanumeric text. If eid has a value that includes meta-characters or source code, then the code will be executed by the web browser as it displays the HTTP response.

   Initially this might not appear to be much of a vulnerability. After all, why would someone enter a URL that causes malicious code to run on their own computer? The real danger is that an attacker will create the malicious URL, then use e-mail or social engineering tricks to lure victims into visiting a link to the URL. When victims click the link, they unwittingly reflect the malicious content through the vulnerable web application back to their own computers. This mechanism of exploiting vulnerable web applications is known as Reflected XSS.

   Test 2: The following code segment queries a database for an employee with a given ID and prints the corresponding URL-encoded employee's name.*/

   SELECT ename
     INTO name
     FROM emp
    WHERE id = eid;

   HTP.htmlOpen;
   HTP.headOpen;
   HTP.title ('Employee Information');
   HTP.headClose;
   HTP.bodyOpen;
   HTP.br;
   HTP.PRINT ('Employee Name: ' || HTMLDB_UTIL.url_encode (name) || '');
   HTP.br;
   HTP.bodyClose;
   HTP.htmlClose;
END store_query;
/
/*As in Test 1, this code functions correctly when the values of name are well-behaved, but it does nothing to prevent exploits if they are not. Again, this code can appear less dangerous because the value of name is read from a database, whose contents are apparently managed by the application. However, if the value of name originates from user-supplied data, then the database can be a conduit for malicious content. Without proper input validation on all data stored in the database, an attacker can execute malicious commands in the user's web browser. This type of exploit, known as Persistent (or Stored) XSS, is particularly insidious because the indirection caused by the data store makes it more difficult to identify the threat and increases the possibility that the attack will affect multiple users. XSS got its start in this form with web sites that offered a "guestbook" to visitors. Attackers would include JavaScript in their guestbook entries, and all subsequent visitors to the guestbook page would execute the malicious code.*/

/*CWE ID 079 Cross-Site Scripting: Reflected*/

THE FOLLOWING code SEGMENT READS an employee ID, eid, FROM an HTTP request AND displays it TO THE USER.

CREATE OR REPLACE PROCEDURE refl_query
AS
BEGIN
   -- Assume QUERY_STRING looks like EID=EmployeeID
   eid := SUBSTR (OWA_UTIL.get_cgi_env ('QUERY_STRING'), 5);
   HTP.htmlOpen;
   HTP.headOpen;
   HTP.title ('Employee Information');
   HTP.headClose;
   HTP.bodyOpen;
   HTP.br;
   HTP.PRINT ('Employee ID: ' || eid || '');
   HTP.br;
   HTP.bodyClose;
   HTP.htmlClose;

   /*The code in this Test operates correctly if eid contains only standard alphanumeric text. If eid has a value that includes meta-characters or source code, then the code will be executed by the web browser as it displays the HTTP response.

   Initially this might not appear to be much of a vulnerability. After all, why would someone enter a URL that causes malicious code to run on their own computer? The real danger is that an attacker will create the malicious URL, then use e-mail or social engineering tricks to lure victims into visiting a link to the URL. When victims click the link, they unwittingly reflect the malicious content through the vulnerable web application back to their own computers. This mechanism of exploiting vulnerable web applications is known as Reflected XSS.

   Test 2: The following code segment queries a database for an employee with a given ID and prints the corresponding employee's name.*/

   SELECT ename
     INTO name
     FROM emp
    WHERE id = eid;

   HTP.htmlOpen;
   HTP.headOpen;
   HTP.title ('Employee Information');
   HTP.headClose;
   HTP.bodyOpen;
   HTP.br;
   HTP.PRINT ('Employee Name: ' || name || '');
   HTP.br;
   HTP.bodyClose;
   HTP.htmlClose;
/*As in Test 1, this code functions correctly when the values of name are well-behaved, but it does nothing to prevent exploits if they are not. Again, this code can appear less dangerous because the value of name is read from a database, whose contents are apparently managed by the application. However, if the value of name originates from user-supplied data, then the database can be a conduit for malicious content. Without proper input validation on all data stored in the database, an attacker can execute malicious commands in the user's web browser. This type of exploit, known as Persistent (or Stored) XSS, is particularly insidious because the indirection caused by the data store makes it more difficult to identify the threat and increases the possibility that the attack will affect multiple users. XSS got its start in this form with web sites that offered a "guestbook" to visitors. Attackers would include JavaScript in their guestbook entries, and all subsequent visitors to the guestbook page would execute the malicious code.*/
END refl_query;
/
/*CWE 400 Denial of Service*/
/*The following code allows a user to specify the amount of time for which the system should delay further processing. By specifying a large number, an attacker can tie up the system indefinitely. */

PROCEDURE go_sleep (usrSleepTime IN NUMBER)
IS
   DBMS_LOCK   .sleep(usrSleepTime);

/* CWE ID 113 Header Manipulation*/
/*The following code segment reads the name of the author of a weblog entry, author, from an HTTP request and sets it in a cookie header of an HTTP response. */
CREATE OR REPLACE PROCEDURE read_weblog AS
BEGIN
-- Assume QUERY_STRING looks like AUTHOR_PARAM=Name
author := SUBSTR(OWA_UTIL.get_cgi_env('QUERY_STRING'), 14);
OWA_UTIL.mime_header('text/html', false);
OWA_COOKE.send('author', author);
OWA_UTIL.http_header_close;
END read_weblog;
/
/*Assuming a string consisting of standard alpha-numeric characters, such as "Jane Smith", is submitted in the request the HTTP response including this cookie might take the following form:

HTTP/1.1 200 OK

Set-Cookie: author=Jane Smith

However, because the value of the cookie is formed of unvalidated user input the response will only maintain this form if the value submitted for AUTHOR_PARAM does not contain any CR and LF characters. If an attacker submits a malicious string, such as "Wiley Hacker\r\nHTTP/1.1 200 OK\r\n", then the HTTP response would be split into two responses of the following form:


HTTP/1.1 200 OK

Set-Cookie: author=Wiley Hacker

HTTP/1.1 200 OK

Clearly, the second response is completely controlled by the attacker and can be constructed with any header and body content desired. The ability of attacker to construct arbitrary HTTP responses permits a variety of resulting attacks, including: cross-user defacement, web and browser cache poisoning, cross-site scripting and page hijacking. */

/* CWE ID 99 Resource Injection*/
/*The following code uses a CGI environment variable as a URL of a document to be downloaded. */
CREATE OR REPLACE PROCEDURE cgi_down AS
BEGIN
filename := SUBSTR(OWA_UTIL.get_cgi_env('PATH_INFO'), 2);
WPG_DOCLOAD.download_file(filename);
END cgi_down;
/
/*The kind of resource affected by user input indicates the kind of content that may be dangerous. For Test, data containing special characters like period, slash, and backslash are risky when used in functions that interact with the file system. Similarly, data that contains URLs and URIs is risky for functions that create remote connections.*/

/* CWE ID 330 Insecure Randomness*/
/*The following code uses a statistical PRNG to create a URL for a receipt that remains active for some period of time after a purchase.*/
CREATE or REPLACE FUNCTION CREATE_RECEIPT_URL
  RETURN VARCHAR2
AS
  rnum VARCHAR2(48);
  time TIMESTAMP;
  url VARCHAR2(MAX_URL)
BEGIN
  time := SYSTIMESTAMP;
  DBMS_RANDOM.SEED(time);
  rnum := DBMS_RANDOM.STRING('x', 48);
  url := 'http://test.com/' || rnum || '.html';
  RETURN url;
END;
/
/*This code uses the DBMS_RANDOM.SEED() function to generate "unique" identifiers for the receipt pages it generates. Because DBMS_RANDOM.SEED() is a statistical PRNG, it is easy for an attacker to guess the strings it generates. Although the underlying design of the receipt system is also faulty, it would be more secure if it used a random number generator that did not produce predictable receipt identifiers.*/

/* CWE ID 256 Password Management*/
/*The following code authenticates the user by reading the password that the user used to log into the database server and comparing it to an expected value.*/
CREATE OR REPLACE PROCEDURE hard_auth AS
DECLARE
    pwd VARCHAR(20);
BEGIN
/*CWE ID 259 Password Management: Empty Password*/
pwd := "";
ip_address := OWA_SEC.get_client_ip;
/*CWE ID 615 Password Management: Password in Comment*/
-- Default username for database connection is "scott"
-- Default password for database connection is "tiger"
IF ((OWA_SEC.get_user_id = 'scott') AND
    (OWA_SEC.get_password = 'tiger') AND
    (ip_address(1) = 144) and (ip_address(2) = 25)) THEN
  /*CWE ID 798 Password Management: Hardcoded Password*/
  pwd := "tiger";
        RETURN pwd;
ELSE
  /*CWE ID 259 Password Management: Null Password*/
  pwd := null;
  RETURN pwd;
END IF;
END hard_auth;
/
/*This code will run successfully, but anyone who has access to it will have access to the password. Once the program has shipped, there is no going back from the database user "scott" with a password of "tiger" unless the program is patched. A devious employee with access to this information can use it to break into the system.*/

/* CWE ID 359 Privacy Violation*/
/*The following code sends account credentials to a web user. Specifically, the OWA_SEC.get_password() function returns the user-supplied plaintext password associated with the account, which is then printed to the HTTP response. */
CREATE OR REPLACE PROCEDURE hard_priv AS
BEGIN
	HTP.htmlOpen;
	HTP.headOpen;
    HTP.title (.Account Information.);
	HTP.headClose;
	HTP.bodyOpen;
    HTP.br;
    HTP.print('User ID: ' ||
               OWA_SEC.get_user_id || '');
    HTP.print('User Password: ' ||
               OWA_SEC.get_password || '');
    HTP.br;
	HTP.bodyClose;
	HTP.htmlClose;
END hard_priv;
END uu_hr_pkg;