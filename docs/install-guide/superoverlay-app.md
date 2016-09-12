# Welcome to the Superoverlay Service

The Superoverlay service creates a KML overlay for any image found in the indexed database.  The service calls the WMS chipping servcie and allows one to create KML lod nodes to partition the dataset into a hierarchical LOD grid.

##Installation
We assume you have configured the yum repository described in [OMAR Common Install Guide](common.md).  To install you should be able to issue the following yum command

```yum
yum install o2-superoverlay-app
```
The installation sets up

* Startup scripts that include /etc/init.d/superoverlay-app for init.d support and /usr/lib/systemd/system/swipe-app.service for systems running systemd
* Creates a system user called *omar*
* Creates log directory with user *omar* permissions under /var/log/superoverlay-app
* Creates a var run directory with user *omar* permissions under /var/run/superoverlay-app
* Adds the fat jar and shell scripts under the directory /usr/share/omar/superoverlay-app location

##Configuration

**Assumptions**:

* Super Overlay Service IP location is 192.168.2.105 on port 8080
* Proxy server is running under the location 192.168.2.200
* Proxy pass entry `ProxyPass /superoverlay-app http://192.168.2.105:8080`
* Postgres database accessible via the IP and port 192.168.2.100:5432 with a database named omardb-prod.  The database can be any name you want as long as you specify it in the configuration.  If the database name or the IP and port information changes please replace in the YAML config file example

The assumptions here has the root URL for the Super Overlay service reachable via the proxy by using IP http://192.168.2.200/superoverlay-app and this is proxied to the root IP of the superoverlay-app service located at http://192.168.2.105:8080. **Note: please change the IP's and ports for your setup accordingly**.

The configuration file is a yaml formatted config file.   For now create a file called superoverlay-app.yaml.  At the time of writting this document we do not create this config file for this is usually site specific configuration and is up to the installer to setup the document

```bash
vi /usr/share/omar/superoverlay-app/superoverlay-app.yml
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
      url: jdbc:postgresql://192.168.2.100:5432/omardb-prod

wfs:
  featureTypeNamespaces:
    - prefix: omar
      uri: http://omar.ossim.org

  datastores:
    - namespaceId: omar
      datastoreId: omardb-prod
      datastoreParams:
        dbtype: postgis
        host: 192.168.2.100
        port: '5432'
        database: omardb-prod
        user: postgres
        passwd: postgres
        'Expose primary keys': 'true'
        namespace: http://omar.ossim.org
  featureTypes:
    - name: raster_entry
      title: raster_entry
      description: ''
      keywords:
        - omar
        - raster_entry
        - features
      datastoreId: omardb-prod

    - name: video_data_set
      title: video_data_set
      description: ''
      keywords:
        - omar
        - video_data_set
        - features
      datastoreId: omardb-prod

omar:
  superOverlay:
    wmsUrl: http://192.168.2.200/wms-app/wms/getMap

endpoints:
  health:
    enabled: true

---
grails:
  serverURL: http://192.168.2.200/superoverlay-app
  assets:
    url: http://192.168.2.200/superoverlay-app/assets/
```

* **contextPath:**, **port:**, **dataSource** Were already covered in the common [OMAR Common Install Guide](common.md).
* **wfs** The WFS entry here is to define the location of where the feature database resides for the feature information.  In the future we will have the superoverlay service call the WFS service. For now we will leave the definition here.  The entry defines the dtabase location id for where the video and raster tables reside.
* **omar.superOverlay.wmsURL** Specify the location of the *GetMap* call that satisfies the WMS chipping interface.
* **grails.serverURL** point to the root location of the superoverlay-app server. This example in the template above points to service via a proxy definition.  If you go directly to the service via 8080 then you can drop the proxy prefix /superoverlay-app
* **assets url** This is the url to the assets location.  Just add the **/assets/** path to the serverURL.

If you wish to look at the swagger API documentation you can visit the api of the service by accessing the page http://192.168.2.200/superoverlay-app/api.

##Executing

To run the service on systems that use the init.d you can issue the command.

```
sudo service superoverlay-app start
```

On systems using systemd for starting and stopping

```
sudo systemctl start superoverlay-app
```

The service scripts calls the shell script under the directory /usr/share/omar/superoverlay-app/superoverlay-app.sh.   You should be able to tail the superoverlay-app.log to see any standard output

```
tail -f /var/log/avro-app/superoverlay-app.log
```

If all is good, then you should see a line that looks similar to the following:

```
Grails application running at http://localhost:8080 in environment: production
```

You can now verify your service with:

```
curl http://192.168.2.200/superoverlay-app/health
```

which returns the health of your sytem and should have the value `{"status":"UP"}`
