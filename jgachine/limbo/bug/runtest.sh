#!/bin/sh
set -e
CLASSPATH="$CLASSPATH:."
ARGS="-classpath $CLASSPATH -Djava.system.class.loader=MyClassLoader"
#JVMS="java /usr/local/mozilla-plugins/java2/bin/java"
JVMS="java"
javac *.java
for JVM in $JVMS; do
	($JVM $ARGS Test && echo OK) || echo ERROR;
done
