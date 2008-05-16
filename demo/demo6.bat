#!/bin/sh

rem This runs the SAGA gridsam adaptor (not the JavaGAT one).
rem For this demo, you first need to run an ftp server on port 12345.
rem For instance:
rem     $HOME/OMIICLIENT/gridsam/bin/gridsam-ftp-server -d $HOME/data -p 12345

..\bin\run_saga_app -DJobService.adaptor.name=gridsam demo.job.TestJob1
