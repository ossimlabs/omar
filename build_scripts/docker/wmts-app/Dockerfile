FROM centos:latest
ADD ossim.repo /etc/yum.repos.d/ossim.repo
ADD wmts-app.yml /usr/share/omar/wmts-app/wmts-app.yml
RUN yum -y install java-1.8.0-openjdk http://dl.fedoraproject.org/pub/epel/7/x86_64/e/epel-release-7-6.noarch.rpm o2-wmts-app && yum clean all
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

