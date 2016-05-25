# Welcome to OMAR Web UI Application

The OMAR Web application, nicknamed O2, is a springboard for the new O2 services and allows one to quickly discover new assets and display them using common OGC services.

##Installation

We assume you have configured the yum repository described in [OMAR Common Install Guide](common.md).  To install you should be able to issue the following yum command

```yum
yum install o2-omar-app
```
The installation sets up

* Startup scripts that include /etc/init.d/omar-app for init.d support and /usr/lib/systemd/system/omar-app.service for systems running systemd
* Creates a system user called *omar*
* Creates log directory with user *omar* permissions under /var/log/omar-app
* Creates a var run directory with user *omar* permissions under /var/run/omar-app
* Adds the fat jar and shell scripts under the directory /usr/share/omar/omar-app location



##Configuration

The configuration file is a yaml formatted config file.   For now create a file called omar-app.yaml.  At the time of writting this document we do not create this config file for this is usually site specific configuration and is up to the installer to setup the document

```bash
vi /usr/share/omar/wms-app/omar-app.yml
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
      password: postgres
      dialect: 'org.hibernate.spatial.dialect.postgis.PostgisDialect'
      url: jdbc:postgresql://<ip>:<port>/omardb-prod

omar:
  app:
    root:
      baseUrl: http://<ip>/omar-app
    wfs:
      baseUrl: http://192.168.2.200/wfs-app/wfs?
      enabled: true
      proxy: /proxy/index?url=
    wms:
      baseUrl: http://<ip>/wms-app/wms?
      enabled: true
    imageSpace:
      baseUrl: http://<ip>/wms-app/imageSpace
      enabled: true
    thumbnails:
      baseUrl: http://<ip>/wms-app/imageSpace/getThumbnail?
    footprints:
      baseUrl: http://<ip>/wms-app/footprints/getFootprints
    kmlApp:
      baseUrl: http://<ip>/superoverlay-app
    predio:
      baseUrl: http://<ip>/predio-app/predio/
      enabled: false
    twofishes:
      baseUrl: http://<ip>:<port>/twofish
      proxy: /twoFishesProxy
    swipeApp:
      baseUrl: http://<ip>/swipe-app/swipe
      enabled: true
    jpipApp:
      baseUrl: http://<ip>/jpip-app/jpip
      enabled: true
    misc:
      icons:
        green-marker: search_marker_green.png
---
grails:
  serverURL: http://192.168.2.200/omar-app
  assets:
    url: http://192.168.2.200/omar-app/assets/
```

* **contextPath:**, **port:**, **dataSource** Were already covered in the common [OMAR Common Install Guide](common.md).
* **omar.app.root** Root settings for the rot url.
 * **baseURL** Base URL for the omar-app
* **omar.app.wfs** Base URL and flag for WFS.
 * **baseURL** Base URL for the WFS service 
 * **enabled** Flag used to specify if the WFS service is enabled and ready
* **omar.app.wms** Base URL and settings for the WMS service.
 * **baseURL** Base URL for the WMS service call
 * **enabled** Flag used to specify if the WMS service is enabled
* **omar.app.imageSpace** Specifies the base settings for the image space services
 * **baseURL** Base URL for the image space services.  Because of the dependencies for WMS the baseURL can use the path to the IP/DNS location of the WMS service. In the above example we have a proxy called wms-app that points to the base WMS service on your WMS instance.
 * **enabled** specifies whether the image space services are enabled
* **omar.app.thunbnails** Base settings for thumbnail generation
 * **baseURL**  Base URL for the thumbnail service. Because of the dependencies for WMS the baseURL can use the path to the IP/DNS location of the WMS service. In the above example we have a proxy called wms-app that points to the base WMS service on your WMS instance.
* **omar.app.footprints** Base settings for footprints
 * **baseURL** Base URL for the footprints service.   Because of the dependencies for WMS the baseURL can use the path to the IP/DNS location of the WMS service. In the above example we have a proxy called wms-app that points to the base WMS service on your WMS instance.
* **omar.app.kmlApp** Base settings for super overlay
 * **baeURL** Base URL for the KML superoverlay service. 
* **omar.app.predio** Base settings for predictionIO
 * **baseURL** Base URL for the location of the Prediction IO service API. 
* **omar.app.twofishes** Base settings for twofishes
 * **baseURL** Base URL for the Twofishes service.  This provide geolocation for coutry names, .. etc. 
* **omar.app.swipeApp** Base settings for swipe service
 * **baseURL** Base URL for the swipe service.
* **omar.app.jpipApp** Base settings for thumbnail generation
 * **baseURL** Base URL for the JPIP service.
 * **enabled** Allows one to specify if the service is enabled.
