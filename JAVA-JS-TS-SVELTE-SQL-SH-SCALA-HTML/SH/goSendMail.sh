#!/usr/bin/ksh

. /var/www/.profile
mydate=`date '+%m%Y'`
logfile='/var/www/htdocs/PortaleDM/BusinessMonitoring/MntBatch/sendMntWarning_'$mydate'.log'
/opt/AMP/php5/bin/php /var/www/htdocs/PortaleDM/BusinessMonitoring/MntBatch/sendMntWarning.php>>$logfile

