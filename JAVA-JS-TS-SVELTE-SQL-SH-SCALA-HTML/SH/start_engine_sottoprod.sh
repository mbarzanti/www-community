#!/bin/bash
cd /var/www/html/PortaleDM/GestoreProdotti/engines/
pid_qe=`ps auxww | grep main_sottoprodotti.php | egrep -v grep | awk '{print $2}'`
if [ "$pid_qe" != "" ]
then
   echo "Query Sottoprodotti già attivo"
else
   echo "Query Sottoprodotti attivata"
   nohup php main_sottoprodotti.php >> SottoProdotti_Creazione.log &
fi