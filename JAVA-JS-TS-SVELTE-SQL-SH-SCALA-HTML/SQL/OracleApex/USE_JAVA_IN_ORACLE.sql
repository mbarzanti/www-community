CREATE OR REPLACE AND COMPILE JAVA SOURCE NAMED "welcome"
AS
public class welcome {
 
    public static String msg() {
        return "Welcome to https://www.oracleplsqltr.com/";
    }
     
};

CREATE OR REPLACE FUNCTION f_java
RETURN VARCHAR2 
AS
LANGUAGE JAVA NAME 'welcome.msg() return java.lang.String';

declare
    v_return varchar2(500);
begin
    v_return := f_java;
    dbms_output.put_line(v_return);
end;
// You can check the created Java objects from USER_OBJECTS dictionary view
SELECT object_name, object_type, status FROM user_objects WHERE object_type like '%JAVA%';

// Running OS command via Java
BEGIN
  
DBMS_JAVA.GRANT_PERMISSION(
                           'DB_USER',
                           'SYS:java.io.FilePermission',
                           '<<ALL FILES>>',
                           'read,write,delete,execute'
                           );
  
END;
CREATE OR REPLACE AND COMPILE JAVA SOURCE NAMED "jos"
AS
import java.io.*;
 
public class jos {
     
    public static String run_os_cmd(String p_cmd) {
        Process process;
        String ln = "";
         
        try {
             
            process = Runtime.getRuntime().exec(p_cmd);
            ln = printResults(process);
             
        } catch (IOException e) {
            e.printStackTrace();
        }
         
        return ln;
             
    }
     
    public static String printResults(Process process) throws IOException {
         
        BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
        String line = "";
        String line2 = "";
         
        while ((line = reader.readLine()) != null) {
            //System.out.println(line);
            line2 += line + "\n";
        }
         
        return line2;
    }   
     
};
CREATE OR REPLACE FUNCTION f_execute_cmd(p_cmd varchar2)
RETURN VARCHAR2 
AS
LANGUAGE JAVA NAME 'jos.run_os_cmd(java.lang.String) return java.lang.String';
declare
    v_return varchar2(4000);
begin
    v_return := f_execute_cmd('pwd');
    dbms_output.put_line(v_return);
end;

DROP JAVA SOURCE "jcalc";
 
DROP PROCEDURE JAVA_TEST_CALC;
 
DROP PROCEDURE PLSQL_TEST_CALC;
CREATE OR REPLACE AND COMPILE JAVA SOURCE NAMED "jcalc"
AS
public class jcalc {
  
    public static void test_calc(int p_iteration) {
      
        int ind = 0;
        int iteration = 0;
        int innerloop = 0;
        double sum = 0.0;
        int array_length = p_iteration;
        double[] arr = new double[array_length];
          
        for (ind = 0; ind < array_length; ind++)
            arr[ind] = ind;
              
        for (innerloop = 0; innerloop < array_length; innerloop++)
            sum += arr[(iteration + innerloop) % array_length];
         
        System.out.println(Double.valueOf(sum).longValue());
          
        arr = null;
    }
};
CREATE OR REPLACE PROCEDURE JAVA_TEST_CALC(P_ITERATION NUMBER)
AS
LANGUAGE JAVA NAME 'jcalc.test_calc(int)';
CREATE OR REPLACE PROCEDURE PLSQL_TEST_CALC(P_ITERATION INTEGER)
IS
    v_element integer := 0;
    v_iteration integer := 0;
    v_innerloop integer := 0;
    v_total number := 0.0;
    v_array_length integer := p_iteration;
    type t_arr is table of number;
    arr t_arr := t_arr();
BEGIN
 
    /* UPDATED as stated in the comment  */
    --SELECT LEVEL BULK COLLECT INTO ARR FROM DUAL CONNECT BY LEVEL <= v_array_length;
     
    FOR I IN 1..v_array_length
    LOOP
        ARR.EXTEND;
        ARR(ARR.COUNT) := I;
    END LOOP;
     
    /* --------------------------------- */
     
    --Index of nested table array start with 1
    v_innerloop := 1;
       
    WHILE v_innerloop < v_array_length
    LOOP
        v_total := v_total + arr(mod((v_iteration + v_innerloop), v_array_length));
        v_innerloop := v_innerloop + 1;
    END LOOP;
       
    dbms_output.put_line(v_total);
       
    arr.delete();
       
END;
CALL dbms_java.set_output(2000);
SET SERVEROUTPUT ON;
declare
  
    V_START NUMBER;
      
    PROCEDURE SHOW_ELAPSED_TIME(P_NAME VARCHAR2)
    IS
    BEGIN
        DBMS_OUTPUT.PUT_LINE(P_NAME||' elapsed time: '||TO_CHAR((DBMS_UTILITY.GET_TIME - V_START)/100));
        V_START := DBMS_UTILITY.GET_TIME;
    END;
  
  
begin
  
    V_START := DBMS_UTILITY.GET_TIME;
      
    PLSQL_TEST_CALC(10000000);
      
    SHOW_ELAPSED_TIME('PLSQL');
      
    JAVA_TEST_CALC(10000000);
      
    SHOW_ELAPSED_TIME('Java');
  
end;

CREATE OR REPLACE AND COMPILE JAVA SOURCE NAMED "Operator"
AS
public class Operator
{
    ADDITION("+") {
	// VIOLAZ annotazione non permessa in Oracle
       @Override public double apply(double x1, double x2) {
        return x1 + x2;
       }
    },
    SUBTRACTION("-") {
	// VIOLAZ annotazione non permessa in Oracle
       @Override public double apply(double x1, double x2) {
        return x1 - x2;
       }
    };
}