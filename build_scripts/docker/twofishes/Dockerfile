# Twofishes is not owned or packaged by us
# and the full installation instructions can
# be found at their location. Twofishes
# location service is used by the OMAR/O2 UI
FROM osismlabs/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="Twofishes is\
      not owned or packaged by us and the full\
      installation instructions can be found at\
      their location. Twofishes location service\
      is used by the OMAR/O2 UI."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN mkdir twofishes && \
    yum -y install java-1.8.0-openjdk-devel && \
    yum -y install wget && yum -y install unzip && \
    cd /twofishes && \
    wget https://s3.amazonaws.com/ossimlabs/dependencies/twofishes/server-assembly-0.84.9.jar && \
    wget https://s3.amazonaws.com/ossimlabs/dependencies/twofishes/2015-03-05.zip && \
    unzip 2015-03-05.zip && rm 2015-03-05.zip

# We need to do this because twofishes crashes without it:
# http://jaredmarkell.com/docker-and-locales/
ENV LANG en_US.UTF-8
ENV LANGUAGE en_US:en
ENV LC_ALL en_US.UTF-8

CMD java -Xmx4096m -jar /twofishes/server-assembly-0.84.9.jar --port 8080 --hfile_basepath /twofishes/2015-03-05-20-05-30.753698

EXPOSE 8080 8081 8082 8083
