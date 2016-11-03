# The Time Lapse Viewer application, nicknamed TLV, is
# an on-demand imagery flipbook.

FROM 320588532383.dkr.ecr.us-east-1.amazonaws.com/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="The Time Lapse \
      Viewer application, nicknamed TLV, is an \
      on-deman imagery flipbook."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install time_lapse && yum clean all
ADD tlv-app.yml /usr/share/omar/time_lapse/time_lapse.yml
ENV DOCKER_HOST_IP=${DOCKER_HOST_IP}\
    WFSURL=${WFSURL}\
    WMSURL=${WMSURL}

EXPOSE 8080
CMD ["sh", "/usr/share/omar/time_lapse/time_lapse.sh"]
