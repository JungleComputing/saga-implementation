export OMIICLIENT="$HOME/OMIICLIENT"
export JAVA_SAGA_LOCATION=$(dirname $0)/..

[ -n "$OMII_CLIENT_HOME" ] && { OMIICLIENT="${OMII_CLIENT_HOME}" ; }
[ -d "${OMIICLIENT}/lib" ] || { echo "OMII-Client is not found in the specified location (${OMIICLIENT}). Please specify the base directory of the OMII-Client with the OMII_CLIENT_HOME environment variable." ; exit 1 ; }

GRIDSAM_HOME="${OMIICLIENT}/gridsam"

ENDORSED="-Djava.endorsed.dirs=${OMIICLIENT}/endorsed"

# Jar-files from library.
SAGA_CLASSPATH=""
add_to_saga_classpath () {
    JARFILES=`cd "$1" && ls *.jar 2>/dev/null`
    for i in ${JARFILES} ; do
	if [ -z "$SAGA_CLASSPATH" ] ; then
	    SAGA_CLASSPATH="$1/$i"
	else
	    SAGA_CLASSPATH="$SAGA_CLASSPATH:$1/$i"
	fi
    done
}

add_to_saga_classpath $JAVA_SAGA_LOCATION/lib

exec java "${ENDORSED}" -classpath ".:${SAGA_CLASSPATH}:${GRIDSAM_HOME}/lib/gridsam-client.jar:${OMIICLIENT}/conf" \
    -Dlog4j.configuration="file:${JAVA_SAGA_LOCATION}/log4j.properties" \
    -Daxis.ClientConfigFile="${OMIICLIENT}/conf/client-config.wsdd" \
    -Dgrid.config.dir="${OMIICLIENT}/conf" \
    benchmarks.job.GridsamJobBenchmark $@

