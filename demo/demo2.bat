@echo OFF

..\scripts\run-saga-app -Xmx256M demo.namespace.ListTest ftp://ftp.cs.vu.nl/pub/ceriel "*" "*.gz" "{L,M}*" "*tar*"
