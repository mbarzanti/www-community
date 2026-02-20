###########################################################################################
# Shell script che lancia tutta la catena di comandi necessaria per generare
# la parte dimensionale del repository metadati a partire dalla parte normalizzata.
# Lo script richiama nel giusto ordine i vari step di generazione del modello dimensionale
#
# Autore: A. Vincenzi
# Data Creazione: 8/9/2008
# 
###########################################################################################
echo '======= Inizio del processo ETL di caricamento del modello dimensionale metadati'
cat /dev/null >  etl_log.txt
cat /dev/null >  etl_error_log.txt
###########################################################################################
# Controllo numero di parametri
if [ "$#" -lt 1 ]; then
	echo 'Inserire il parametro "numero step iniziale" (numerico) '
	exit
fi
###########################################################################################
# Altra operazione di staging
if [ "$1" -lt 1 ]; then
	echo 'Step 1: dw_estrai_db_param.php'
fi
###########################################################################################
# Carica staging varie
if [ "$1" -lt 2 ]; then
	echo 'Step 2: dw_carica_staging_impact.php'
fi
###########################################################################################
#  Carica dimensioni
if [ "$1" -lt 3 ]; then
	echo 'Step 3: dw_carica_dimensioni.php'
fi

###########################################################################################
# Carica staging fatti
if [ "$1" -lt 4 ]; then
	echo 'Step 4: dw_carica_fact_impact_staging.php'
fi
###########################################################################################
# Carica fatti
if [ "$1" -lt 5 ]; then
	echo 'Step 5: dw_carica_fact_impact.php'
fi
###########################################################################################
# Post-processing dimensioni
if [ "$1" -lt 6 ]; then
	echo 'Step 6: dw_post_processing_dimensioni.php'
fi
###########################################################################################
# Post-processing fatti
if [ "$1" -lt 7 ]; then
	echo 'Step 7: dw_post_processing_fatti.php'
fi
###########################################################################################
# Termine processi ETL
echo '======= Fine del processo ETL di caricamento del modello dimensionale metadati'
echo "Elaborazione terminata correttamente"
