# The Download Web Service takes multiple
# images or image groups specified in a
# JSON and return a zip archive.

FROM ossimlabs/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="he Download \
      Web Service takes multiple images or image \
      groups specified in a JSON and return a zip \
      archive."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install o2-download-app
ADD download-app.yml /usr/share/omar/download-app/download-app.yml
ENV DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBNAME=${DBNAME}\
    DOCKER_HOST_IP=${DOCKER_HOST_IP}

ARG O2_DEV_HOME

RUN rm -f /usr/share/omar/download-app/download-app.jar && \
      ln -s $O2_DEV_HOME/apps/download-app/build/libs/download-app-1.0.0.jar /usr/share/omar/download-app/download-app.jar

EXPOSE 8080
CMD ["sh", "/usr/share/omar/download-app/download-app.sh"]
