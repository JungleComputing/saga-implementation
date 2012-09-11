#!/bin/sh

export JAVA_SAGA_LOCATION=$(dirname $0)/..
export GAT_LOCATION=$JAVA_SAGA_LOCATION/lib/adaptors/JavaGatAdaptor
export CLASSPATH=$GAT_LOCATION/adaptors/SshTrileadAdaptor/'*'

${JAVA_SAGA_LOCATION}/scripts/run-saga-app benchmarks.namespace.SshNSBenchmark $@
