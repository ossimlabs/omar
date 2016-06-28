# Welcome to the Swipe Service

The swipe service takes two image ID's as input and allows one to swipe, manual flip, or auto flip the images to see changes.  It uses existing [WFS](wfs-app.md) and [WMS](wms-app.md) services.  The WFS is used to query the holding for the datasets used in the swipe.  The WMS chips data displayed in the swipe view.


##Installation
We assume you have configured the yum repository described in [OMAR Common Install Guide](common.md).  To install you should be able to issue the following yum command

```yum
yum install o2-swipe-app
```
The installation sets up

* Startup scripts that include /etc/init.d/swipe-app for init.d support and /usr/lib/systemd/system/swipe-app.service for systems running systemd
* Creates a system user called *omar*
* Creates log directory with user *omar* permissions under /var/log/swipe-app
* Creates a var run directory with user *omar* permissions under /var/run/swipe-app
* Adds the fat jar and shell scripts under the directory /usr/share/omar/swipe-app location

##Configuration

The configuration file is a yaml formatted config file.   For now create a file called swipe-app.yaml.  At the time of writting this document we do not create this config file for this is usually site specific configuration and is up to the installer to setup the document

```bash
vi /usr/share/omar/stager-app/stager-app.yml
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

swipe:
  app:
    wfs:
      baseUrl: http://<ip>/wfs-app/wfs?
    wms:
      baseUrl: http://<ip>/wms-app/wms?
---
grails:
  serverURL: http://<ip>:8080/
  assets:
    url: http://<ip>:8080/assets/
```

* **contextPath:**, **port:**, **dataSource** Was already covered in the common [OMAR Common Install Guide](common.md).
* **swipe.app.wfs:** Base WFS url to query the image holdings.
* **swipe.app.wms:** Base WMS url to chip the imagery.
* **grails.serverURL** point to the root location of the wmts-app server. This example in the template above points to service via a proxy definition.  If you go directly to the service via 8080 then you can drop the proxy prefix /stager-app
* **assets url** This is the url to the assets location.  Just add the **/assets/** path to the serverURL.

If you wish to look at the swagger API documentation you can visit the api of the service by accessing the page http://\<ip>/\<proxy path>/api.


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
tail -f /var/log/wmts-app/swipe-app.log
```

If all is good, then you should see a line that looks similar to the following:

```
Grails application running at http://localhost:8080 in environment: production
```

You can now verify your service with:

`curl http://localhost:8080/wms?request=GetCapabilities`

which should return an XML document with meta-data about the service.
