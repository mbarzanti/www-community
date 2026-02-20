#!/usr/bin/ksh

. /var/www/.profile
nohup /opt/AMP/php5/bin/php /var/www/htdocs/PortaleDM/MSGServer/msg_server.php >> /var/www/htdocs/PortaleDM/MSGServer/lancia_MSGServer.log &

