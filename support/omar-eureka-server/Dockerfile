FROM ossimlabs/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version=""
RUN yum -y install java-1.8.0-openjdk-devel
ADD ./build/libs/omar-eureka-server-0.0.1-SNAPSHOT.jar /usr/share
EXPOSE 8761
CMD java -Xms256m -Xmx1024m -Dspring.profiles.active=production -Djava.security.egd=file:/dev/./urandom -jar /usr/share/omar-eureka-server-0.0.1-SNAPSHOT.jar
