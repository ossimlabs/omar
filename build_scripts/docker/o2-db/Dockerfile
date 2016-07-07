FROM postgres:latest
RUN apt-get --assume-yes update && apt-get --assume-yes install postgis && apt-get --assume-yes clean
ADD init-add-postgis.sh /docker-entrypoint-initdb.d/init-add-postgis.sh
ENV POSTGRES_PASSWORD=${DBPASS}\
    POSTGRES_USER=${DBUSER}\
    POSTGRES_DB=${DBNAME}

EXPOSE 5432
