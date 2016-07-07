FROM radiantbluetechnologies/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
RUN yum -y install o2-stager-app ossim-kakadu-plugin ossim-sqlite-plugin && yum clean all
ADD stager-app.yml /usr/share/omar/stager-app/stager-app.yml
ENV DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DBNAME=${DBNAME}

EXPOSE 8080
CMD ["sh", "/usr/share/omar/stager-app/stager-app.sh"]
