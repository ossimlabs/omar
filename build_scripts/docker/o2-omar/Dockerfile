# The OMAR Web application, nicknamed O2, is a
# springboard for the new O2 services and allows
# one to quickly discover new assets and display
# them using common OGC services.

FROM radiantbluetechnologies/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="The OMAR Web \
      application, nicknamed O2, is a springboard \
      for the new O2 services and allows one to \
      quickly discover new assets and display them \
      using common OGC services."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install o2-omar-app && yum -y clean all
ADD omar-app.yml /usr/share/omar/omar-app/omar-app.yml
ENV DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBNAME=${DBNAME}\
    WFSURL=${WFSURL}\
    WFSPORT=${WFSPORT}\
    WMSURL=${WMSURL}\
    WMSPORT=${WMSPORT}

EXPOSE 8080
CMD ["sh", "/usr/share/omar/omar-app/omar-app.sh"]
