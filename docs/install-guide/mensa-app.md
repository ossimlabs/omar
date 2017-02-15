# Welcome to the Mensuration Service

The mensuration service takes image points and the image file associated with those image points and do different forms of measurements.  The service supports calculating linear and geodetic distances based on a path and if the path also defines a polygon it will add in support for area calculation.  We also support transforming the image point to ground and, if supported by the sensor, add in an optional position quality to show the horizontal and vertical error based on the underlying surface elevation used.

##Installation

We assume you have configured the yum repository described in [OMAR Common Install Guide](common.md).  To install you should be able to issue the following yum command

```yum
yum install o2-mensa-app
```

The installation sets up

* Startup scripts that include /etc/init.d/mensa-app for init.d support and /usr/lib/systemd/system/mensa-app.service for systems running systemd
* Creates a system user called *omar*
* Creates log directory with user *omar* permissions under /var/log/mensa-app
* Creates a var run directory with user *omar* permissions under /var/run/mensa-app
* Adds the fat jar and shell scripts under the directory /usr/share/omar/mensa-app location


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

* Mensa Service IP location is 192.168.2.113 on port 8080
* Proxy server is running under the location 192.168.2.200
* Proxy pass entry `ProxyPass /mensa-app http://192.168.2.113:8080`

The assumptions here has the root URL for the Mensa service reachable via the proxy by using IP http://192.168.2.200/mensa-app and this is proxied to the root IP of the mensa-app service located at http://192.168.2.113:8080. **Note: please change the IP's and ports for your setup accordingly**.


The configuration file is a yaml formatted config file.  For now create a file called mensa-app.yaml.  At the time of writting this document we do not create this config file for this is usually site specific configuration and is up to the installer to setup the document.

```bash
sudo vi /usr/share/omar/mensa-app/mensa-app.yml
```

that contains the following settings:

```
server:
  contextPath:
  port: 8080

endpoints:
  health:
    enabled: true

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
      
---
grails:
  serverURL: http://192.168.2.200/mensa-app
  assets:
    url: http://192.168.2.200/mensa-app/assets/
```

* **endpoints** allows one to setup endpoints.  To support the additional .../health path you can enable by using the endpoints definition


##Executing

To run the service on systems that use the init.d you can issue the command.

```
sudo service mensa-app start
```

On systems using systemd for starting and stopping

```
sudo systemctl start mensa-app
```

The service scripts calls the shell script under the directory /usr/share/omar/mensa-app/mensa-app.sh.   You should be able to tail the /var/log/mensa-app.log to see any standard output

```
tail -f /var/log/mensa-app/mensa-app.log
```

If all is good, then you should see a line that looks similar to the following:

```
Grails application running at http://localhost:8080 in environment: production
```

You can now verify your service with:

```
curl http://192.168.2.200/mensa-app/health
```

which returns the health of your sytem and should have the value with items similar to `{"status":"UP"}`

##Examples

The mensa service comes with a **Swagger** api definition that is reachable with the endpoint: `http://192.168.2.200/mensa-app/api`.  Please refer to this documentation on how to use the service.
