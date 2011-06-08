@ECHO OFF

IF "%JAVA_SAGA_LOCATION%X"=="X" set JAVA_SAGA_LOCATION=%SAGA_LOCATION%

IF "%JAVA_SAGA_LOCATION%X"=="X" set JAVA_SAGA_LOCATION=%~dp0..

set SAGA_CLASSPATH=

FOR %%i IN ("%JAVA_SAGA_LOCATION%\lib\adaptors\JavaGatAdaptor\adaptors\shared\*.jar") DO CALL "%JAVA_SAGA_LOCATION%\bin\AppendToClassPath.bat" %%i

java -DUID="%USERNAME%" -classpath "%JAVA_SAGA_LOCATION%\lib\adaptors\JavaGatAdaptor\adaptors\GlobusAdaptor\GlobusAdaptor.jar;%SAGA_CLASSPATH%" org.globus.tools.ProxyInit %*
