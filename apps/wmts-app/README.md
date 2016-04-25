# Welcome to WMTS Web Application

WMTS implements the [OGC WMTS standard](http://www.opengeospatial.org/standards/wmts).  The WMTS web app uses the [WMS](../wms-app/README.md) and the [WFS](../wfs-app/README.md) web applications and assumes these services are reachable via a http "GET" call from the WMTS service.  When the WMTS formatted query params are performed the WFS is used to query what features are about a requested area and the WMS service is used to chip out the data found over that area.  

##Installation

We assume you have configured the yum repository described in the [root location](../../README.md).  To install you should be able to issue the following yum command

```yum
yum install o2-wmts-app
```

The installation will put a fat jar located under /usr/share/omar/wmts-app/ location.  At the time of writing this document the only file installed via the RPM is the fat jar.

You will next need to configure the application.


##Configuration

The configuration file is a yaml formatted config file.   For now create a file called wmts-app.yaml that contains the following settings:

```yaml
server:
  port: 8080

omar:
  wmts:
    wfsUrl: http://localhost:8080/wfs
    wmsUrl: http://localhost:8080/wms
    oldmarWmsFlag: false
```

notice each indentation level is 2 characters and must not be a tab character.

* <b>port:</b> For the server.port you can set the port that the web application will come up on.  By default the port is 8080
* <b>wfsUrl:</b> is used to identify the endpoint location for querying the WFS information.  The default location of localhost will have to be changed to your installation of the OMAR wfs service.
* <b>wmsUrl:</b> is used to chip a region based on the WMTS query specification.  The default location of localhost will have to be changed to where the WMS chipping endpoint resides. 
* <b>oldmarWmsFlag:</b> The format of the query string has changed in the newer versions of omar WMS implementation.   If you have an installation of OMAR that is 1.8.20 or older then you can turn this flag on and it will enable a different query string for requesting the WMS chip.

##Executing

After you have setup your wmts-app.yaml file you can run the application by issuing the following command:

```
java -jar /usr/share/omar/wmts-app/wmts-app-<version>.jar --spring.config.location=<location wmts-app.yaml>
```
For better performance you might want to tune the JAVA options for your environment.  Here is an example but you may add any number of additional JAVA_OPTS to the options.

```
export JAVA_OPTS="-server -Xms256m -Xmx2048m -Djava.awt.headless=true -XX:MaxPermSize=256m -XX:+CMSClassUnloadingEnabled -XX:+UseGCOverheadLimit"
java $JAVA_OPTS -jar /usr/share/omar/wmts-app/wmts-app-<version>.jar --spring.config.location=<location wmts-app.yaml>

```

