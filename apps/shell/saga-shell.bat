@echo on

if "%OS%"=="Windows_NT" @setlocal

rem if "%JAVA_SAGA_LOCATION%X"=="X" set JAVA_SAGA_LOCATION=%~dp0\saga-lib

set SAGA_ARGS=

:doneStart

java -cp "%CLASSPATH%;%JAVA_SAGA_LOCATION%;%JAVA_SAGA_LOCATION%\lib\*;%~dp0\lib\*" -Dlog4j.configuration="file%~dp0\log4j.properties" -Dsaga.location="%JAVA_SAGA_LOCATION%" -Xmx512M org.ogf.saga.apps.shell.SagaShell %SAGA_ARGS%

if "%OS%"=="Windows_NT" @endlocal
