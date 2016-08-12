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

**Assumptions**:

* OMAR UI IP location is 192.168.2.120 on port 8080
* Proxy server is running under the location 192.168.2.200
* Proxy pass entry `ProxyPass /omar-app http://192.168.2.120:8080`
* Postgres database accessible via the IP and port 192.168.2.100:5432 with a database named omardb-prod.  The database can be any name you want as long as you specify it in the configuration.  If the database name or the IP and port information changes please replace in the YAML config file example

The assumptions here has the root URL for the Stager service reachable via the proxy by using IP http://192.168.2.200/stager-app and this is proxied to the root IP of the stager-app service located at http://192.168.2.102:8080. **Note: please change the IP's and ports for your setup accordingly**.

The configuration file is a yaml formatted config file.   For now create a file called omar-app.yaml.  At the time of writting this document we do not create this config file for this is usually site specific configuration and is up to the installer to setup the document

```bash
vi /usr/share/omar/omar-app/omar-app.yml
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
  openlayers:
    baseMaps:
      -
        layerType: "tile"
        title: "Open Street Map"
        url: "http://vmap0.tiles.osgeo.org/wms/vmap0"
        params:
          layers: "basic,coastline_01,coastline_02,priroad,secroad,rail,ferry,tunnel,bridge,trail,CAUSE,clabel,statelabel,ctylabel"
          format: "image/jpeg"
        options:
          visible: false
  app:
    root:
      baseUrl: http://192.168.2.200/omar-app
    wfs:
      baseUrl: http://192.168.2.200/wfs-app/wfs?
      enabled: true
      proxy: /proxy/index?url=
    wms:
      baseUrl: http://192.168.2.200/wms-app/wms?
      enabled: true
    imageSpace:
      baseUrl: http://192.168.2.200/wms-app/imageSpace
      enabled: true
    thumbnails:
      baseUrl: http://192.168.2.200/wms-app/imageSpace/getThumbnail?
    footprints:
      baseUrl: http://192.168.2.200/wms-app/footprints/getFootprints
    downloadApp:
      baseUrl: http://192.168.2.200/download-app
    kmlApp:
      baseUrl: http://192.168.2.200/superoverlay-app
    predio:
      baseUrl: http://192.168.2.200/predio-app/predio/
      enabled: false
    twofishes:
      baseUrl: http://<ip>:<port>/twofish
      proxy: /twoFishesProxy
    swipeApp:
      baseUrl: http://192.168.2.200/swipe-app/swipe
      enabled: true
    jpipApp:
      baseUrl: http://192.168.2.200/jpip-app/jpip
      enabled: true
    piwikApp:
      baseUrl: http://<url>/piwik/
      enabled: true
    apiApp:
      baseUrl: http://localhost:8081/api
      enabled: true
    misc:
      icons:
        green-marker: search_marker_green.png

classificationBanner:
  backgroundColor: green
  classificationType: Unclassified

---
grails:
  serverURL: http://<ip>:8080
  assets:
    url: http://<ip>:8080/assets
```

* **contextPath:**, **port:**, **dataSource** Were already covered in the common [OMAR Common Install Guide](common.md).
* **omar.openlayers.baseMaps** Allows one to controll the layers added to the base maps section of openlayers on the ortho view and map view pages in the omar-app. If you do not have this field specified in the application YAML it ill use the default layers. The default layers includes **Open Street Map** layer, **Natural Earth** layer, and a **Blue Marble** layer.
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
* **classificationBanner**
 * **backgroundColor** Can be named values such as "red", "green", "yellow" , ... etc. or you can specify an exact color using the CSS styling format.  For example, if you wanted white banners you can set the value to "#FFFFFF" and if you wanted red you can also use the value "#FF0000".
 * **classificationType** This is the string displayed in the banners.  So setting to "My Secret Stuff" would print that string at the top and bottom of every page with a background color identified by the **backgroundColor** field

##Executing

To run the service on systems that use the init.d you can issue the command.

```
sudo service omar-app start
```

On systems using systemd for starting and stopping

```
sudo systemctl start omar-app
```

The service scripts calls the shell script under the directory /usr/share/omar/omar-app/omar-app.sh.   You should be able to tail the omar-app.log to see any standard output

```
tail -f /var/log/stager-app/omar-app.log
```

If all is good, then you should see a line that looks similar to the following:

```
Grails application running at http://localhost:8080 in environment: production
```

You can now verify your service with:

`curl http://192.168.2.200/omar-app/health`

which returns the health of your sytem and should have the value `{"status":"UP"}`
