FROM rbt/o2base
ADD wmts-app.yml /usr/share/omar/wmts-app/wmts-app.yml
RUN yum -y install o2-wmts-app && yum clean all
ENV DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DBNAME=${DBNAME}\
    WFSSERVER=${WFSSERVER}\
    WFSPORT=${WFSPORT}\
    WMSSERVER=${WMSSERVER}\
    WMSPORT=${WMSPORT}

#CMD ["/usr/bin/java", "-version"]
CMD ["printenv"]

