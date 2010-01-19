#!/bin/sh

export JAVA_SAGA_LOCATION=$(dirname $0)/..
export GAT_LOCATION=$JAVA_SAGA_LOCATION/lib/adaptors/JavaGatAdaptor

CLASSPATH=$GAT_LOCATION/adaptors/shared/'*':$GAT_LOCATION/'*':$GAT_LOCATION/adaptors/GlobusAdaptor/'*' ${JAVA_SAGA_LOCATION}/bin/run-saga-app benchmarks.namespace.GlobusNSBenchmark $@
