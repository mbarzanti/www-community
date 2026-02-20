@ECHO OFF 
cls 
Echo compilation des librairies.... 
for %%f IN (*.pll) do ifcmp90 userid=%1 module=%%f batch=yes module_type=library compile_all=yes window_state=minimize 
ECHO compilation librairies terminée ---
Echo compilation des menus.... 
for %%f IN (*.mmb) do ifcmp90 userid=%1 module=%%f batch=yes module_type=menu compile_all=yes window_state=minimize 
ECHO compilation menus terminée ---
Echo compilation des formes.... 
for %%f IN (*.fmb) do ifcmp90 userid=%1 module=%%f batch=yes module_type=form compile_all=yes window_state=minimize 
ECHO compilation formes terminée ---
