# Welcome to the Swipe Service

The swipe service takes two image ID's as input and allows one to swipe, manual flip, or auto flip the images to see changes.  It uses existing [WFS](wfs-app.md) and [WMS](wms-app.md) services.  The WFS is used to query the holding for the datasets used in the swipe.  The WMS chips data displayed in the swipe view.


##Installation

We assume you have configured the yum repository described in [OMAR Common Install Guide](common.md).  To install you should be able to issue the following yum command

```
yum install o2-swipe-app
```
The installation sets up

* Startup scripts that include /etc/init.d/swipe-app for init.d support and /usr/lib/systemd/system/swipe-app.service for systems running systemd
* Creates a system user called *omar*
* Creates log directory with user *omar* permissions under /var/log/swipe-app
* Creates a var run directory with user *omar* permissions under /var/run/swipe-app
* Adds the fat jar and shell scripts under the directory /usr/share/omar/swipe-app location

##Configuration

**Assumptions**:

* Swipe Service IP location is 192.168.2.106 on port 8080
* Proxy server is running under the location 192.168.2.200
* Proxy pass entry `ProxyPass /swipe-app http://192.168.2.106:8080`
* Postgres database accessible via the IP and port 192.168.2.100:5432 with a database named omardb-prod.  The database can be any name you want as long as you specify it in the configuration.  If the database name or the IP and port information changes please replace in the YAML config file example

The assumptions here has the root URL for the Swipe service reachable via the proxy by using IP http://192.168.2.200/swipe-app and this is proxied to the root IP of the swipe-app service located at http://192.168.2.106:8080. **Note: please change the IP's and ports for your setup accordingly**.

The configuration file is a yaml formatted config file.   For now create a file called swipe-app.yaml.  At the time of writting this document we do not create this config file for this is usually site specific configuration and is up to the installer to setup the document

```bash
vi /usr/share/omar/swipe-app/swipe-app.yml
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

swipe:
  app:
    wfs:
      baseUrl: http://192.168.2.200/wfs-app/wfs?
    wms:
      baseUrl: http://192.168.2.200/wms-app/wms?

endpoints:
  health:
    enabled: true
---
grails:
  serverURL: http://192.168.2.200/swipe-app
  assets:
    url: http://192.168.2.200/swipe-app/assets/
```

* **contextPath:**, **port:**, **dataSource** Was already covered in the common [OMAR Common Install Guide](common.md).
* **swipe.app.wfs:** Base WFS url to query the image holdings.
* **swipe.app.wms:** Base WMS url to chip the imagery.
* **grails.serverURL** point to the root location of the swipe-app server.
* **assets url** This is the url to the assets location.  Just add the **/assets/** path to the serverURL.

If you wish to look at the swagger API documentation you can visit the api of the service by accessing the page http://192.168.2.200/swipe-app/api.


##Executing

To run the service on systems that use the init.d you can issue the command.

```
sudo service swipe-app start
```

On systems using systemd for starting and stopping

```
sudo systemctl start swipe-app
```

The service scripts calls the shell script under the directory /usr/share/omar/swipe-app/swipe-app.sh.   You should be able to tail the wmts-app.log to see any standard output

```
tail -f /var/log/swipe-app/swipe-app.log
```

If all is good, then you should see a line that looks similar to the following:

```
Grails application running at http://localhost:8080 in environment: production
```

You can now verify your service with:

`curl http://192.168.2.200/swipe-app/health`

which returns the health of your sytem and should have the value `{"status":"UP"}`
