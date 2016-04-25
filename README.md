# Welcome to OMAR suite of Web Applications



## YUM Repository
The current binary delivery for all the OMAR web applications is via a yum repository

```yum
[ossim]
name=CentOS-$releasever - ossim packages for $basearch
baseurl=http://s3.amazonaws.com/o2-rpms/CentOS/$releasever/dev/$basearch
enabled=1
gpgcheck=0
metadata_expire=5m
protect=1
```

create and repo file in you /etc/yum.repos.d directory location.  For now you can call it <b>ossim.repo</b>


* [WMTS Installation and setup](apps/wmts-app/README.md)
