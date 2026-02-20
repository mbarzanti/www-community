#!/usr/bin/ksh

pid_qe=`ps auxww | grep main_loader.php | egrep -v grep | awk '{print $2}'`
if [ "$pid_qe" != "" ]
then
   kill -9 $pid_qe
else
   echo "Loader non attivo"
fi

