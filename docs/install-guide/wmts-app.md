# Welcome to WMTS Web Service

WMTS implements the [OGC WMTS standard](http://www.opengeospatial.org/standards/wmts).  The WMTS web app uses the [WMS](wms-app.md) and the [WFS](wfs-app.md) web services and assumes these services are reachable via a http "GET" call from the WMTS service.  The WMTS service wraps the WMTS service call and 1) converts to a WFS query to get the features that cover the WMTS query parameters and 2) calls the WMS service to chip and return the pixel values that satisfy the WMTS request.  

If you want to take it for a test drive please visit the [vagrant setup](https://github.com/ossimlabs/ossim-vagrant).

##Installation

We assume you have read the generalized installation procedures that shows the common configuration created for all services in the OMAR distribution found in the [OMAR Common Install Guide](common.md).  To install you should be able to issue the following yum command

```
yum install o2-wmts-app
```

The installation sets up

* Startup scripts that include /etc/init.d/wmts-app for init.d support and /usr/lib/systemd/system/wmts-app.service for systems running systemd
* Creates a system user called *omar*
* Creates log directory with user *omar* permissions under /var/log/wmts-app
* Creates a var run directory with user *omar* permissions under /var/run/wmts-app
* Adds the fat jar and shell scripts under the directory /usr/share/omar/wmts-app location


##Configuration

The configuration file is a yaml formatted config file.   For now create a file called wmts-app.yaml.  At the time of writting this document we do not create this config file for this is usually site specific configuration and is up to the installer to setup the document

```
vi /usr/share/omar/wmts-app/wmts-app.yml
```

 that contains the following settings:

```
server:
  contextPath:
  port: 8080

environments:
  production:
    dataSource:
      pooled: true
      jmxExport: true
      driverClassName: org.postgresql.Driver
      username: postgres
      password:
      dialect: 'org.hibernate.spatial.dialect.postgis.PostgisDialect'
      url: jdbc:postgresql://<ip>:<port>/omardb-prod

omar:
  wmts:
    wfsUrl: http://<wfs-service-ip>:<port>/wfs
    wmsUrl: http://<wms-service-ip>:<port>/wms
    oldmarWmsFlag: false
    footprints:
      url: "http://<ip>:<port>/footprints/getFootprints"
      layers: "omar:raster_entry"
      styles: "byFileType"
---
grails:
  serverURL: http://<ip>:<port>/
  assets:
    url: http://<ip>:<port>/assets/

```
Please modify the configuration for your environment.

notice each indentation level is 2 characters and must not be a tab character.

* **port:** For the server.port you can set the port that the web application will come up on.  By default the port is 8080.  If you are going through a proxy then ignore the port and use the proxy path to the service.
* **dataSource** Was already covered in the common [OMAR Common Install Guide](common.md).
* **contextPath:** For most installation you will set server.contextPath to empty and proxy the request via a httpd proxy to the port 8080.  If a context path is used then the services access point is of the form: http://\<url>:\<port>/\<contextPath>
* **wfsUrl:** is used to identify the endpoint location for querying the WFS information.  The default location of localhost will have to be changed to your installation of the OMAR wfs service. If you are going through a proxy then ignore the port and use the proxy path to the service.
* **wmsUrl:** is used to chip a region based on the WMTS query specification.  The default location of localhost will have to be changed to where the WMS chipping endpoint resides. 
* **oldmarWmsFlag:** The format of the query string has changed in the newer versions of omar WMS implementation.   If you have an installation of OMAR that is 1.8.20 or older then you can turn this flag on and it will enable a different query string for requesting the WMS chip.
* **footprints** Used to define the footprints service location and style and layer definition.  The layer will probably stay the value it is but the styles might change if you want different footprint colorings.
* **grails.serverURL** point to the root location of the wmts-app server. The example goes directly to the service via 8080.  If a proxy is used then you must add the proxy end point.
* **assets url** This is the url to the assets location.  Just add the **/assets/** path to the serverURL.

##Executing

To run the service on systems that use the init.d you can issue the command.

```bash
sudo service wmts-app start
```

On systems using systemd for starting and stopping

```bash
sudo systemctl start wmts-app
```

The service scripts calls the shell script under the directory /usr/share/omar/wmts-app/wmts-app.sh.   You should be able to tail the wmts-app.log to see any standard output

```bash
tail -f /var/log/wmts-app/wmts-app.log
```

If all is good, then you should see a line that looks similar to the following:

```bash
Grails application running at http://localhost:8080 in environment: production
```

You can now verify your service with:

`curl http://localhost:8080/wmts?request=GetCapabilities`

which should return an XML document with meta-data about the service.
