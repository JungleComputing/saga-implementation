#!/bin/sh

export JAVA_SAGA_LOCATION=$(dirname $0)/..

${JAVA_SAGA_LOCATION}/bin/run-saga-app benchmarks.job.SagaJobBenchmark $@
