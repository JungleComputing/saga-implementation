export OMIICLIENT="$HOME/OMIICLIENT"
export JAVA_SAGA_LOCATION=$(dirname $0)/..

[ -n "$OMII_CLIENT_HOME" ] && { OMIICLIENT="${OMII_CLIENT_HOME}" ; }
[ -d "${OMIICLIENT}/lib" ] || { echo "OMII-Client is not found in the specified location (${OMIICLIENT}). Please specify the base directory of the OMII-Client with the OMII_CLIENT_HOME environment variable." ; exit 1 ; }

GRIDSAM_HOME="${OMIICLIENT}/gridsam"

ENDORSED="-Djava.endorsed.dirs=${OMIICLIENT}/endorsed"

exec java "${ENDORSED}" -classpath ".:${JAVA_SAGA_LOCATION}/lib/"'*'":${GRIDSAM_HOME}/lib/gridsam-client.jar:${OMIICLIENT}/conf" \
    -Dlog4j.configuration="file:${JAVA_SAGA_LOCATION}/log4j.properties" \
    -Daxis.ClientConfigFile="${OMIICLIENT}/conf/client-config.wsdd" \
    -Dgrid.config.dir="${OMIICLIENT}/conf" \
    benchmarks.job.GridsamJobBenchmark $@

