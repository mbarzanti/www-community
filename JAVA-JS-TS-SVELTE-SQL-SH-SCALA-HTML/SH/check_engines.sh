#!/bin/bash

echo "*************************  Monitoraggio Query Engine  **************************"
tail -10 main_qe.log
echo "\n\n\n"
echo "*************************  Monitoraggio Address Engine  **************************"
tail -10 main_ae.log
echo "\n\n\n"
echo "*************************  Monitoraggio Send Engine  **************************"
tail -10 main_se.log

