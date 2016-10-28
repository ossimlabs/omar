# The SQS service allows one to Read from an Amazon Simple Queueing
# System (SQS) and redirect the messages either to standard out or
# to another URL endpoint by issuing a post. We use a background
# job to poll the SQS for any pending messages and will only
# delete the messages from the SQS if the message was handled
# properly. If the message was not handled properly it will
# remain on the queue and be reset automatically based on the queue
# properties.

FROM 320588532383.dkr.ecr.us-east-1.amazonaws.com/o2-base
MAINTAINER RadiantBlue Technologies radiantblue.com
LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="The SQS service allows \
      one to Read from an Amazon Simple Queueing System (SQS) \
      and redirect the messages either to standard out or to \
      another URL endpoint by issuing a post. We use a background \
      job to poll the SQS for any pending messages and will only \
      delete the messages from the SQS if the message was handled \
      properly. If the message was not handled properly it will \
      remain on the queue and be reset automatically based on the \
      queue properties."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
RUN yum -y install o2-sqs-app && yum clean all
ADD sqs-app.yml /usr/share/omar/sqs-app/sqs-app.yml
ENV AWSDNS=${AWSDNS}\
    AWSQUEUEPATH=${AWSQUEUEPATH}\
    WAIT_TIME_SECONDS=${WAIT_TIME_SECONDS}\
    NUMBER_OF_MESSAGES=${NUMBER_OF_MESSAGES}\
    DESTINATION_TYPE=${DESTINATION_TYPE}\
    DESTINATION_POST_END_POINT=${DESTINATION_POST_END_POINT}\
    DESTINATION_POST_END_POINT_PORT=${DESTINATION_POST_END_POINT_PORT}\
    DESTINATION_POST_FIELD=${DESTINATION_POST_FIELD}\
    SPRING_CLOUD_CONFIG_ENABLED=true\
    SPRING_CLOUD_DISCOVERY_ENABLED=true


ARG O2_DEV_HOME

EXPOSE 8080
CMD ["sh", "/usr/share/omar/sqs-app/sqs-app.sh"]
