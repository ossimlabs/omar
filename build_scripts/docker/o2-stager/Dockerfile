# The stager service currently indexes the data into
# the OMAR system.

FROM ossimlabs/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="The stager service \
      currently indexes the data into the OMAR system."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install o2-stager-app ossim-kakadu-plugin ossim-sqlite-plugin && yum clean all
ADD stager-app.yml /usr/share/omar/stager-app/stager-app.yml
ENV DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DBNAME=${DBNAME}\
    DOCKER_HOST_IP=${DOCKER_HOST_IP}

RUN rm -f /usr/share/omar/stager-app/stager-app.jar && \
      ln -s /Users/sbortman/IdeaProjects/omar/apps/stager-app/build/libs/stager-app-1.0.0.jar /usr/share/omar/stager-app/stager-app.jar

EXPOSE 8080
CMD ["sh", "/usr/share/omar/stager-app/stager-app.sh"]
