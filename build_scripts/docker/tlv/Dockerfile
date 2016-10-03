FROM java:8-jdk
MAINTAINER RadiantBlue Technologies radiantblue.co

WORKDIR /app

RUN apt-get install -y git


# get the main tlv source code
RUN git clone --depth 1 https://github.com/time-lapse-viewer/tlv.git tlv_master


# get ossimlabs network specific code
RUN git clone https://github.com/ossimlabs/tlv.git ossimlabs_tlv
WORKDIR /app/ossimlabs_tlv
RUN git fetch
RUN git checkout dev

# replace the tlv source code as necessary
WORKDIR /app
RUN rm -rf tlv_master/plugins/network_specific
RUN mv ossimlabs_tlv/plugins/network_specific tlv_master/plugins/
RUN rm -rf /app/ossimlabs_tlv


# add the application documentation
WORKDIR /app
RUN  mv tlv_master/docs/tlv.html tlv_master/time_lapse/grails-app/conf/


# install grails
ENV GRAILS_VERSION 3.1.5
RUN curl -L -o grails.zip  https://github.com/grails/grails-core/releases/download/v${GRAILS_VERSION}/grails-${GRAILS_VERSION}.zip
RUN unzip grails.zip
RUN rm grails.zip

ENV GRAILS_HOME /app/grails-$GRAILS_VERSION
ENV PATH $GRAILS_HOME/bin:$PATH


# export container environment variables
ENV DOCKER_HOST_IP $DOCKER_HOST_IP
ENV WFSURL $WFSURL
ENV WMSURL $WMSURL


# add the config file
ADD tlv-app.yml .
ENV SPRING_CONFIG_LOCATION /app/tlv-app.yml


# adjust the tlv source code to accommodate deployment

# take into account the web proxy path
RUN sed -i -e 's/\/assets/\/tlv\/assets/g' /app/tlv_master/time_lapse/grails-app/conf/tlv.html
RUN sed -i -e 's/${request.contextPath}/\/tlv/g' /app/tlv_master/time_lapse/grails-app/views/index.gsp

# force a jar artifact
RUN sed -i '/apply plugin:"war"/d' /app/tlv_master/time_lapse/build.gradle

# create the artifact
WORKDIR /app/tlv_master/time_lapse
RUN ./gradlew assemble
RUN mv /app/tlv_master/time_lapse/build/libs/time_lapse-0.1.jar /app/tlv.jar

# delete unnecessary files
RUN rm -rf $GRAILS_HOME
RUN rm -rf /app/tlv_master
RUN rm -rf /root/.gradle


# run the jar
WORKDIR /app
EXPOSE 8080
CMD java -jar tlv.jar
