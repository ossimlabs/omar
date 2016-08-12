# The swipe service takes two image ID's as
# input and allows one to swipe, manual flip,
# or auto flip the images to see changes. It
# uses existing WFS and WMS services. The WFS
# is used to query the holding for the datasets
# used in the swipe. The WMS chips data displayed
# in the swipe view.

FROM radiantbluetechnologies/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="The swipe \
      service takes two image ID's as input and \
      allows one to swipe, manual flip, the images \
      to see changes. It uses existing WFS and WMS \
      services. The WFS is used to query the holding \
      for the datasets used in the swipe. The WMS \
      chips data displayed in the swipe view."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install o2-swipe-app && yum clean all
ADD swipe-app.yml /usr/share/omar/swipe-app/swipe-app.yml
ENV DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBNAME=${DBNAME}\
    WMSURL=${WMSURL}\
    WMSPORT=${WMSPORT}\
    WFSURL=${WFSURL}\
    WFSPORT=${WFSPORT}

EXPOSE 8080
CMD ["sh", "/usr/share/omar/swipe-app/swipe-app.yml"]
