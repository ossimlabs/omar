FROM centos:latest
add ossim.repo /etc/yum.repos.d/ossim.repo
add wmts-app.yml /usr/share/omar/wmts-app/wmts-app.yml
run yum -y install java-1.8.0-openjdk http://dl.fedoraproject.org/pub/epel/7/x86_64/e/epel-release-7-6.noarch.rpm o2-wmts-app
CMD ["/usr/bin/java", "-version"]

