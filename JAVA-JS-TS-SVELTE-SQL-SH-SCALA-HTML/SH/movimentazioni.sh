/oracle/app/oracle/product/ora10g/bin/sqlplus -s ora_pfa/ora_pfa@DMS <<EOF
set head off
set pagesize 0
set feed off
set verify off
set linesize 10000
set wrap off
SET TRIMOUT ON

select business_partner
from pfa_sau_clienti_movimentazioni
exit;
