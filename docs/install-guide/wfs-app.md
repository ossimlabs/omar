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

      - name: video_data_set
        title: video_data_set
        description: ''
        keywords:
          - omar
          - video_data_set
          - features
        datastoreId: omardb-prod

wms:
  styles:
    byFileType:
      adrg:
        filter: file_type='adrg'
        color:
          r: 50
          g: 111
          b: 111
          a: 255
      aaigrid:
        filter: file_type='aaigrid'
        color: pink
      cadrg:
        filter: file_type='cadrg'
        color:
          r: 0
          g: 255
          b: 255
          a: 255
      ccf:
        filter: file_type='ccf'
        color:
          r: 128
          g: 100
          b: 255
          a: 255
      cib:
        filter: file_type='cib'
        color:
          r: 0
          g: 128
          b: 128
          a: 255
      doqq:
        filter: file_type='doqq'
        color: purple
      dted:
        filter: file_type='dted'
        color:
          r: 0
          g: 255
          b: 0
          a: 255
      imagine_hfa:
        filter: file_type='imagine_hfa'
        color: lightGray
      jpeg:
        filter: file_type='jpeg'
        color:
          r: 255
          g: 255
          b: 0
          a: 255
      jpeg2000:
        filter: file_type='jpeg2000'
        color:
          r: 255
          g: 200
          b: 0
          a: 255
      landsat7:
        filter: file_type='landsat7'
        color:
          r: 255
          g: 0
          b: 255
          a: 255
      mrsid:
        filter: file_type='mrsid'
        color:
          r: 0
          g: 188
          b: 0
          a: 255
      nitf:
        filter: file_type='nitf'
        color:
          r: 0
          g: 0
          b: 255
          a: 255
      tiff:
        filter: file_type='tiff'
        color:
          r: 255
          g: 0
          b: 0
          a: 255
      mpeg:
        filter: file_type='mpeg'
        color:
          r: 164
          g: 254
          b: 255
          a: 255
      unspecified:
        filter: file_type='unspecified'
        color: white

---
grails:
  serverURL: http://<ip>:8080
  assets:
    url: http://<ip>:8080/assets/
```

* **contextPath:**, **port:**, **dataSource** Was already covered in the common [OMAR Common Install Guide](common.md).
* **wfs** This entry stores both the datastore information and the feature types.  The only thing that will change in these two is the location of the postgres datastore location identified in the **datastoreParams** section by the host, port, and database.  The Feature type uses the database ans the datastore ID.  Internally this is used to query the features.
* **wms.styles** is used for footprint styling for the WMS footprint drawing.  You can define different color definitions and group them by a style name.  wms footprint interface can be defined with this service to allow one to draw the footprints of the WFS holdings.

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

`curl http://localhost:8080/wfs?request=GetCapabilities`

which should return an XML document with meta-data about the service.
