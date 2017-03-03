# Welcome to WMS Web Service
WMS Implements the [OGC WMS standard](http://www.opengeospatial.org/standards/wms).  The WMS service uses the JAVA bindings to the OSSIM core library to perform all on-the-fly chipping of raw imagery via a WMS **GetMap** query.    

If you want to take it for a test drive please visit the [vagrant setup](https://github.com/ossimlabs/ossim-vagrant).

##Installation

We assume you have configured the yum repository described in [OMAR Common Install Guide](common.md).  To install you should be able to issue the following yum command

```
yum install o2-wms-app
```
The installation sets up

* Startup scripts that include /etc/init.d/wms-app for init.d support and /usr/lib/systemd/system/wms-app.service for systems running systemd
* Creates a system user called *omar*
* Creates log directory with user *omar* permissions under /var/log/wms-app
* Creates a var run directory with user *omar* permissions under /var/run/wms-app
* Adds the fat jar and shell scripts under the directory /usr/share/omar/wms-app location

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

* WMS Web Service IP location is 192.168.2.103 on port 8080
* Proxy server is running under the location 192.168.2.200
* Proxy pass entry `ProxyPass /wms-app http://192.168.2.103:8080`
* Postgres database accessible via the IP and port 192.168.2.100:5432 with a database named omardb-prod.  The database can be any name you want as long as you specify it in the configuration.  If the database name or the IP and port information changes please replace in the YAML config file example

The assumptions here has the root URL for the WMS service reachable via the proxy by using IP http://192.168.2.200/wms-app and this is proxied to the root IP of the wms-app service located at http://192.168.2.103:8080. **Note: please change the IP's and ports for your setup accordingly**.

The configuration file is a yaml formatted config file.   For now create a file called wms-app.yaml.  At the time of writting this document we do not create this config file for this is usually site specific configuration and is up to the installer to setup the document

```
sudo vi /usr/share/omar/wms-app/wms-app.yml
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

endpoints:
  health:
    enabled: true
---
grails:
  serverURL: http://192.168.2.200/wms-app
  assets:
    url: http://192.168.2.200/wms-app/assets/
```

The wfs definitions are used to query the database for feature information that will be used to satisfy the Chip request.

* **contextPath:**, **port:**, **dataSource** Was already covered in the common [OMAR Common Install Guide](common.md).
* **wfs** This entry stores both the datastore information and the feature types.  The only thing that will change in these two is the location of the postgres datastore location identified in the **datastoreParams** section by the host, port, and database.  The Feature type uses the database and the datastore ID.  **Note:** You can use Unix env variable for the values in this section.  For example dbhost: ${DBHOST} would use the environment variable call DBHOST. 
* **wms.styles** is used for footprint styling for the WMS footprint drawing.  You can define different color definitions and group them by a style name.   

##Elevation Configuration

This is a core OSSIM configuration but for clarity we will repeat the documentation for the elevation portion here. 

**Assumptions**:

* Environment **OSSIM_DATA** variable will be defined when the web applation starts and is pointing to a root path where elevation data and any additional geoids reside.  By default we will use /data as an example value for the **OSSIM_DATA** environment variable. Edit the file /usr/share/ossim/ossim-site-preferences

`vi /usr/share/ossim/ossim-site-preferences`


To see the default layout look for the elevation sections:

```
// One arc second post spacing dted, ~30 meters, default enabled:
elevation_manager.elevation_source0.connection_string: $(OSSIM_DATA)/elevation/dted/level2
elevation_manager.elevation_source0.enabled: true
elevation_manager.elevation_source0.extension: .dt2
elevation_manager.elevation_source0.type: dted_directory
elevation_manager.elevation_source0.min_open_cells: 25
elevation_manager.elevation_source0.max_open_cells: 50
elevation_manager.elevation_source0.memory_map_cells: false
elevation_manager.elevation_source0.geoid.type: geoid1996
elevation_manager.elevation_source0.upcase: false

// One arc second post spacing srtm, ~30 meters, default disabled:
elevation_manager.elevation_source1.connection_string: $(OSSIM_DATA)/elevation/srtm/1arc
elevation_manager.elevation_source1.enabled: false
elevation_manager.elevation_source1.type: srtm_directory
elevation_manager.elevation_source1.min_open_cells: 25
elevation_manager.elevation_source1.max_open_cells: 50
elevation_manager.elevation_source1.memory_map_cells: false
elevation_manager.elevation_source1.geoid.type: geoid1996
```

Our preferences uses a keyword list to identify name value pairs and we use "." to seperate the path and the ":" to separate the value.  You can have any number of elevation sources but it is important to note that the elevation manager will start at the first database to find an elevation post for a passed in latitude and longitude.

If the **OSSIM_DATA** is defined then we have a default tree structure that we assume stems from that root path stored in the environment variable.  In this example we have a path to dted level 2 format found under the directory $(OSSIM_DATA)/elevation/dted/level2.  Under that directory we have a dted format tree that has the organization structure as `e044/n33.dt2` where "e" stands for east and the "n" stands for north.

We also have an example SRTM 30 meter 1 arc second directory: $(OSSIM_DATA)/elevation/srtm/1arc.  This directory is required to be a 1x1 degree cells/files that adhere to the naming convention N38W113.hgt where "N" is north latitude followed by a 2 digit lat location and a "W" for west longitude followed by a 3 digit longitude value.  Likewise you can use "S" for south latitude and "E" for east logitude.

To test the elevation I would make sure the you install `yum install ossim` and run the following commands:

* **ossim-info --cg [elevation file]** Replace [elevation file] with a file in the database.  This will return the center ground coordinate That can be used to query the elevation height.

```
image0.center_ground:  (38.5,-119.5,nan,WGE)
```

Now use those values to query an elevation point to see if the database returns the proper value

* **ossim-info  --height 38.5 -119.5**

Should output something like the following:

```
Opened cell:            /data/elevation/dted/1k/w120/n38.dt1
MSL to ellipsoid delta: -24.1909999847412
Height above MSL:       1863
Height above ellipsoid: 1838.80900001526
Geoid value:            -24.1909999847412
``` 


##Executing

To run the service on systems that use the init.d you can issue the command.

```
sudo service wms-app start
```

On systems using systemd for starting and stopping

```
sudo systemctl start wms-app
```

The service scripts calls the shell script under the directory /usr/share/omar/wms-app/wms-app.sh.   You should be able to tail the wms-app.log to see any standard output

```
tail -f /var/log/wms-app/wms-app.log
```

If all is good, then you should see a line that looks similar to the following:

```
Grails application running at http://localhost:8080 in environment: production
```

You can now verify your service with:

```
curl http://192.168.2.200/wms-app/health
```

which returns the health of your sytem and should have the value `{"status":"UP"}`

#Problems

If you exceed a connection pool limit you can pass as a command line option the property **spring.datasource.maxActive=50** to increase the maximum active connecitons.  This can be passed as a proeprty argument **--spring.datasource.maxActive=50** or as a property in the YAML file:

```
spring:
  datasource:
    maxActive: 50
```
