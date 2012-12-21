#! /bin/sh
#
# See contrib/init.resin for /etc/rc.d/init.d startup script
#
# resin.sh can be called like apachectl
#
# resin.sh         -- execs resin in the foreground
# resin.sh start   -- starts resin in the background
# resin.sh stop    -- stops resin
# resin.sh restart -- restarts resin
#
# resin.sh will return a status code if the wrapper detects an error, but
# some errors, like bind exceptions or Java errors, are not detected.
#
# To install, you'll need to configure JAVA_HOME and RESIN_HOME and
# copy contrib/init.resin to /etc/rc.d/init.d/resin.  Then
# use "unix# /sbin/chkconfig resin on"

if test -n "${JAVA_HOME}"; then
  if test -z "${JAVA_EXE}"; then
    JAVA_EXE=$JAVA_HOME/bin/java
  fi
fi  

JAVA_HOME=/application/jdk1.6.0_10
export JAVA_HOME
RESIN_HOME=$HOME/run/resin
export RESIN_HOME

# 3600000 == 60 * 60000 == 1H
# 180000  ==  3 * 60000 == 3M
# -Xss the stack size for each thread 
# -Xms	initial java heap size
# -Xmx	maximum java heap size
# -Xmn	the size of the heap for the young generation

JAVA_OPTS="-server -Xmn125M -Xms768M -Xmx768M -Djava.rmi.server.logCalls=false -Dsun.rmi.transport.tcp.maxConnectionThreads=250 -Dsun.rmi.transport.tcp.threadKeepAliveTime=60000 -Dsun.rmi.transport.tcp.readTimeout=180000 -Dsun.rmi.transport.tcp.logLevel=WARNING -Xloggc:${RESIN_HOME}/logs/gc.log"

#
# trace script and simlinks to find the wrapper
#
if test -z "${RESIN_HOME}"; then
  script=`/bin/ls -l $0 | awk '{ print $NF; }'`

  while test -h "$script"
  do
    script=`/bin/ls -l $script | awk '{ print $NF; }'`
  done

  bin=`dirname $script`
  RESIN_HOME="$bin/.."
fi  

#exec java -Djava.rmi.server.hostname=$HOSTNAME -jar ${RESIN_HOME}/lib/resin.jar $*
exec java ${JAVA_OPTS} -jar ${RESIN_HOME}/lib/resin.jar $*
