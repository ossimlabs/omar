# The OMAR Web application, nicknamed O2, is a
# springboard for the new O2 services and allows
# one to quickly discover new assets and display
# them using common OGC services.

FROM ossimlabs/o2-base-all
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="The OMAR Web \
      application, nicknamed O2, is a springboard \
      for the new O2 services and allows one to \
      quickly discover new assets and display them \
      using common OGC services."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
# RUN yum -y install o2-omar-app && yum -y clean all
ADD omar-app.yml /usr/share/omar/omar-app/omar-app.yml
ENV DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBNAME=${DBNAME}\
    DOCKER_HOST_IP=${DOCKER_HOST_IP}\
    WFSURL=${WFSURL}\
    WFSPORT=${WFSPORT}\
    WMSURL=${WMSURL}\
    WMSPORT=${WMSPORT}\
    STAGERURL=${STAGERURL}\
    STAGERPORT=${STAGERPORT}\
    DOWNLOADURL=${DOWNLOADURL}\
    DOWNLOADPORT=${DOWNLOADPORT}\
    KMLURL=${KMLURL}\
    KMLPORT=${KMLPORT}\
    PREDIOURL=${PREDIOURL}\
    PREDIOPORT=${PREDIOPORT}\
    PREDIOENABLED=${PREDIOENABLED}\
    TWOFISHURL=${TWOFISHURL}\
    TWOFISHPORT=${TWOFISHPORT}\
    SWIPEURL=${SWIPEURL}\
    SWIPEPORT=${SWIPEPORT}\
    SWIPEENABLED=${SWIPEENABLED}\
    JPIPURL=${JPIPURL}\
    JPIPPORT=${JPIPPORT}\
    JPIPENABLED=${JPIPENABLED}\
    WMTSURL=${WMTSURL}\
    WMTSPORT=${WMTSPORT}\
    WMTSENABLED=${WMTSENABLED}\
    PWIKURL=${PWIKURL}\
    PWIKPORT=${PWIKPORT}\
    PWIKENABLED=${PWIKENABLED}\
    APIURL=${APIURL}\
    APIPORT=${APIPORT}\
    APIENABLED=${APIENABLED}\
    BANNERCOLOR=${BANNERCOLOR}\
    BANNERCLASSIFICATION=${BANNERCLASSIFICATION}
RUN rm /usr/share/omar/omar-app/omar-app.jar && \
    ln -s /Users/sbortman/IdeaProjects/omar/apps/omar-app/build/libs/omar-app-1.0.0.jar /usr/share/omar/omar-app/omar-app.jar

EXPOSE 8080
CMD ["sh", "/usr/share/omar/omar-app/omar-app.sh"]
#RUN java -server -Xms256m -Xmx1024m -Djava.awt.headless=true -XX:+CMSClassUnloadingEnabled -XX:+UseGCOverheadLimit -jar /usr/share/omar/omar-app/omar-app-1.0.0.jar --server.contextPath=/o2-omar --spring.config.location=/usr/share/omar/wfs-app/wfs-app.yml
#RUN java -server -Xms256m -Xmx1024m -Djava.awt.headless=true -XX:+CMSClassUnloadingEnabled -XX:+UseGCOverheadLimit -jar /usr/share/omar/omar-app/omar-app-1.0.0.jar --spring.config.location=/usr/share/omar/wfs-app/wfs-app.yml
