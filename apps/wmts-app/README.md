# Welcome to WMTS Web Application

WMTS implements the [OGC WMTS standard](http://www.opengeospatial.org/standards/wmts).  The WMTS web app uses the WMS and the WFS web applications and assumes these services are reachable via a http get call from the WMTS we application.  The WFS is used to query what features are about a requested area and the WMS web pplicaiton is used to chip out the data found over that area.  

##Installation

The Current delivery is via RPM installation and the root URL can be found at [http://s3.amazonaws.com/o2-rpms/CentOS](http://s3.amazonaws.com/o2-rpms/CentOS). Note, the URL is not listable.

We currently do not have an RPM install that creates a yum repo so a yum repo should be created manaually with the following contents:

```yum
[ossim]
name=CentOS-$releasever - ossim packages for $basearch
baseurl=http://s3.amazonaws.com/o2-rpms/CentOS/$releasever/dev/$basearch
enabled=1
gpgcheck=0
metadata_expire=5m
protect=1
```

This is for the dev branch location.  If you wish to have access to the master branch then you can change the location to be master for the baseurl:

 baseurl=http://s3.amazonaws.com/o2-rpms/CentOS/$releasever/master/$basearch

##Configuration

The configuration file is a yaml formatted config file that contains the following settings:

```yaml
omar:
  wmts:
    wfsUrl: http://localhost:8080/wfs
    wmsUrl: http://localhost:8080/wms
    oldmarWmsFlag: false
```

notice each indentation level is 2 characters and must not be a tab character.

* <b>wfsUrl:</b> is used to identify the endpoint location for querying the WFS information.
* <b>wmsUrl:</b> is used to chip a region based on the WMTS query specification.
* <b>oldmarWmsFlag:</b> The format of the query string has changed in the newer versions of omar WMS implementation.   If you have an installation of OMAR that is 1.8.20 or older then you can turn this flag on and it should enable a different query string for requesting the WMS chip.

