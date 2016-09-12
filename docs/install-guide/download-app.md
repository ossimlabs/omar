# Welcome to Download Web Service
The Download Web Service takes multiple images or image groups specified in a JSON and return a zip archive.

##Installation

We assume you have configured the yum repository described in [OMAR Common Install Guide](common.md).  To install you should be able to issue the following yum command

```
yum install o2-download-app
```
The installation sets up

* Startup scripts that include /etc/init.d/download-app for init.d support and /usr/lib/systemd/system/download-app.service for systems running systemd
* Creates a system user called *omar*
* Creates log directory with user *omar* permissions under /var/log/download-app
* Creates a var run directory with user *omar* permissions under /var/run/download-app
* Adds the fat jar and shell scripts under the directory /usr/share/omar/download-app location

##Configuration

**Assumptions**:

* Download Web Service IP location is 192.168.2.112 on port 8080
* Proxy server is running under the location 192.168.2.200
* Proxy pass entry `ProxyPass /download-app http://192.168.2.112:8080`
* Postgres database accessible via the IP and port 192.168.2.100:5432 with a database named omardb-prod.  The database can be any name you want as long as you specify it in the configuration.  If the database name or the IP and port information changes please replace in the YAML config file example

The assumptions here has the root URL for the Download service reachable via the proxy by using IP http://192.168.2.200/download-app and this is proxied to the root IP of the download-app service located at http://192.168.2.112:8080. **Note: please change the IP's and ports for your setup accordingly**.

The configuration file is a yaml formatted config file.   For now create a file called download-app.yaml.  At the time of writting this document we do not create this config file for this is usually site specific configuration and is up to the installer to setup the document

```
sudo vi /usr/share/omar/download-app/download-app.yml
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
      password:
      dialect: 'org.hibernate.spatial.dialect.postgis.PostgisDialect'
      url: jdbc:postgresql://192.168.2.100:5432/omardb-prod

endpoints:
  health:
    enabled: true
---
grails:
  serverURL: http://192.168.2.200/download-app
  assets:
    url: http://192.168.2.200/download-app/assets/
    
```
* **contextPath:**, **port:**, **dataSource** Was already covered in the common [OMAR Common Install Guide](common.md).

##Executing
To run the service on systems that use the init.d you can issue the command.

```
sudo service download-app start
```

On systems using systemd for starting and stopping

```
sudo systemctl start download-app
```

The service scripts calls the shell script under the directory /usr/share/omar/download-app/download-app.sh.  You should be able to tail the download-app.log to see any standard output

```
tail -f /var/log/download-app/download-app.log
```

If all is good, then you should see a line that looks similar to the following:

```
Grails application running at http://localhost:8080 in environment: production
```

You can now verify your service with:

```
curl http://192.168.2.200/download-app/health
```

which returns the health of your sytem and should have the value `{"status":"UP"}`