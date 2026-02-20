#!/usr/bin/ksh

. /var/www/.profile
LIBPATH="/opt/AMP/apache/lib:$LIBPATH"
export LIBPATH
export LIBPATH=$LIBPATH:/opt/AMP/libs/teradata/lib
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/opt/AMP/libs/iodbc/lib
export ODBCINI=/opt/AMP/libs/iodbc/odbc.ini

mydate=`date '+%m%Y'`
logfile='/var/www/htdocs/PortaleDM/FlowsMonitoring/MntBatch/Log/loadMonitCaricamenti_'$mydate'.log'
/opt/AMP/php5/bin/php /var/www/htdocs/PortaleDM/FlowsMonitoring/MntBatch/loadMonitCaricamenti.php>>$logfile

