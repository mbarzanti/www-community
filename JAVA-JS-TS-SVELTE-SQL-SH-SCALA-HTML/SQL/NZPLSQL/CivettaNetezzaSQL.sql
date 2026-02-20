CREATE FUNCTION [database_name.]function_name (parameters)
RETURNS data_type AS
BEGIN
    SQL statements
    RETURN value
END;
    
ALTER FUNCTION [database_name.]function_name (parameters)
RETURNS data_type AS
BEGIN
    SQL statements
    RETURN value
END;
    
DROP FUNCTION [database_name.]function_name;

SELECT
    job_title,
    LISTAGG(
        first_name,
        ','
    ) WITHIN GROUP(
    ORDER BY
        first_name
    ) AS employees
FROM
    employees
GROUP BY
    job_title
ORDER BY
    job_title;

SELECT REPLACE('abc', 'a', '1') AS STR; --VIOLAZ

TRY
	--VIOLAZ Empty Try Block
CATCH
	RAISE NOTICE 'Exception Raised: %', SQLERRM;
END TRY

SELECT TOOLKIT.SQLEXT.REPLACE('abc', 'a', '1') AS STR; --OK
TRY
	EXECUTE IMMEDIATE  'select * from TABLE1';  --VIOLAZ
CATCH
	-- VIOLAZ Empty Catch Block
END TRY

TRY
  BEGIN WORK
  EXECUTE IMMEDIATE  SELECT * INTO :FIRSTNAME, :LASTNAME, :EMPNO, :SALARY
     FROM EMP
     WHERE EMPNO = '528671'
     FOR UPDATE; –VIOLAZ
  COMMIT WORK
CATCH
  ROLLBACK WORK
END TRY

If(OK=1)
	-- VIOLAZ Empty Control Statement
ELSE
    return hp_true_filename(parentid) || '/' || fullname;
	SQLStmt:="SELECT ename FROM emp" -- VIOLAZ Code is Unreachable 
	SQL EXECUTE(SQLStmt;[Employee]Name)
END IF;

If(1=1) -- VIOLAZ Expression is always True
	$FirstName_value:="John"
ELSE IF (1=0) -- VIOLAZ Expression is always False
	$FirstName_value:="Mark"
END IF;

CREATE TABLE T1 (
             ID BIGINT NULL,
             CAT VARCHAR(255) NULL,
             M_ID BIGINT NULL,
             T_CAT  VARCHAR(255) NULL,
             NUM BIGINT NULL) 

    INSERT INTO T1
    VALUES -- VIOLAZ Insert Without Column List
    (32,'Math',945,'Red',2),
    (6,'English',232,'Blue',2)

CREATE TEMP TABLE temp_table1 AS  –VIOLAZ
(
 SMALLINT_COLUMN SMALLINT, MONEY_COLUMN MONEY
) DISTRIBUTE ON RANDOM;

CREATE TABLE NETEZZA_TABLE_NAME ( 
 BYTEINT_COLUMN BYTEINT, –VIOLAZ
 SMALLINT_COLUMN SMALLINT, 
 INTEGER_COLUMN INTEGER, –VIOLAZ
 BIGINT_COLUMN BIGINT,
 BOOLEAN_COLUMN BOOLEAN, –VIOLAZ
 CHAR_COLUMN CHARACTER(2), –VIOLAZ
 VARCHAR_COLUMN CHARACTER VARYING(10),
 NCHAR_COLUMN NATIONAL CHARACTER(10),
 DATE_COLUMN DATE,  –VIOLAZ
 TIME_COLUMN TIME, –VIOLAZ
 TIME_WITH_TIME_ZONE_COLUMN TIME WITH TIME ZONE, –VIOLAZ
 TIMESTAMP_COLUMN TIMESTAMP, –VIOLAZ
 INTERVAL_COLUMN INTERVAL, –VIOLAZ
 DOUBLE_PRECISION_COLUMN DOUBLE PRECISION, –VIOLAZ
 FLOAT_COLUMN DOUBLE PRECISION, –VIOLAZ
 NUMERIC_COLUMN NUMERIC(18,0), –VIOLAZ
 NVARCHAR_COLUMN NATIONAL CHARACTER VARYING(10), –VIOLAZ
 REAL_COLUMN REAL,
 ST_GEOMETRY_COLUMN ST_GEOMETRY(10), –VIOLAZ
 VARBINARY_COLUMN BINARY VARYING(10), –VIOLAZ
 DECIMAL_COLUMN DECIMAL(16,2),
 FLOAT_COLUMN FLOAT(6),
 MONEY_COLUMN MONEY
) DISTRIBUTE ON (BIGINT_COLUMN) |[ DISTRIBUTE ON RANDOM] ;

CREATE TABLE <tablename> (col1 int, col2 int, col3 int)
DISTRIBUTE ON RANDOM
ORGANIZE ON (<col>) ; –VIOLAZ

ALTER VIEWS ON customer MATERIALIZE SUSPEND
CREATE OR REPLACE MATERIALIZED VIEW weather_v AS SELECTcity, temp_lo, temp_hi FROM weather ORDER BY city;CREATE MATERIALIZED VIEW

SELECT
    SUBSTR(model, 1, STRPOS(model, ';')-1) AS model_clean
FROM table1
;

SELECT age(to_date('10-22-2003','MM-DD-YYYY'),to_date('07-06-2002','MM-DD-YYYY');

select
   deptno,
   wm_concat(distinct ename) –VIOLAZ
from
   emp
group by
   deptno;
  
DECLARE c1 CURSOR WITH HOLD FOR SELECT * FROM customers; –VIOLAZ Fetch from cursos with select * statement

SELECT CASE WHEN CAST('99999' AS NUMERIC(18,0)) between -32678 AND 32767
            THEN CAST('99999' AS smallint)
END –VIOLAZ CASE Without ELSE
	   
  
CREATE SEQUENCE sequence1 as integer
START WITH 1 
increment by 1
minvalue 1 
maxvalue 300 
no cycle;

SQLStmt:="SELECT ename FROM emp"
 SQL EXECUTE(SQLStmt;[Employee]Name)
 SQL LOAD RECORD(SQL all records);
 
 SQL LOGIN("mysql";"root";"")
 SQLStmt:="SELECT alpha_field FROM app_testTable"
 START TRANSACTION
 SQL EXECUTE(SQLStmt;[Table 2]Field1)
 While(Not(SQL End selection))
    SQL LOAD RECORD
    ... //Place the data validation code here
 End while
 VALIDATE TRANSACTION //Validation of the transaction
 
 SQL LOGIN("IP:192.168.18.15:19812";"user";"password";*)
 If(OK=1)
  // Starting from here all SQL requests are made on the remote database
    C_TEXT($LastName_value) // 4D variable used in the search statement
    ARRAY TEXT($a_LastName;0) // Temporary storage of remote values for LastName
    ARRAY TEXT($a_FirstName;0) // Temporary storage of remote values for FirstName
    C_BOOLEAN($UseSQL) //Choice of means for local storage of data from the remote database
  // (demo only)
 
    $LastName_value:="Smith" // Initialization of 4D variable
 
  // Associate the 4D $LastName_value variable with the first "?" in the SQL request
    SQL SET PARAMETER($LastName_value;SQL param in)
 
  // From the remote PERSONS table, retrieve the values of the LastName and FirstName fields
  // where "LastName = Smith" and store them in the $a_LastName and $a_FirstName arrays
    SQL EXECUTE("SELECT LastName, FirstName FROM PERSONS WHERE LastName = ?";$a_LastName;$a_FirstName)
    If(Not(SQL End selection)) // If at least one record is found
 
       SQL LOAD RECORD(SQL all records) // Load all the records
 
       $UseSQL:=True // Chooses the way to integrate the data (demo only)
 
       If($UseSQL) // Use SQL requests
          SQL LOGOUT // Log out from the remote database
          SQL LOGIN(SQL_INTERNAL;"user";"password") // Log in to the local database
  // Starting from here all SQL requests are made on the local database
  // Save the $a_LastName and $a_FirstName arrays in the local PERSONS table
          SQL EXECUTE("INSERT INTO PERSONS(LastName, FirstName) VALUES (:$a_LastName, :$a_FirstName);")
 
       Else // Using 4D commands
          For($i;1;Size of array($a_LastName))
             CREATE RECORD([PERSONS])
             [PERSONS]LastName:=$a_LastName{$i}
             [PERSONS]FirstName:=$a_FirstName{$i}
             SAVE RECORD([PERSONS])
          End for
       End if
    End if
    SQL LOGOUT // Close the connection
 End if

