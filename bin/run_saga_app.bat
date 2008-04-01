@echo off

IF "%SAGA_LOCATION%X"=="X" set SAGA_LOCATION=%~dp0..

set SAGA_CLASSPATH=

FOR %%i IN ("%SAGA_LOCATION%\lib\*.jar") DO CALL "%SAGA_LOCATION%\bin\AddToClassPath.bat" %%i

java -cp "%SAGA_CLASSPATH%";"%CLASSPATH%" -Dlog4j.configuration=file:"%SAGA_LOCATION%"\log4j.properties %*
