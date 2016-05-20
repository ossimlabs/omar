# Welcome to WMS Web Service
WMS Implements the [OGC WMS standard](http://www.opengeospatial.org/standards/wms).  The WMS service uses the JAVA bindings to the OSSIM core library to perform all on-the-fly chipping of raw imagery via a WMS **GetMap** query.    

If you want to take it for a test drive please visit the [vagrant setup](https://github.com/ossimlabs/ossim-vagrant).

##Installation

We assume you have configured the yum repository described in [OMAR Readme Guide](../README.md).  To install you should be able to issue the following yum command

```yum
yum install o2-wms-app
```
The installation sets up

* Startup scripts that include /etc/init.d/wms-app for init.d support and /usr/lib/systemd/system/wms-app.service for systems running systemd
* Creates a system user called *omar*
* Creates log directory with user *omar* permissions under /var/log/wms-app
* Creates a var run directory with user *omar* permissions under /var/run/wms-app
* Adds the fat jar and shell scripts under the directory /usr/share/omar/wms-app location

##Configuration

The configuration file is a yaml formatted config file.   For now create a file called wmts-app.yaml.  At the time of writting this document we do not create this config file for this is usually site specific configuration and is up to the installer to setup the document

```bash
vi /usr/share/omar/wms-app/wms-app.yml
```
 that contains the following settings:

```yaml
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


      bySensorType:
          'ACES_YOGI-HRI1':
            filter: mission_id='ACES_YOGI-HRI1'
            color:
              r: 255
              g: 0,
              b: 0
              a: 255

     byVideoType:
          mpeg:
            filter: filename like '%mpg'
            color:
              r: 255
              g: 0
              b: 0
              a: 255
---
grails:
  serverURL: http://192.168.2.200/wms-app
  assets:
    url: http://192.168.2.200/wms-app/assets/
```
The wfs definitions are used to query the database for feature information that will be used to satisfy the Chip request.  There are styles

* **contextPath:**, **port:**, **dataSource** Was already covered in the common [OMAR Readme Guide](../README.md).
* **wfs** This entry stores both the datastore information and the feature types.  The only thing that will change in these two is the location of the postgres datastore location identified in the **datastoreParams** section by the host, port, and database.  The Feature type uses the database ans the datastore ID.
* **wms.styles** is used for footprint styling for the WMS footprint drawing.  You can define different color definitions and group them by a style name.   
