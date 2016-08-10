# The JPIP streaming service allows one to
# stream a directory of JP2 images to a client
# via a continuous socket or stateless http
# requests.

FROM radiantbluetechnologies/o2-base
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
# RUN systemctl start jpip-server
# ENTRYPOINT systemctl start jpip-server && bash
CMD systemctl enable jpip-server.service && bash
# ENTRYPOINT ["/usr/share/omar/jpip-server/jpip-server.sh"]
# CMD ["sh", "/usr/share/omar/jpip-server/jpip-server.sh"] && bash

EXPOSE 8080
