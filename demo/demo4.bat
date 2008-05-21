@echo OFF

rem For now, javaGAT only has a local file adaptor for RandomAccessFile,
rem so as long as we only have a javaGAT adaptor for File, this only works
rem for local files.

..\bin\run_saga_app -Xmx256M demo.file.FileSize demo4.bat
