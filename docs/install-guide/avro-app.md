# Welcome to the AVRO Service

This service takes an AVRO JSON payload or JSON record from and AVRO file as input and will process the file by looking for the reference URI field and downloading the File.  The schema definition is rather large but currently in the initial implementation we are only concerned with the following fields

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

The configuration file is a yaml formatted config file.   For now create a file called avro-app.yaml.  At the time of writting this document we do not create this config file for this is usually site specific configuration and is up to the installer to setup the document.

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
      url: jdbc:postgresql://<ip>:<port>/omardb-prod
      
omar:
  avro:
    sourceUriField: "S3_URI_Nitf"
    dateField: "Observation_Date"
    dateFieldFormat: "yyyyMMddHHmmss"
    imageIdField: "Image_Id"
    download:
      directory: "/data/s3"
    destination:
      type: "post"
      post:
        addRasterEndPoint: "http://192.168.2.200/stager-app/dataManager/addRaster"
        addRasterEndPointField: "filename"
```

* **sourceUriField** Is the source URI field name in the JSON Avro record.
* **dateField (optional)** Is the date field in the JSON Avro Record.  This field is optional and is used as a way to encode the **directory** for storing the image.  If this is not given then the directory suffix will be the path of the **sourceUriField**
* **dateFieldFormat** Is the format of the date field.
* **imageIdField** Is the image Id field used to identify the image
* **download** This is the download specifications
 * **directory** This is the directory prefix where the file will be downloaded.  For example,   if we have the **sourceUriField** given as http://\<IP\>/\<path\>/\<to\>/\<image\>/foo.tif and the date field content has for a value of 20090215011010  with a dateField format the directory structure will be \<yyyy\>/\<mm\>/\<dd\>/\<hh\> where **yyyy** is a 4 character year and the **mm** is the two character month and the **dd** is the two character day and the **hh** is a two character hour.  If the datefield is not specified then we use the path in the URI as a suffix to the local directory defined in the **directory** field above: /data/s3/\<path\>/\<to\>/\<image\>/foo.tif 
* **destination**
 * **type** Referes to the type we wish to specify and use.  The values can be "stdout" or "post".  If the value 'stdout' is used it will just do a println of the message. If the type is "post" then it will post the message to the service definition for the endPoint and the Field.
 * **post.addRasterEndPoint** If the destination type is **"post"** then this field needs to be specified to identify the location of the addRaster endpoint.  Typically you will be connecting this to a stager-app endpoint which will have a relative path of dataManager/addRaster.
 * **post.addRasterEndPointField** If the destination type is **"post"** then this field is needed to define the post variable used.   By default this field should be left as *"filename"*.
