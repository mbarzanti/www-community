#!/bin/bash

source /home/pfa/.bash_profile

pid_qe=`ps auxww | grep main_qe.php | egrep -v grep | awk '{print $2}'`
if [ "$pid_qe" != "" ]
then
   echo "Query Engine già attivo"
else
   nohup php /var/www/html/PortaleDM/DirectMailing/engines/main_qe.php >> /var/www/html/PortaleDM/DirectMailing/engines/main_qe.log &
fi

pid_ae=`ps auxww | grep main_ae.php | egrep -v grep | awk '{print $2}'`
if [ "$pid_ae" != "" ]
then
   echo "Address Engine gia' attivo"
else
   nohup php /var/www/html/PortaleDM/DirectMailing/engines/main_ae.php >> /var/www/html/PortaleDM/DirectMailing/engines/main_ae.log &
fi

pid_se=`ps auxww | grep main_se.php | egrep -v grep | awk '{print $2}'`
if [ "$pid_se" != "" ]
then
   echo "Send Engine già attivo"
else
   nohup php /var/www/html/PortaleDM/DirectMailing/engines/main_se.php >> /var/www/html/PortaleDM/DirectMailing/engines/main_se.log &
fi

