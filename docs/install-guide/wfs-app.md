# Welcome to WFS Service

WFS Implements the [OGC WFS standard](http://www.opengeospatial.org/standards/wfs).  The Web Feature Service (WFS) supports returning feature information indexed into either the imagery tables or the video tables.

If you want to take it for a test drive please visit the [vagrant setup](https://github.com/ossimlabs/ossim-vagrant).


##Installation

We assume you have read the generalized installation procedures that shows the common configuration created for all services in the OMAR distribution found in the [OMAR Common Install Guide](common.md).  To install you should be able to issue the following yum command

```
yum install o2-wfs-app
```

The installation sets up

* Startup scripts that include /etc/init.d/wfs-app for init.d support and /usr/lib/systemd/system/wfs-app.service for systems running systemd
* Creates a system user called *omar*
* Creates log directory with user *omar* permissions under /var/log/wfs-app
* Creates a var run directory with user *omar* permissions under /var/run/wfs-app
* Adds the fat jar and shell scripts under the directory /usr/share/omar/wfs-app location

##Configuration

**Assumptions**:

* WFS Web Service IP location is 192.168.2.104 on port 8080
* Proxy server is running under the location 192.168.2.200
* Proxy pass entry `ProxyPass /wfs-app http://192.168.2.104:8080`
* Postgres database accessible via the IP and port 192.168.2.100:5432 with a database named omardb-prod.  The database can be any name you want as long as you specify it in the configuration.  If the database name or the IP and port information changes please replace in the YAML config file example

The assumptions here has the root URL for the WMS service reachabl e via the proxy by using IP http://192.168.2.200/wfs-app and this is proxied to the root IP of the wfs-app service located at http://192.168.2.104:8080. **Note: please change the IP's and ports for your setup accordingly**.

The configuration file is a yaml formatted config file.   For now create a file called wfs-app.yaml.  At the time of writting this document we do not create this config file for this is usually site specific configuration and is up to the installer to setup the document

```
vi /usr/share/omar/wfs-app/wfs-app.yml
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

      - name: opir_raster_entry
        title: opir_raster_entry
        description: ''
        keywords:
          - omar
          - opir_raster_entry
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
  serverURL: http://192.168.2.200/wfs-app
  assets:
    url: http://192.168.2.200/wfs-app/assets/
```

* **contextPath:**, **port:**, **dataSource** Was already covered in the common [OMAR Common Install Guide](common.md).
* **wfs** This entry stores both the datastore information and the feature types.  The only thing that will change in these two is the location of the postgres datastore location identified in the **datastoreParams** section by the host, port, and database.  The Feature type uses the database ans the datastore ID.  Internally this is used to query the features.

##Executing

To run the service on systems that use the init.d you can issue the command.

```
sudo service wfs-app start
```

On systems using systemd for starting and stopping

```
sudo systemctl start wfs-app
```

The service scripts calls the shell script under the directory /usr/share/omar/wfs-app/wfs-app.sh.   You should be able to tail the wfs-app.log to see any standard output

```
tail -f /var/log/wfs-app/wfs-app.log
```

If all is good, then you should see a line that looks similar to the following:

```
Grails application running at http://localhost:8080 in environment: production
```

You can now verify your service with:

`curl http://192.168.2.200/wfs-app/health`

which returns the health of your sytem and should have the value `{"status":"UP"}`
