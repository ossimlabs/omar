# This image conducts a postgresql installation from
# postgres:latest.  It then installs the PostGIS spatial
# extension for Postgres.  It also adds the postgis
# extension to the specified database.
FROM postgres:latest
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="This image conducts \
      a postgresql installation from postgres:latest. \
      It then installs the PostGIS spatial extension for \
      Postgres. It also adds the postgis extension to the \
      specified database."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"

RUN apt-get -y update && \
    apt-get install -y cron && \
    apt-get install -y gradle && \
    apt-get install -y openjdk-7-jre && \
    apt-get install -y postgis && \
    apt-get -y clean


ADD o2-disk-cleanup /o2-disk-cleanup
RUN gradle -p /o2-disk-cleanup jar
RUN mv /o2-disk-cleanup/build/libs/o2-disk-cleanup.jar /o2-disk-cleanup.jar
RUN echo "*/10 * * * * /o2-disk-cleanup.jar > /dev/null 2>&1" > crontab.txt
RUN crontab crontab.txt

ADD init-add-postgis.sh /docker-entrypoint-initdb.d/init-add-postgis.sh
ENV O2_MAX_DISK_LIMIT=0${O2_MAX_DISK_LIMIT} \
    O2_MIN_DISK_LIMIT=${O2_MIN_DISK_LIMIT} \
    POSTGRES_PASSWORD=${DBPASS}\
    POSTGRES_USER=${DBUSER}\
    POSTGRES_DB=${DBNAME} \
    STAGERURL=${STAGERURL}

EXPOSE 5432
