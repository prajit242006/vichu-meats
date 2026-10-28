#!/bin/sh
# Apache Maven Wrapper launcher; wrapper JAR is bundled.
set -eu
BASE=$(CDPATH= cd -- "$(dirname -- "$0")" && pwd)
JAVA=java
if [ -n "${JAVA_HOME:-}" ]; then JAVA="$JAVA_HOME/bin/java"; fi
exec "$JAVA" ${MAVEN_OPTS:-} "-Dmaven.multiModuleProjectDirectory=$BASE" -classpath "$BASE/.mvn/wrapper/maven-wrapper.jar" org.apache.maven.wrapper.MavenWrapperMain "$@"
