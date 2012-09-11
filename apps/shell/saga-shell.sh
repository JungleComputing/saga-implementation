#!/bin/sh

base=$(dirname $0)

export CLASSPATH=$CLASSPATH:$base/lib/jline-0.9.94.jar:$base/lib/saga-shell.jar

if [ -z "$JAVA_SAGA_LOCATION" ]; then
    export JAVA_SAGA_LOCATION=${base}/../..
fi

$JAVA_SAGA_LOCATION/scripts/run-saga-app -Xmx512M \
-Dlog4j.configuration=file:${base}/log4j.properties \
  org.ogf.saga.apps.shell.SagaShell $@
