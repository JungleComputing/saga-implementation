#!/bin/sh

export JAVA_SAGA_LOCATION=$(dirname $0)/..
export GAT_LOCATION=$JAVA_SAGA_LOCATION/lib/adaptors/JavaGatAdaptor
export CLASSPATH=$GAT_LOCATION/'*'

${JAVA_SAGA_LOCATION}/bin/run-saga-app -Dgat.adaptor.path=$GAT_LOCATION/adaptors benchmarks.file.JavaGATFileBenchmark $@
