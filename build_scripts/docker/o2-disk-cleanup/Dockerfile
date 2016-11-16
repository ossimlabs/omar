FROM 320588532383.dkr.ecr.us-east-1.amazonaws.com/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description=""\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"


ENV JAVA_HOME=/usr \
    O2_DISK_VOLUME=${O2_DISK_VOLUME} \
    O2_MAX_DISK_LIMIT=${O2_MAX_DISK_LIMIT} \
    O2_MIN_DISK_LIMIT=${O2_MIN_DISK_LIMIT} \
    JDBC_CONNECTION_STRING=${JDBC_CONNECTION_STRING} \
    STAGERURL=${STAGERURL}

RUN yum install -y which && \
    yum install -y unzip && \
    curl -s https://get.sdkman.io | bash && \
    source /root/.sdkman/bin/sdkman-init.sh && sdk install gradle

ADD o2-disk-cleanup /o2-disk-cleanup
RUN source /root/.sdkman/bin/sdkman-init.sh && gradle -p /o2-disk-cleanup jar && \
    mv /o2-disk-cleanup/build/libs/o2-disk-cleanup.jar /o2-disk-cleanup.jar && \
    echo "*/10 * * * * java -jar /o2-disk-cleanup.jar > /dev/null 2>&1" > crontab.txt && \
    yum install -y cronie && \
    crontab crontab.txt

CMD while true; do sleep 1000; done;
