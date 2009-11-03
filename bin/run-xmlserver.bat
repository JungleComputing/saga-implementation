@ECHO OFF

IF "%JAVA_SAGA_LOCATION%X"=="X" set JAVA_SAGA_LOCATION=%SAGA_LOCATION%

IF "%JAVA_SAGA_LOCATION%X"=="X" set JAVA_SAGA_LOCATION=%~dp0..

set SAGA_CLASSPATH=

FOR %%i IN ("%JAVA_SAGA_LOCATION%\lib\adaptors\XMLRPCAdaptor\*.jar") DO CALL "%JAVA_SAGA_LOCATION%\bin\AppendToClassPath.bat" %%i

java -DUID="%USERNAME%" -classpath "%JAVA_SAGA_LOCATION%\lib\saga-demo.jar;%SAGA_CLASSPATH%" demo.rpc.server.Server %*
