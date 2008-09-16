@ECHO OFF

java -DUID="%USERNAME%" -classpath "%SAGA_LOCATION%\lib\adaptors\JavaGatAdaptor\adaptors\GlobusAdaptor\cog-jglobus.jar" org.globus.tools.ProxyDestroy %*
