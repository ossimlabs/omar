FROM 320588532383.dkr.ecr.us-east-1.amazonaws.com/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description=""\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"

ENV JAVA_HOME=/usr \
    JDBC_CONNECTION_STRING=${JDBC_CONNECTION_STRING} \
    O2_DISK_VOLUME=${O2_DISK_VOLUME} \
    O2_MAX_DISK_LIMIT=${O2_MAX_DISK_LIMIT} \
    O2_MIN_DISK_LIMIT=${O2_MIN_DISK_LIMIT} \
    POSTGRES_PASSWORD=${POSTGRES_PASSWORD} \
    POSTGRES_USER=${POSTGRES_USER} \
    STAGER_URL=${STAGER_URL}

RUN yum install -y cronie && \
    yum install -y o2-disk-cleanup && \
    yum install -y initscripts && \
    yum clean all && \
    echo "*/10 * * * * . $HOME/.profile; java -jar /usr/share/omar/disk-cleanup/disk-cleanup.jar >> /dev/null 2>&1" > crontab.txt && \
    crontab crontab.txt

CMD echo "export JAVA_HOME=/usr" >> $HOME/.profile && \
    echo "export JDBC_CONNECTION_STRING=${JDBC_CONNECTION_STRING}" >> $HOME/.profile && \
    echo "export O2_DISK_VOLUME=${O2_DISK_VOLUME}" >> $HOME/.profile && \
    echo "export O2_MAX_DISK_LIMIT=${O2_MAX_DISK_LIMIT}" >> $HOME/.profile && \
    echo "export O2_MIN_DISK_LIMIT=${O2_MIN_DISK_LIMIT}" >> $HOME/.profile && \
    echo "export POSTGRES_PASSWORD=${POSTGRES_PASSWORD}" >> $HOME/.profile && \
    echo "export POSTGRES_USER=${POSTGRES_USER}" >> $HOME/.profile && \
    echo "export STAGER_URL=${STAGER_URL}" >> $HOME/.profile && \
    crond -p && \
    while true; do sleep 1000; done;
