#!/bin/sh

base=$(dirname $0)
export CLASSPATH=$CLASSPATH:$base/lib/'*'

if [ -z "$JAVA_SAGA_LOCATION" ]; then
    export JAVA_SAGA_LOCATION=${base}/../..
fi

$JAVA_SAGA_LOCATION/bin/run-saga-app -Xmx512M \
-Dlog4j.configuration=file:${base}/log4j.properties \
  org.ogf.saga.apps.shell.SagaShell $@
