@ECHO OFF

IF "%JAVA_SAGA_LOCATION%X"=="X" set JAVA_SAGA_LOCATION=%SAGA_LOCATION%

IF "%JAVA_SAGA_LOCATION%X"=="X" set JAVA_SAGA_LOCATION=%~dp0..

java -DUID="%USERNAME%" -classpath "%JAVA_SAGA_LOCATION%\lib\adaptors\JavaGatAdaptor\adaptors\GlobusAdaptor\cog-jglobus.jar" org.globus.tools.ProxyInit %*
