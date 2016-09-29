# The JPIP web service is an interface to
# convert imagery so the jpip-server can
# stream to a client. When interfacing into
# the JPIP web application you can post
# messages to request a URL. The result that
# is returned is a JSON formatted string that
# has the URL and the state at which the URL
# is in. For example. If a JPIP stream is
# requested on a given image and if the image
# does not have a JPIP stream associated with
# it yet then it will submit the image for background
# processing and return a STATUS.

FROM ${DOCKER_REGISTRY_URI}/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="The JPIP web service \
      is an interface to convert imagery so the jpip-server \
      can stream to a client. When interfacing into the JPIP \
      web application you can post messages to request a URL. \
      The result that is returned is a JSON formatted string \
      that has the URL and the state at which the URL is in. \
      For example. If a JPIP stream is requested on a given \
      image and if the image does not have a JPIP stream \
      associated with it yet then it will submit the image \
      for background processing and return a STATUS." \
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install o2-jpip-app && yum -y install ossim-kakadu-plugin && yum clean all
ADD jpip-app.yml /usr/share/omar/jpip-app/jpip-app.yml
ENV DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DBNAME=${DBNAME}\
    DOCKER_HOST_IP=${DOCKER_HOST_IP}\
    JPIPCACHEDIR=${JPIPCACHEDIR}\
    JPIPSERVERURL=${JPIPSERVERURL}\
    JPIPSERVERPORT=${JPIPSERVERPORT}

EXPOSE 8080
CMD ["sh", "/usr/share/omar/jpip-app/jpip-app.sh"]
