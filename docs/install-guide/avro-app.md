# Welcome to the AVRO Service

This service takes an AVRO JSON payload or JSON record from an AVRO file as input and will process the file by looking for the reference URI field and downloading the File.  The schema definition is rather large but currently in the initial implementation we are only concerned with the following fields

* **S3\_URI\_Nitf** This is a JSON field defining the source URI location of the image we wish to download and process.
* **Observation_Date** This is acquisition date of the image and we use the date field as a way to create a local destination directory for the field
* **Image_Id** This is the Image Id and is used for the destination filename


We assume you have configured the yum repository described in [OMAR Common Install Guide](common.md).  To install you should be able to issue the following yum command

```yum
yum install o2-avro-app
```

The installation sets up

* Startup scripts that include /etc/init.d/avro-app for init.d support and /usr/lib/systemd/system/avro-app.service for systems running systemd
* Creates a system user called *omar*
* Creates log directory with user *omar* permissions under /var/log/avro-app
* Creates a var run directory with user *omar* permissions under /var/run/avro-app
* Adds the fat jar and shell scripts under the directory /usr/share/omar/avro-app location

##Configuration

The configuration file is a yaml formatted config file.   For now, create a file called avro-app.yaml.  At the time of writing of this document we do not create this config file for this is usually a site specific configuration and is up to the installer to setup the document.

**Assumptions**:

* AVRO Service IP location is 192.168.2.110 on port 8080
* Proxy server is running under the location 192.168.2.200
* Proxy pass entry `ProxyPass /avro-app http://192.168.2.110:8080`
* Postgres database accessible via the IP and port 192.168.2.100:5432 with a database named omardb-prod.  The database can be any name you want as long as you specify it in the configuration.  If the database name or the IP and port information changes please replace in the YAML config file example

The assumptions here has the root URL for the Swipe service reachable via the proxy by using IP http://192.168.2.200/avro-app and this is proxied to the root IP of the avro-app service located at http://192.168.2.106:8080. **Note: please change the IP's and ports for your setup accordingly**.

```bash
sudo vi /usr/share/omar/avro-app/avro-app.yml
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
  avro:
    sourceUriField: "uRL"
    dateField: "observationDateTime"
    dateFieldFormat: "yyyyMMddHHmmss"
    imageIdField: "imageId"
    jsonSubFieldPath: "Message"
    download:
      directory: "/data/s3"
      command: ""
    destination:
      type: "post"
      post:
        addRasterEndPoint: "http://192.168.2.200/stager-app/dataManager/addRaster"
        addRasterEndPointField: "filename"
        addRasterEndPointParams:
          background: "true"
          buildHistograms: "true"
          buildOverviews: "true"
          overviewCompressionType: "NONE"
          overviewType: "ossim_tiff_box"
          filename: ""
endpoints:
  health:
    enabled: false
```

* **sourceUriField** Is the source URI field name in the JSON Avro record.
* **dateField (optional)** Is the date field in the JSON Avro Record.  This field is optional and is used as a way to encode the **directory** for storing the image.  If this is not given then the directory suffix will be the path of the **sourceUriField**
* **dateFieldFormat** Is the format of the date field.  If you leave this blank "" then it will default to parsing an ISO8601 date.
* **imageIdField** Is the image Id field used to identify the image
* **jsonSubFieldPath** Allows one to specify a path separated by "." to the submessage to where all the image information resides.  For example, if you pass a Message wrapped within the SNS notification it will be a subfield of the SNS message.  This allows one to specify a path to the message to be handled.
* **download** This is the download specifications
 * **directory** This is the directory prefix where the file will be downloaded.  For example,   if we have the **sourceUriField** given as http://\<IP>/\<path>/\<to>/\<image>/foo.tif and the date field content has for a value of 20090215011010  with a dateField format the directory structure will be \<yyyy>/\<mm>/\<dd>/\<hh> where **yyyy** is a 4 character year and the **mm** is the two character month and the **dd** is the two character day and the **hh** is a two character hour.  If the datefield is not specified then we use the path in the URI as a suffix to the local directory defined in the **directory** field above: /data/s3/\<path>/\<to>/\<image>/foo.tif
 * **command** If you do not want the standard HTTP connect to be used in java then you can pass a shell command: ex. `wget -O <destination> <source>` we use where the **source** and **destination** are replaced internally with the proper values.
* **destination**
 * **type** Referes to the type we wish to specify and use.  The values can be "stdout" or "post".  If the value 'stdout' is used it will just do a println of the message. If the type is "post" then it will post the message to the service definition for the endPoint and the Field.
 * **post.addRasterEndPoint** If the destination type is **"post"** then this field needs to be specified to identify the location of the addRaster endpoint.  Typically you will be connecting this to a stager-app endpoint which will have a relative path of dataManager/addRaster.  The example URL was taken from the ossim-vagrant repo definitions.  This will need to be modified for your environment.
 * **post.addRasterEndPointParams** This is used as the post parameters to the URL given by the value **post.addRasterEndPoint** We support modifying the default action being passed and you can specify **background**, **buildHistograms**, **buildOverviews** flags.  The **background** tells the stager to perform the staging as a background process.  If this flag is false it will do the staging inline to the endpoint call.  You can also specify the parameters **overviewCompressionType** which can be of values "NONE","JPEG","PACKBITS", or "DEFLATE" and also the paramter **overviewType** where the value can be "ossim_tiff_box", "ossim_tiff_nearest", or "ossim_kakadu_nitf_j2k".
 * **post.addRasterEndPointField** If the destination type is **"post"** then this field is needed to define the post variable used for the filename.   By default this field should be left as *"filename"*.  It will add the filename value to the addRasterEndPointParams.

##Executing

To run the service on systems that use the init.d you can issue the command.

```
sudo service avro-app start
```

On systems using systemd for starting and stopping

```
sudo systemctl start avro-app
```

The service scripts calls the shell script under the directory /usr/share/omar/avro-app/avro-app.sh.   You should be able to tail the avro-app.log to see any standard output

```
tail -f /var/log/avro-app/avro-app.log
```

If all is good, then you should see a line that looks similar to the following:

```
Grails application running at http://localhost:8080 in environment: production
```

You can now verify your service with:

`curl http://192.168.2.200/avro-app/health`

which returns the health of your sytem and should have the value `{"status":"UP"}`
