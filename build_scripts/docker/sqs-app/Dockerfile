FROM radiantbluetechnologies/o2-base
RUN yum -y install o2-sqs-app && yum clean all
ADD sqs-app.yml /usr/share/omar/sqs-app/sqs-app.yml
ENV AWSDNS=${AWSDNS}\
    AWSQUEUEPATH=${AWSQUEUEPATH}
    WAIT_TIME_SECONDS=${WAIT_TIME_SECONDS}
    NUMBER_OF_MESSAGES=${NUMBER_OF_MESSAGES}
    DESTINATION_TYPE=${DESTINATION_TYPE}
    DESTINATION_POST_END_POINT=${DESTINATION_POST_END_POINT}
    DESTINATION_POST_FIELD=${DESTINATION_POST_FIELD}

EXPOSE 8080
CMD ["sh", "/usr/share/omar/sqs-app/sqs-app.sh"]
