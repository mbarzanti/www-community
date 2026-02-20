#!/bin/bash
pid_qe=`ps auxww | grep main_sottoprodotti.php | egrep -v grep | awk '{print $2}'`
if [ "$pid_qe" != "" ]
then
   kill -9 $pid_qe
fi