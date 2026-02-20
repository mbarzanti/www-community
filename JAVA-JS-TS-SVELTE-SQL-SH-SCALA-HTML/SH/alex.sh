/oracle/app/oracle/product/11.2.0/client_1/bin/sqlplus -s ora_dm/mdaro@//10.193.0.220/msip.posteitaliane.it <<EOF>/var/www/html/PortaleDM/DirectMailing/engines/liste/lista_match_607_108.txt
set head off
set pagesize 0
set feed off
set verify off
set line 1000

select trim(a.id_cliente_bic)
from dm_indirizzo_etichetta a
where a.id_preventivo=455;
exit
EOF

