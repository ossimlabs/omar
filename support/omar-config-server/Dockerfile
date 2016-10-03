FROM ossimlabs/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version=""
RUN yum -y install java-1.8.0-openjdk-devel vim git
ADD ./config-repo /usr/share/config-repo
#ADD ./build/libs/omar-config-server-0.0.1-SNAPSHOT.jar /usr/share
ARG O2_DEV_HOME
RUN ln -s $O2_DEV_HOME/support/omar-config-server/build/libs/omar-config-server-0.0.1-SNAPSHOT.jar /usr/share
EXPOSE 8888
CMD java -Xms256m -Xmx1024m -Dspring.cloud.config.server.git.uri=file:///usr/share/config-repo -Djava.security.egd=file:/dev/./urandom -jar /usr/share/omar-config-server-0.0.1-SNAPSHOT.jar
