#!/bin/sh

export JAVA_SAGA_LOCATION=$(dirname $0)/..

${JAVA_SAGA_LOCATION}/scripts/run-saga-app benchmarks.file.SagaFileStreamBenchmark $@
