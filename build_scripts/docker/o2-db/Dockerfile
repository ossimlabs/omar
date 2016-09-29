# This image conducts a postgresql installation from
# postgres:latest.  It then installs the PostGIS spatial
# extension for Postgres.  It also adds the postgis
# extension to the specified database.
FROM postgres:9.5
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="This image conducts \
      a postgresql installation from postgres:latest. \
      It then installs the PostGIS spatial extension for \
      Postgres. It also adds the postgis extension to the \
      specified database."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN apt-get --assume-yes update && apt-get --assume-yes install postgis && apt-get --assume-yes clean

ADD init-add-postgis.sh /docker-entrypoint-initdb.d/init-add-postgis.sh
ENV POSTGRES_PASSWORD=${DBPASS}\
    POSTGRES_USER=${DBUSER}\
    POSTGRES_DB=${DBNAME}

EXPOSE 5432
