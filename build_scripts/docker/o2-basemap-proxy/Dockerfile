FROM 320588532383.dkr.ecr.us-east-1.amazonaws.com/o2-basemap-proxy
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="TBD" \
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"

ENV BASICURL=${BASICURL} \
    BRIGHTURL=${BRIGHTURL}

ADD mapproxy.yaml /mapproxy/mapproxy.yaml
ADD seed.yaml /mapproxy/seed.yaml

ADD start.sh /start.sh
RUN chmod 0755 /start.sh

#USER www-data
# Now launch mappproxy in the foreground
# The script will create a simple config in /mapproxy
# if one does not exist. Typically you should mount
# /mapproxy as a volume
CMD /start.sh
