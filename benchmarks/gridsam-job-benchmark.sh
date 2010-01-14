#!/bin/sh

export JAVA_SAGA_LOCATION=$(dirname $0)/..
export OMII=/home/ceriel/OMII


CLASSPATH=.:$JAVA_SAGA_LOCATION/lib/adaptors/shared/'*':$JAVA_SAGA_LOCATION/lib/'*':$JAVA_SAGA_LOCATION/lib/adaptors/GridsamAdaptor/'*' ${JAVA_SAGA_LOCATION}/bin/run-saga-app -Djava.endorsed.dirs=$JAVA_SAGA_LOCATION/lib/adaptors/GridsamAdaptor/endorsed benchmarks.job.GridsamJobBenchmark $@
