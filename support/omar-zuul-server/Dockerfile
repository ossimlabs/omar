FROM ossimlabs/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version=""
RUN yum -y install java-1.8.0-openjdk-devel
#ADD ./build/libs/omar-zuul-server-0.0.1-SNAPSHOT.jar /usr/share
ARG O2_DEV_HOME
RUN ln -s $O2_DEV_HOME/support/omar-zuul-server/build/libs/omar-zuul-server-0.0.1-SNAPSHOT.jar  /usr/share/omar-zuul-server-0.0.1-SNAPSHOT.jar
EXPOSE 8765
CMD java -Xms256m -Xmx1024m -Dspring.profiles.active=production -Djava.security.egd=file:/dev/./urandom -jar /usr/share/omar-zuul-server-0.0.1-SNAPSHOT.jar
