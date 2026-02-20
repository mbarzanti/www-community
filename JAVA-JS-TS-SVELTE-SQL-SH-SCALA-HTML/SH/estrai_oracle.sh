/oracle/app/oracle/product/11.2.0/client_1/bin/sqlplus -s ora_dm/mdaro@//10.193.0.220:1555/msip.posteitaliane.it <<EOF>/var/www/html/PortaleDM/DirectMailing/engines/tmprepo/export_data_1609_1352.txt
set head off
set pagesize 0
set feed off
set verify off
set line 1000

select nvl(t.gp_id_civico,0) || '|' || nvl(t.gp_id_toponimo,0) || '|' || trim(to_char(t.distanza, '99999999.99'))
from dm_preventivo_lista_indirizzi t
where t.id_preventivo=1609
and tipo_lista_norm='P'
and t.stato_indirizzo='NORMALIZZATO'
and (nvl(t.gp_id_civico, 0) <> 0 or nvl(t.gp_id_toponimo, 0) <> 0);
exit
EOF

