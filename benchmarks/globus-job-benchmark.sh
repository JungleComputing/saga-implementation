#!/bin/sh

export JAVA_SAGA_LOCATION=$(dirname $0)/..
export GAT_LOCATION=$JAVA_SAGA_LOCATION/lib/adaptors/JavaGatAdaptor

CLASSPATH=$GAT_LOCATION/adaptors/shared/'*':$GAT_ADAPTOR_LOCATION/'*':$GAT_LOCATION/adaptors/GlobusAdaptor/'*' ${JAVA_SAGA_LOCATION}/scripts/run-saga-app benchmarks.job.GlobusJobBenchmark $@
