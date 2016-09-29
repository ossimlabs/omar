# The mensuration service takes image
# points and the image file associated
# with those image points and do different
# forms of measurements. The service
# supports calculating linear and geodetic
# distances based on a path and if the path
# also defines a polygon it will add in
# support for area calculation. We also
# support transforming the image point to
# ground and, if supported by the sensor,
# add in an optional position quality to
# show the horizontal and vertical error
# based on the underlying surface elevation
# used.

FROM ${DOCKER_REGISTRY_URI}/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="The mensuration \
      service takes image points and the image file \
      associated with those image points and do \
      different forms of measurements. The service \
      supports calculating linear and geodetic \
      distances based on a path and if the path \
      also defines a polygon it will add in support \
      for area calculation. We also support transforming \
      the image point to ground and, if supported by the \
      sensor, add in an optional position quality to show \
      the horizontal and vertical error based on the \
      underlying surface elevation used." \
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install o2-mensa-app \
  yum -y install ossim && yum -y install ossim-kakadu-plugin && \
  yum -y install ossim-jpeg12-plugin && yum -y install ossim-sqlite-plugin && \
  yum -y install ossim-geopdf-plugin && \
  yum -y install ossim-png-plugin && yum -y install ossim-gdal-plugin.x86_64
ADD mensa-app.yml /usr/share/omar/mensa-app/mensa-app.yml

EXPOSE 8080
CMD ["sh", "/usr/share/omar/mensa-app/mensa-app.sh"]
