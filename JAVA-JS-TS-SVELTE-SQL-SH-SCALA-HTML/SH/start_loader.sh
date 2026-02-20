#!/usr/bin/ksh

ORACLE_HOME=/oracle/app/oracle/product/11.2.0/client_1; export ORACLE_HOME

pid_qe=`ps auxww | grep main_loader.php | egrep -v grep | awk '{print $2}'`
if [ "$pid_qe" != "" ]
then
   echo "Loader gia' attivo"
else
   nohup php /var/www/html/PortaleDM/portafogliazione/loader/main_loader.php >> /var/www/html/PortaleDM/portafogliazione/loader/main_loader.log &
fi

