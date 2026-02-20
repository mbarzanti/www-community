#!/usr/bin/ksh
. /var/www/.profile

LIBPATH="/opt/AMP/apache/lib:$LIBPATH"
export LIBPATH
#
LDR_CNTRL="MAXDATA=0x80000000" ; export LDR_CNTRL ; AIXTHREAD_SCOPE=S ; export AIXTHREAD_SCOPE ; AIXTHREAD_MUTEX_DEBUG=OFF ; export 
AIXTHREAD_MUTEX_DEBUG ; AIXTHREAD_RWLOCK_DEBUG=OFF ; export AIXTHREAD_RWLOCK_DEBUG ; AIXTHREAD_COND_DEBUG=OFF ; export AIXTHREAD_CON
D_DEBUG ; SPINLOOPTIME=1000 ; export SPINLOOPTIME ; YIELDLOOPTIME=8 ; export YIELDLOOPTIME ; MALLOCMULTIHEAP=considersize,heaps:8 ; 
export MALLOCMULTIHEAP
unset LD_LIBRARY_PATH LIBPATH SHLIB_PATH
ORACLE_HOME=/opt/AMP/libs/oracle
export ORACLE_HOME
export LIBPATH=$LIBPATH:/opt/AMP/libs/teradata/lib
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:/opt/AMP/libs/iodbc/lib
export ODBCINI=/opt/AMP/libs/iodbc/odbc.ini

mydate=`date '+%m%Y'`
logfile='/var/www/htdocs/PortaleDM/BusinessMonitoring/MntBatch/monitbuskpi_'$mydate'.log'
/opt/AMP/php5/bin/php /var/www/htdocs/PortaleDM/BusinessMonitoring/MntBatch/monitbuskpi.php>>$logfile

