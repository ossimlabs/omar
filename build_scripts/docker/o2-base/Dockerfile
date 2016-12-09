# This is a base container to install java and the ossim repo
# there isn't a command to run it is meant to serve as
# a beginning for the rest of the o2 apps
# A base image to install java and the ossim repo.
# There isn't a command to run it is meant to serve as
# a beginning for the rest of the o2 apps.
FROM centos:latest
MAINTAINER RadiantBlue Technologies radiantblue.com
ARG OSSIM_GIT_BRANCH=dev

LABEL com.radiantblue.version="0.1"\
      com.radiantblue.description="A base image to \
      install java and the ossim repo. There isn't \
      a command to run it is meant to serve as a \
      beginning for the rest of the o2 apps."\
      com.radiantblue.source=""\
      com.radiantblue.classification="UNCLASSIFIED"
ADD ossim.repo /etc/yum.repos.d/ossim.repo
RUN sed -i "s/OSSIM_GIT_BRANCH/${OSSIM_GIT_BRANCH}/g" /etc/yum.repos.d/ossim.repo
##
# If EPEL is doing a full mirror list synchronization you might get errors
# Uncomment this to use a fixed location for the epel
ADD epel.repo /etc/yum.repos.d/epel.repo

##
# If the mirror list for epel is good then use the RPM for epel installation
# and uncomment this run command out and comment the next run command
#RUN yum -y install https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm java-1.8.0-openjdk haveged && yum clean all
RUN yum -y install java-1.8.0-openjdk haveged && yum clean all

