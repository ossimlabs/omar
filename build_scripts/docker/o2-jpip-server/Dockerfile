# The JPIP streaming service allows one to
# stream a directory of JP2 images to a client
# via a continuous socket or stateless http
# requests.

ARG DOCKER_REGISTRY_URI
FROM ${DOCKER_REGISTRY_URI}/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="The JPIP streaming \
      service allows one to stream a directory of JP2 \
      images to a client via a continuous socket or \
      stateless http requests." \
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install ossim-jpip-server && yum clean all \
    && mkdir -p /usr/share/omar/jpip-server \
    && mkdir -p /data/jpip-cache \
    && chown omar:omar /usr/share/omar
ADD jpip-server.sh /usr/share/omar/jpip-server/jpip-server.sh

CMD ["sh", "/usr/share/omar/jpip-server/jpip-server.sh"]

EXPOSE 8080
