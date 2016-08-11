# Twofishes is not owned or packaged by us
# and the full installation instructions can
# be found at their location. Twofishes
# location service is used by the OMAR/O2 UI

FROM radiantbluetechnologies/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="Twofishes is\
      not owned or packaged by us and the full\
      installation instructions can be found at\
      their location. Twofishes location service\
      is used by the OMAR/O2 UI."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN mkdir twofishes && cd twofishes \
    && yum -y install java-1.8.0-openjdk-devel \
    && wget https://s3.amazonaws.com/ossimlabs/dependencies/twofishes/server-assembly-0.84.9.jar \
    && wget https://s3.amazonaws.com/ossimlabs/dependencies/twofishes/2015-03-05.zip \
    && unzip 2015-03-05.zip \
    && java -jar server-assembly-0.84.9.jar --port 8080 --hfile_basepath 2015-03-05-20-05-30.753698

EXPOSE 8080 8081 8082 8083
