#!/usr/bin/ksh

. /var/www/.profile
mydate=`date '+%m%Y'`
logfile='/var/www/htdocs/PortaleDM/FlowsMonitoring/MntBatch/Log/loadMonitStg_'$mydate'.log'
/opt/AMP/php5/bin/php /var/www/htdocs/PortaleDM/FlowsMonitoring/MntBatch/loadMonitStg.php>>$logfile

