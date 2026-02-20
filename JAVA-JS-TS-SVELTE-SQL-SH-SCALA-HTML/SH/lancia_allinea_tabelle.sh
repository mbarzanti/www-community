#!/bin/bash

source /home/pfa/.bash_profile

cd /var/www/htdocs/PortaleDM/DirectMailing/engines
nohup php /var/www/htdocs/PortaleDM/DirectMailing/engines/allinea_tabelle.php >> /var/www/htdocs/PortaleDM/DirectMailing/engines/lancia_allinea_tabelle.log &

