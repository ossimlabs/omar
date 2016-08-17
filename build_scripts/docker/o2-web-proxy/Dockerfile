# O2 Web Service proxy

FROM ossimlabs/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="O2 Web Service \
      proxy"\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y update && yum clean all \
    && yum -y install httpd mod_ssl && yum clean all

COPY reverse-proxy.conf /etc/httpd/conf.d/reverse-proxy.conf

# Simple startup script to avoid some issues observed with container restart
ADD run-httpd.sh /run-httpd.sh
RUN chmod -v +x /run-httpd.sh

CMD ["/run-httpd.sh"]

EXPOSE 80
