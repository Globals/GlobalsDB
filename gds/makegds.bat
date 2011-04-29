@echo off
set ANT_HOME=c:\perforce\dev\latest\thirdparty\apache-ant-1.7.1
set ANT=%ANT_HOME%\bin\ant.bat
set TARGET=%1
call %ANT% -f gds.xml all
