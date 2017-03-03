# Welcome to WCS Web Service

WCS Service Implements the [OGC WCS standard](http://www.opengeospatial.org/standards/wcs).  

##Installation

We assume you have configured the yum repository described in [OMAR Common Install Guide](common.md).  To install you should be able to issue the following yum command

```
yum install o2-wcs-app
```
The installation sets up

* Startup scripts that include /etc/init.d/wcs-app for init.d support and /usr/lib/systemd/system/wcs-app.service for systems running systemd
* Creates a system user called *omar*
* Creates log directory with user *omar* permissions under /var/log/wcs-app
* Creates a var run directory with user *omar* permissions under /var/run/wcs-app
* Adds the fat jar and shell scripts under the directory /usr/share/omar/wcs-app location

Because this library accesses imagery for chipping you might want to consider adding additional plugins to handle J2K imagery or other types of data.  

##Dependencies

This plugin uses JNI bindings to the ossim core C++ engine.  By default it should install the JNI C++ bindings but will not install any optional plugins for handling additional file formats.  Here is a suggested list of plugins to add to your installation:

**Sugested Packages**

```
yum install ossim
yum install ossim-kakadu-plugin
yum install ossim-jpeg12-plugin
yum install ossim-sqlite-plugin
yum install ossim-hdf5-plugin
yum install ossim-geopdf-plugin
yum install ossim-png-plugin
```

**Additional**

```
sudo yum install ossim-gdal-plugin.x86_64
```

and if Available:

```
sudo yum -y install ossim-ntm-plugin
```

##Configuration

**Assumptions**:

* WCS Web Service IP location is 192.168.2.111 on port 8080
* Proxy server is running under the location 192.168.2.200
* Proxy pass entry `ProxyPass /wcs-app http://192.168.2.111:8080`
* Postgres database accessible via the IP and port 192.168.2.100:5432 with a database named omardb-prod.  The database can be any name you want as long as you specify it in the configuration.  If the database name or the IP and port information changes please replace in the YAML config file example

The assumptions here has the root URL for the WCS service reachable via the proxy by using IP http://192.168.2.200/wcs-app and this is proxied to the root IP of the wcs-app service located at http://192.168.2.111:8080. **Note: please change the IP's and ports for your setup accordingly**.

The configuration file is a yaml formatted config file.   For now create a file called wcs-app.yaml.  At the time of writting this document we do not create this config file for this is usually site specific configuration and is up to the installer to setup the document

```
sudo vi /usr/share/omar/wcs-app/wcs-app.yml
``` 
that contains the following settings

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

endpoints:
  health:
    enabled: true
---
grails:
  serverURL: https://192.168.2.200/wcs-app
  assets:
    url: https://192.168.2.200/wcs-app/assets/
```
* **contextPath:**, **port:**, **dataSource** Was already covered in the common [OMAR Common Install Guide](common.md).
* **wfs** This entry stores both the datastore information and the feature types.  The only thing that will change in these two is the location of the postgres datastore location identified in the **datastoreParams** section by the host, port, and database.  The Feature type uses the database and the datastore ID.

##Executing

To run the service on systems that use the init.d you can issue the command.

```
sudo service wcs-app start
```

On systems using systemd for starting and stopping

```
sudo systemctl start wcs-app
```

The service scripts calls the shell script under the directory /usr/share/omar/wcs-app/wcs-app.sh.   You should be able to tail the wcs-app.log to see any standard output

```
tail -f /var/log/wcs-app/wcs-app.log
```

If all is good, then you should see a line that looks similar to the following:

```
Grails application running at http://localhost:8080 in environment: production
```

You can now verify your service with:

```
curl http://192.168.2.200/wcs-app/health
```

which returns the health of your sytem and should have the value `{"status":"UP"}`
