# This service takes an AVRO JSON payload or JSON record
# from an AVRO file as input and will process the file
# by looking for the reference URI field and downloading
# the File.
FROM ossimlabs/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="Takes an AVRO JSON \
      payload or JSON record from an AVRO file as \
      input and will process the file by looking for \
      the reference URI field and downloading the File."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install o2-avro-app && yum clean all
ADD avro-app.yml /usr/share/omar/avro-app/avro-app.yml
ENV DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DBNAME=${DBNAME}\
    DOCKER_HOST_IP=${DOCKER_HOST_IP}\
    ADDRASTERENDPOINTURL=${ADDRASTERENDPOINTURL}\
    ADDRASTERENDPOINTPORT=${ADDRASTERENDPOINTPORT}

EXPOSE 8080
CMD ["sh", "/usr/share/omar/avro-app/avro-app.sh"]
