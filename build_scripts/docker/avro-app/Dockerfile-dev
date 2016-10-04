FROM 320588532383.dkr.ecr.us-east-1.amazonaws.com/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
RUN yum -y install o2-avro-app && yum clean all
ADD avro-app.yml /usr/share/omar/avro-app/avro-app.yml
ENV DBUSER=${DBUSER}\
    DBPASS=${DBPASS}\
    DBHOST=${DBHOST}\
    DBPORT=${DBPORT}\
    DBNAME=${DBNAME}\
    ADDRASTERENDPOINTURL=${ADDRASTERENDPOINTURL}\
    ADDRASTERENDPOINTPORT=${ADDRASTERENDPOINTPORT}

EXPOSE 8080
CMD ["sh", "/usr/share/omar/avro-app/avro-app.sh"]
