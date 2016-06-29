# This is a base container to install java and the ossim repo
# there isn't a command to run it is meant to serve as
# a beginning for the rest of the o2 apps
FROM centos:latest
ADD ossim.repo /etc/yum.repos.d/ossim.repo
RUN yum -y install https://dl.fedoraproject.org/pub/epel/epel-release-latest-7.noarch.rpm java-1.8.0-openjdk haveged && yum clean all
