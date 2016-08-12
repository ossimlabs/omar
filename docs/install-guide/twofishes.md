# Welcome to Twofishes

[Twofishes](http://twofishes.net) is not owned or packaged by us and the full installation instructions can be found at their location.  Twofishes location service is used by the [OMAR/O2 UI](omar-app.md)

For clarity we will repeat some of the instructions from an installation process we did for the CentOS 6 distribution:

* [Download server binary](https://s3.amazonaws.com/ossimlabs/dependencies/twofishes/server-assembly-0.84.9.jar) (version 0.84.9, 2015-03-10)
* [Download latest index](https://s3.amazonaws.com/ossimlabs/dependencies/twofishes/2015-03-05.zip) (updated 2015-03-05)

Create a directory location for running the twofishes service and then extract the index as a subdirectory to that location.  

```
mkdir twofishes
cd twofishes
wget http://twofishes.net/binaries/server-assembly-0.84.9.jar
wget http://twofishes.net/indexes/revgeo/2015-03-05.zip
unzip 2015-03-05.zip
```
The unzip command should create a directory with the name **2015-03-05-20-05-30.753698**

After extracting the latest and copying the server-assembly jar file you should be able to give the following command to the index directory

```
java -jar server-assembly-0.84.9.jar --port 8080 --hfile_basepath 2015-03-05-20-05-30.753698&
```

## Example Production Setup

For production installation we wrapped the twofishes with a shell that exports the JVM settings and give more memory to twofishes for caching the location database.  Within the same directory mentioned above add a **vi /opt/twofishes/twofishes.sh** with the contents

```
export TWOFISHES_PID=$1
export PORT=$2

if [ "$PORT" == "" ]; then
	export PORT=8080
fi

pushd `dirname $0` > /dev/null
export JAVA_HOME=/usr/lib/jvm/java-1.8.0/
export PATH=$JAVA_HOME/bin:$PATH
export LD_LIBRARY_PATH=$JAVA_HOME/lib:$LD_LIBRARY_PATH

export JAVA_OPTS="-server -Xms256m -Xmx1024m -Djava.awt.headless=false -XX:MaxPermSize=256m -XX:+CMSClassUnloadingEnabled -XX:+UseGCOverheadLimit"

if [ "$TWOFISHES_PID" != "" ]; then
  java -jar server-assembly-0.84.9.jar --port $PORT --hfile_basepath 2015-03-05-20-05-30.753698&
  sleep 1
  echo $! >$TWOFISHES_PID
else
  java -jar server-assembly-0.84.9.jar --port $PORT --hfile_basepath 2015-03-05-20-05-30.753698
fi
popd
exit 0
```
**Note**: this script takes two arguments **TWOFISHES_PID** for the pid file location to write the PID, and the **PORT** that you wish to run twofishes on.  In the script if a port is not specified it will come up on port 8080 and if no PID is supplied it will not run in the background.  Also note we have a JAVA_OPTS giving up to 1024m of memory.  If you have the resources you probably be better off increasing the number to 2 or 4 gigs.


## Example init.d for Twofishes

Edit the twofishes service file under /etc/init.d

```
sudo vi /etc/init.d/twofishes
```

Modify the contents for your system.  Here is an example setup on our twofishes server

```
#!/bin/bash
#
# twofishes      Starts and stops the twofishes.
#
#
### BEGIN INIT INFO
# chkconfig: 3 80 20
# Provides:Ingest twofishes service
# Required-Start: $network $syslog
# Required-Stop: $network $syslog
# Default-Start: 3
# Default-Stop: 0 1 2 6
# Should-Start: $network $syslog
# Should-Stop: $network $syslog
# Description: twofishes start, stop and restart script
# Short-Description: start, stop and restart ingestListener
### END INIT INFO

RETVAL=$?
PROG=twofishes
PID_DIR="/var/run/twofishes"
PRODUCT_PID_DIR="$PID_DIR"
PRODUCT_PID="$PID_DIR/twofishes.pid"
PRODUCT_LOG_DIR="/var/log/twofishes"
PRODUCT_ERROR_LOG=$PRODUCT_LOG_DIR/error_log
PRODUCT_USER="omar"
PRODUCT_GROUP="omar"
RUN_PROG_TEST="/opt/twofishes/twofishes.sh"
RUN_PROGRAM="$RUN_PROG_TEST $PRODUCT_PID 8080"

# Must be exported in order for catatlina to generate pid file
export PRODUCT_PID
start() {
   if [ -f "$RUN_PROG_TEST" ]; then
      # Make omar lock file dir and change ownership to omar user
      if [ ! -d "$PID_DIR" ]; then
            mkdir -p $PID_DIR
            chown $PRODUCT_USER:$PRODUCT_GROUP $PID_DIR
      fi
      # Make omar log directory and change ownership to omar user
      if [ ! -d "$PRODUCT_LOG_DIR" ]; then
            mkdir -p $PRODUCT_LOG_DIR
            chown $PRODUCT_USER:$PRODUCT_GROUP $PRODUCT_LOG_DIR
      fi
      # Check for running instance
      if [ -e "$PRODUCT_PID" ]; then
              read PID < $PRODUCT_PID
                   if checkpid $PID 2>&1; then
                       echo $"$PROG process is already running..."
                           return 1
                           echo_failure
                       else
                           echo "Lock file found but no $PROG process running for (pid $PID)"
                           echo "Removing old lock file..."
                     rm -rf $PRODUCT_PID
                   fi
        fi
         echo -ne $"Starting $PROG: "
echo $RUN_PROGRAM
            /bin/su - $PRODUCT_USER $RUN_PROGRAM >> $PRODUCT_ERROR_LOG 2>&1
            RETVAL="$?"
          if [ "$RETVAL" -eq 0 ]; then
               echo_success
                  echo -en "\n"
            else
                  echo_failure
                  echo -en "\n"
               exit 1
          fi
   else
      echo "$RUN_PROGRAM does not exist..."
      echo_failure
      echo -en "\n"
      exit 1
   fi
}

stop() {
    COUNT=0
    WAIT=10
    if [ -e "$PRODUCT_PID" ]; then
      read PID < $PRODUCT_PID
#echo ${PID}
        if checkpid $PID 2>&1; then
            echo -ne $"Stopping $PROG: "
         kill $PID >> $PRODUCT_ERROR_LOG 2>&1
         RETVAL="$?"
         sleep 2
#        if [ "$RETVAL" -eq 0 ]; then
#           echo_successss
#           echo -en "\n"
#        else
            while [ "$(ps -p $PID | grep -c $PID)" -eq "1"  -a  "$COUNT" -lt "$WAIT" ]; do
            echo -ne "\nWaiting on $PROG process to exit..."
            COUNT=`expr $COUNT + 1`
            sleep .3
#           echo $COUNT
            done

#              if [ "$COUNT" -gt "$WAIT" ]; then
               if [ "$(ps -p $PID | grep -c $PID)" == "1" ] ; then
                  echo -ne "\nCouldn't shutdown, forcing shutdown of $PROG process"
                  kill -9 $PID
               fi
               echo_success
               echo -en "\n"
#        fi
      fi
   else
      echo -ne $"Stopping $PROG: "
      echo_failure
      echo -en "\n"
   fi
}

status() {
  RETVAL="1"
    if [ -e "$PRODUCT_PID" ]; then
        read PID < $PRODUCT_PID
        if checkpid $PID 2>&1; then
            echo "$PROG (pid $PID) is running..."
            RETVAL="0"
        else
            echo "Lock file found but no process running for (pid $PID)"
        fi
    else
        PID="$(pgrep -u $PRODUCT_USER java)"
        if [ -n "$PID" ]; then
            echo "$PROG running (pid $PID) but no PID file exists"
            RETVAL="0"
        else
            echo "$PROG is stopped"
            RETVAL="1"
        fi
    fi
    return $RETVAL
}

# Check if pid is running
checkpid() {
   local PID
   for PID in $* ; do
      [ -d "/proc/$PID" ] && return 0
   done
   return 1
}

echo_success() {
   if [ -e "/etc/redhat-release" ]; then
      echo -en "\\033[60G"
      echo -n "[  "
      echo -en "\\033[0;32m"
      echo -n $"OK"
      echo -en "\\033[0;39m"
      echo -n "  ]"
      echo -en "\r"
   elif [ -e "/etc/SuSE-release" ]; then
      echo -en "\\033[60G"
      echo -en "\\033[1;32m"
      echo -n $"done"
      echo -en "\\033[0;39m"
      echo -en "\r"
   else
      echo -en "\\033[60G"
      echo "OK"
      echo -en "\r"
   fi
   return 0
}

echo_failure() {
   if [ -e "/etc/redhat-release" ]; then
      echo -en "\\033[60G"
      echo -n "["
      echo -en "\\033[0;31m"
      echo -n $"FAILED"
      echo -en "\\033[0;39m"
      echo -n "]"
      echo -en "\r"
   elif [ -e "/etc/SuSE-release" ]; then
      echo -en "\\033[60G"
      echo -en "\\033[0;31m"
      echo -n $"failed"
      echo -en "\\033[0;39m"
      echo -en "\r"
   else
      echo -en "\\033[60G"
      echo "FAILED"
      echo -en "\r"
   fi
    return 1
}

# See how we were called.
case "$1" in
 start)
   start
   ;;
 stop)
   stop
   ;;
restart)
   stop
    sleep 2
    start
    ;;
status)
   status
   ;;
 *)
   echo $"Usage: $PROG {start|stop|restart|status}"
   exit 1
   ;;
esac

exit $RETVAL
```

At the top of the file you should see variables that you can modify for your setup.  Please modify these values above to match your environement


* PROG=twofishes
* PID_DIR="/var/run/twofishes"
* PRODUCT_PID_DIR="$PID_DIR"
* PRODUCT_PID="$PID_DIR/twofishes.pid"
* PRODUCT_LOG_DIR="/var/log/twofishes"
* PRODUCT_ERROR_LOG=$PRODUCT_LOG_DIR/error_log
* PRODUCT_USER="omar"
* PRODUCT_GROUP="omar"
* RUN_PROG_TEST="/opt/twofishes/twofishes.sh"
* RUN_PROGRAM="$RUN_PROG_TEST $PRODUCT_PID 8080"


**NOTE**: This is only an exmaple and you can modify for your installation process.
