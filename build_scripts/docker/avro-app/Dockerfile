FROM radiantbluetechnologies/o2-base
RUN yum -y install o2-avro-app && yum clean all
ADD avro-app.yml /usr/share/omar/avro-app.yml

EXPOSE 8082
CMD ["sh", "/usr/share/omar/avro-app/avro-app.sh"]
