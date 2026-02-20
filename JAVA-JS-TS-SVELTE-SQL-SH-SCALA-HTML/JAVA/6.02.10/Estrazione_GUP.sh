echo =====================================================
echo inizio esecuzione script `/usr/bin/date +"%Y/%m/%d %H:%M:%S"`
echo =====================================================

HOME=/data/DWH/BIC/BBP/GUP
cd $HOME

TDPNAME=$1
USERNAME=$2
PASSWORD=$3

CURRENT_DATE=`/usr/bin/date +%Y%m%d`
OUTPATH=/mnt_ftp1/Nautilus_3/output/GUP/

#-------------------  Remove old files -------------------------------#

rm -f $OUTPATH*.txt


#-------------------  GUP ----------------------------------#

OUT_Anag_UP_GUP=NTL3_Anag_UP_GUP_$CURRENT_DATE\.txt
sh ./Estr_GUP_NTL3_Anagrafica_UP.fexp $TDPNAME $USERNAME $PASSWORD $OUTPATH $OUT_Anag_UP_GUP
ret=$?
echo export Estr_GUP_NTL3_Anagrafica_UP : ret = $ret

OUT_Dot_Sic_GUP=NTL3_Dotazioni_Sicurezza_GUP_$CURRENT_DATE\.txt
sh ./Estr_GUP_NTL3_Dot_Sicurezza.fexp $TDPNAME $USERNAME $PASSWORD $OUTPATH $OUT_Dot_Sic_GUP
ret=$?
echo export Estr_GUP_NTL3_Dot_Sicurezza.fexp : ret = $ret

OUT_ATM_GUP=NTL3_ATM_GUP_$CURRENT_DATE\.txt
sh ./Estr_GUP_NTL3_ATM.fexp $TDPNAME $USERNAME $PASSWORD $OUTPATH $OUT_ATM_GUP
ret=$?
echo export Estr_GUP_NTL3_ATM : ret = $ret

OUT_Orari_UP_GUP=NTL3_Orari_UP_Mese_$CURRENT_DATE\.txt
sh ./Estr_GUP_NTL3_Orari_UP.fexp $TDPNAME $USERNAME $PASSWORD $OUTPATH $OUT_Orari_UP_GUP
ret=$?
echo export Estr_GUP_NTL3_Orari_UP : ret = $ret

OUT_Classi_Giac_GUP=NTL3_Classi_Giacenza_GUP_$CURRENT_DATE\.txt
sh ./Estr_GUP_NTL3_Classi_Giac.fexp $TDPNAME $USERNAME $PASSWORD $OUTPATH $OUT_Classi_Giac_GUP
ret=$?
echo export Estr_GUP_NTL3_Classi_Giac : ret = $ret

OUT_Giacenza_UP_GUP=NTL3_Giacenza_UP_GUP_$CURRENT_DATE\.txt
sh ./Estr_GUP_NTL3_Giacenza_UP.fexp $TDPNAME $USERNAME $PASSWORD $OUTPATH $OUT_Giacenza_UP_GUP
ret=$?
echo export Estr_GUP_NTL3_Giacenza_UP : ret = $ret

OUT_GUP_UPM=GUP_UPM_$CURRENT_DATE\.txt
sh ./Estr_GUP_UPM.fexp $TDPNAME $USERNAME $PASSWORD $OUTPATH $OUT_GUP_UPM
ret=$?
echo export Estr_GUP_UPM : ret = $ret


echo
echo
echo

#########################   FINE   ###############################################

echo
echo
echo

echo CURRENT_DATE=$CURRENT_DATE
echo OUTPATH=$OUTPATH
echo
echo ls -l $OUTPATH
ls -l $OUTPATH
echo

echo

## da rimuovere alla messa in esercizio
## ====================================

#exit

## ====================================


echo esecuzione FTP \(CFT\)
echo ====================
echo

cd $OUTPATH
OUTFILE1=`ls NTL3_Anag_UP_GUP_*.txt`
OUTFILE2=`ls NTL3_Dotazioni_Sicurezza_GUP_*.txt`
OUTFILE3=`ls NTL3_ATM_GUP_*.txt`
OUTFILE4=`ls NTL3_Orari_UP_Mese_*.txt`
OUTFILE5=`ls NTL3_Classi_Giacenza_GUP_*.txt`
OUTFILE6=`ls NTL3_Giacenza_UP_GUP_*.txt`
OUTFILE7=`ls GUP_UPM_*.txt`

echo OUTPATH=$OUTPATH
echo OUTFILE1=$OUTFILE1
echo OUTFILE2=$OUTFILE2
echo OUTFILE3=$OUTFILE3
echo OUTFILE4=$OUTFILE4
echo OUTFILE5=$OUTFILE5
echo OUTFILE6=$OUTFILE6
echo OUTFILE7=$OUTFILE7
echo

touch $OUTPATH/semaforo_$OUTFILE1
touch $OUTPATH/semaforo_$OUTFILE2
touch $OUTPATH/semaforo_$OUTFILE3
touch $OUTPATH/semaforo_$OUTFILE4
touch $OUTPATH/semaforo_$OUTFILE5
touch $OUTPATH/semaforo_$OUTFILE6
touch $OUTPATH/semaforo_$OUTFILE7

echo creato semaforo per Axway
echo ======================

echo =====================================================
echo fine esecuzione script `/usr/bin/date +"%Y/%m/%d %H:%M:%S"`
echo =====================================================
echo

