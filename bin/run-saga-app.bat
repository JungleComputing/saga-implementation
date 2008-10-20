@echo off

IF "%JAVA_SAGA_LOCATION%X"=="X" set JAVA_SAGA_LOCATION=%SAGA_LOCATION%

IF "%JAVA_SAGA_LOCATION%X"=="X" set JAVA_SAGA_LOCATION=%~dp0..

set SAGA_CLASSPATH=

FOR %%i IN ("%JAVA_SAGA_LOCATION%\lib\*.jar") DO CALL "%JAVA_SAGA_LOCATION%\bin\AppendToClassPath.bat" %%i

java -cp "%SAGA_CLASSPATH%";"%CLASSPATH%" -Dlog4j.configuration=file:"%JAVA_SAGA_LOCATION%"\log4j.properties -Dsaga.location="%JAVA_SAGA_LOCATION%" %*
