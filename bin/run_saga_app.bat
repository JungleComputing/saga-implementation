@echo off

IF "%SAGA_LOCATION%X"=="X" set SAGA_LOCATION=.

:: ---- do not touch anything below this line ----

set SAGA_LIB_LOCATION=%SAGA_LOCATION%\lib
set GAT_ADAPTOR_LOCATION=%SAGA_LIB_LOCATION%\adaptors\JavaGatAdaptor\adaptors

set SAGA_CLASSPATH=

FOR %%i IN ("%SAGA_LIB_LOCATION%\*.jar") DO CALL "%SAGA_LOCATION%\bin\AddToClassPath.bat" %%i

java -cp "%CLASSPATH%";"%SAGA_CLASSPATH%" -Dlog4j.configuration=file:"%SAGA_LOCATION%"\log4j.properties -Dgat.adaptor.path="%GAT_ADAPTOR_LOCATION%" %*
