# Welcome to the Stager Service

The stager service currently indexes the data into the OMAR system.  If you want to take it for a test drive please visit the [vagrant setup](https://github.com/ossimlabs/ossim-vagrant).

##Installation
We assume you have configured the yum repository described in [OMAR Common Install Guide](common.md).  To install you should be able to issue the following yum command

```yum
yum install o2-stager-app
```
The installation sets up

* Startup scripts that include /etc/init.d/stager-app for init.d support and /usr/lib/systemd/system/stager-app.service for systems running systemd
* Creates a system user called *omar*
* Creates log directory with user *omar* permissions under /var/log/stager-app
* Creates a var run directory with user *omar* permissions under /var/run/stager-app
* Adds the fat jar and shell scripts under the directory /usr/share/omar/stager-app location

##Dependencies

This plugin uses JNI bindings to the ossim core C++ engine.  By default it should install the JNI C++ bindings but will not install any optional plugins for handling additional file formats.  Here is a suggested list of plugins to add to your installation:

**Sugested Packages**

```
yum install ossim
yum install ossim-kakadu-plugin
yum install ossim-sqlite-plugin
yum install ossim-hdf5-plugin
yum install ossim-geopdf-plugin
yum install ossim-png-plugin
```

**Additional**

```
yum install ossim-gdal-plugin.x86_64
```

##Configuration

**Assumptions**:

* Stager Service IP location is 192.168.2.102 on port 8080
* Proxy server is running under the location 192.168.2.200
* Proxy pass entry `ProxyPass /stager-app http://192.168.2.102:8080`
* Postgres database accessible via the IP and port 192.168.2.100:5432 with a database named omardb-prod.  The database can be any name you want as long as you specify it in the configuration.  If the database name or the IP and port information changes please replace in the YAML config file example

The assumptions here has the root URL for the Stager service reachable via the proxy by using IP http://192.168.2.200/stager-app and this is proxied to the root IP of the stager-app service located at http://192.168.2.102:8080. **Note: please change the IP's and ports for your setup accordingly**.

The configuration file is a yaml formatted config file.   For now create a file called swipe-app.yaml.  At the time of writting this document we do not create this config file for this is usually site specific configuration and is up to the installer to setup the document

`vi /usr/share/omar/stager-app/stager-app.yml`

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

quartz:
  jdbcStore: false
  threadPool:
    threadCount: 4

---
grails:
  serverURL: http://192.168.2.200/stager-app
  assets:
    url: http://192.168.2.200/stager-app/assets/
```

* **contextPath:**, **port:**, **dataSource** Was already covered in the common [OMAR Common Install Guide](common.md).
* **quartz.jdbcStore:** This service supports background jobs using the quartz framework.  Just fix this to not use the jdbcStore.   For now the requests are not persistent.
* **quarts.threadPool.threadCount** Quartz allows one to adjust the number of concurrent threads running.  Here we default to 4 threads.  This will allow 4 concurrent stagers to run for this service.
* **grails.serverURL** point to the root location of the stager-app server. This example in the template above points to service via a proxy definition.  If you go directly to the service via 8080 then you can drop the proxy prefix /stager-app
* **assets url** This is the url to the assets location.  Just add the **/assets/** path to the serverURL.

If you wish to look at the swagger API documentation you can visit the api of the service by accessing the page http://192.168.2.200/stager-app/api.


##Executing

To run the service on systems that use the init.d you can issue the command.

```
sudo service stager-app start
```

On systems using systemd for starting and stopping

```
sudo systemctl start stager-app
```

The service scripts calls the shell script under the directory /usr/share/omar/stager-app/stager-app.sh.   You should be able to tail the stager-app.log to see any standard output

```
tail -f /var/log/stager-app/stager-app.log
```

If all is good, then you should see a line that looks similar to the following:

```
Grails application running at http://localhost:8080 in environment: production
```

You can now verify your service with:

```
curl http://192.168.2.200/stager-app/health
```

which returns the health of your sytem and should have the value `{"status":"UP"}`

## Examples

To add a raster file for indexing make sure you are on the same NFS mount path.  In this example we will assume that the endpoint URL is located: http://192.168.2.200/stager-app/dataManager/addRaster.   To add a raster file using curl:

```
curl -d "filename=<path of file>" "http://192.168.2.200/stager-app/dataManager/addRaster"
```

and to remove the raster

```
curl -d "filename=<path of file>" "http://192.168.2.200/stager-app/dataManager/removeRaster"
```
