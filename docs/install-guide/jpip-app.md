# Welcome to JPIP Web Service

The JPIP web service is an interface to convert imagery so the jpip-server can stream to a client.  When interfacing into the JPIP web application you can post messages to request a URL. The result that is returned is a JSON formatted string that has the URL and the state at which the URL is in.  For example.  If a JPIP stream is requested on a given image and if the image does not have a JPIP stream associated with it yet then it will submit the image for background processing and return a STATUS.  

##Installation
We assume you have read the generalized installation procedures that shows the common configuration created for all services in the OMAR distribution found in the [OMAR Common Install Guide](common.md).  To install you should be able to issue the following yum command

```
yum install o2-jpip-app
```

The installation sets up

* Startup scripts that include /etc/init.d/jpip-app for init.d support and /usr/lib/systemd/system/jpip-app.service for systems running systemd
* Creates a system user called *omar*
* Creates log directory with user *omar* permissions under /var/log/jpip-app
* Creates a var run directory with user *omar* permissions under /var/run/jpip-app
* Adds the fat jar and shell scripts under the directory /usr/share/omar/jpip-app location

##Configuration

**Assumptions**:

* JPIP Service IP location is 192.168.2.107 on port 8080
* Proxy server is running under the location 192.168.2.200
* Proxy pass entry `ProxyPass /swipe-app http://192.168.2.107:8080`
* Postgres database accessible via the IP and port 192.168.2.100:5432 with a database named omardb-prod.  The database can be any name you want as long as you specify it in the configuration.  If the database name or the IP and port information changes please replace in the YAML config file example

The assumptions here has the root URL for the JPIP service reachable via the proxy by using IP http://192.168.2.200/jpip-app and this is proxied to the root IP of the jpip-app service located at http://192.168.2.107:8080. **Note: please change the IP's and ports for your setup accordingly**.

The configuration file is a yaml formatted config file.   For now create a file called wmts-app.yaml.  At the time of writting this document we do not create this config file for this is usually site specific configuration and is up to the installer to setup the document

```
vi /usr/share/omar/wmts-app/wmts-app.yml
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

omar:
  jpip:
    server:
      cache: <jpip_cache_dir>
      ip:    <ip>
      url:   jpip://<ip>:<port>

---
grails:
  serverURL: http://192.168.2.200/jpip-app
  assets:
    url: http://192.168.2.200/jpip-app/assets/
```

* **port:** For the server.port you can set the port that the web application will come up on.  By default the port is 8080.  If you are going through a proxy then ignore the port and use the proxy path to the service.
* **contextPath:** For most installation you will set server.contextPath to empty and proxy the request via a httpd proxy to the port 8080.  If a context path is used then the services access point is of the form: http://***url***:***port***/***contextPath***
* **dataSource** Was already covered in the common [OMAR Common Install Guide](common.md).
* **omar.jpip.server.cache:** This is the location where images are written when they are converted to the input format used by the jpip-server.
* **omar.jpip.server.ip:** Ip of the jpip-server location 
* **omar.jpip.server.url** Base url used as a prefix for accessing the converted file over JPIP protocol


##Executing

To run the service on systems that use the init.d you can issue the command.

```
sudo service jpip-app start
```

On systems using systemd for starting and stopping

```
sudo systemctl start jpip-app
```

The service scripts calls the shell script under the directory /usr/share/omar/jpip-app/jpip-app.sh.   You should be able to tail the jpip-app.log to see any standard output

```
tail -f /var/log/stager-app/jpip-app.log
```

If all is good, then you should see a line that looks similar to the following:

```
Grails application running at http://localhost:8080 in environment: production
```

You can now verify your service with:

`curl http://192.168.2.200/jpip-app/health`

which returns the health of your sytem and should have the value `{"status":"UP"}`


##Examples

Assume the server has access to the image path called  **/data/sanfran/foo.ccf** If we were to submit a request:

```
wget http://192.168.2.200/jpip-app/jpip/createStream?filename=/data/sanfran/foo.ccf&entry=0&projCode=4326

```

* **filename** This is the filename of the image you wish to have a JPIP stream created for. If one is already created then it will return a URL link with the value of *FINISHED*
* **entry** For multi entry Images you need to specify the entry number. We should default to entry 0

* **projCode** chip=image space, geo-scaled - origin lat of true scale = image center, 4326 for geographic, 3857 for google mercator.


The result of a call to the web service can be of severaly status types but on initial creation you should see a status of *READY*

```
{
  "url": "jpip://10.0.10.100:8080/e5d73979-e76b-40a8-9f92-1f253d377387.jp2",
  "status": "READY"
}
```
* **url** The url of the jpip streaming server for accessing the image.
* **status** Can be on of *READY*, *RUNNING*, *PAUSED*, *CANCELED*, *FINISHED*, and *FAILED*.  Note, *READY* here does not mean that the URL is ready, but instead means that it is on the *READY* queue for the background jobs to stage. 

You can call the URL again, with the same parameters, and you should get a different status if it has been started by the job:

```
wget http://192.168.2.200/jpip-app/jpip/createStream?filename=/data/sanfran/foo.ccf&entry=0&projCode=4326
```

Result:

```
{
  "url": "jpip://10.0.10.100:8080/e5d73979-e76b-40a8-9f92-1f253d377387.jp2",
  "status": "FINISHED"
}
```

When you get the **FINISHED** status this means that the URL returned can now be accessed as a JPIP stream.

