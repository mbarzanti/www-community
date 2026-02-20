#!/usr/bin/ksh

. /var/www/.profile
mydate=`date '+%m%Y'`
logfile='/var/www/htdocs/PortaleDM/FlowsMonitoring/MntBatch/Log/loadMonitEdw_'$mydate'.log'
/opt/AMP/php5/bin/php /var/www/htdocs/PortaleDM/FlowsMonitoring/MntBatch/loadMonitEdw_SP.php>>$logfile
/opt/AMP/php5/bin/php /var/www/htdocs/PortaleDM/FlowsMonitoring/MntBatch/matchMonitFS_DR.php>>$logfile

