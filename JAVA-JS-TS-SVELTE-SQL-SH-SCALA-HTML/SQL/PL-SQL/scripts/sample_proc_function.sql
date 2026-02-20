CREATE OR REPLACE PROCEDURE P1 (PN$Seconds IN PLS_INTEGER)
Is
  n  PLS_INTEGER := 0 ;
  n2 PLS_INTEGER := 0 ;
BEGIN
  n2 := To_Char(SYSDATE,'SSSSS');
  LOOP
    n := To_Char(SYSDATE,'SSSSS');
    EXIT WHEN n > (n2 + PN$Seconds )
         OR   n < n2;
  END LOOP;
END P1;
/

CREATE OR REPLACE FUNCTION F1 (PN$Seconds IN PLS_INTEGER)
Return Varchar2
Is
  n  PLS_INTEGER := 0 ;
  n2 PLS_INTEGER := 0 ;
BEGIN
  n2 := To_Char(SYSDATE,'SSSSS');
  LOOP
    n := To_Char(SYSDATE,'SSSSS');
    EXIT WHEN n > (n2 + PN$Seconds )
         OR   n < n2;
  END LOOP;
  Return PN$Seconds ;
END F1;
/

