#!/bin/bash

source /home/pfa/.bash_profile

pid_qe=`ps auxww | grep main_qe.php | egrep -v grep | awk '{print $2}'`
if [ "$pid_qe" != "" ]
then
   kill -9 $pid_qe
fi

pid_ae=`ps auxww | grep main_ae.php | egrep -v grep | awk '{print $2}'`
if [ "$pid_ae" != "" ]
then
   kill -9 $pid_ae
fi

pid_se=`ps auxww | grep main_se.php | egrep -v grep | awk '{print $2}'`
if [ "$pid_se" != "" ]
then
   kill -9 $pid_se
fi


