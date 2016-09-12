# Welcome to the SQS Service

The SQS service allows one to Read from an Amazon **S**imple **Q**ueueing **S**ystem (**SQS**) and redirect the messages either to standard out or to another URL endpoint by issuing a post.  We use a background job to poll the SQS for any pending messages and will only delete the messages from the SQS if the message was handled properly.  If the message was not handled properly it will remain on the queue and be reset automatically based on the queue properties.

##Installation

We assume you have configured the yum repository described in [OMAR Common Install Guide](common.md).  To install you should be able to issue the following yum command

```yum
yum install o2-sqs-app
```

The installation sets up

* Startup scripts that include /etc/init.d/sqs-app for init.d support and /usr/lib/systemd/system/sqs-app.service for systems running systemd
* Creates a system user called *omar*
* Creates log directory with user *omar* permissions under /var/log/sqs-app
* Creates a var run directory with user *omar* permissions under /var/run/sqs-app
* Adds the fat jar and shell scripts under the directory /usr/share/omar/sqs-app location


##Configuration

**Assumptions**:

* SQS Service IP location is 192.168.2.109 on port 8080
* Proxy server is running under the location 192.168.2.200
* Proxy pass entry `ProxyPass /stager-app http://192.168.2.109:8080`

The assumptions here has the root URL for the Stager service reachable via the proxy by using IP http://192.168.2.200/sqs-app and this is proxied to the root IP of the sqs-app service located at http://192.168.2.109:8080. **Note: please change the IP's and ports for your setup accordingly**.


The configuration file is a yaml formatted config file.   For now create a file called sqs-app.yaml.  At the time of writting this document we do not create this config file for this is usually site specific configuration and is up to the installer to setup the document.

```bash
sudo vi /usr/share/omar/sqs-app/sqs-app.yml
```

that contains the following settings:

```
server:
  contextPath:
  port: 8080

omar:
  sqs:
    reader:
      queue: "https://<AmazonDNS>/<path_to_queue>"
      waitTimeSeconds: 20
      maxNumberOfMessages: 1
      pollingIntervalSeconds: 10
      destination:
        type: "post"
        post:
            urlEndPoint: "http://192.168.2.200/avro-app/avro/addMessage"

endpoints:
  health:
    enabled: true

---
grails:
  serverURL: http://192.168.2.200/sqs-app
  assets:
    url: http://192.168.2.200/sqs-app/assets/
```

* **queue** defines an Amazon SQS endpoint for access.
* **waitTimeSecond** This value can be between 1 and 20 and can not exceed 20 or you get errors and the service will not start proeprly.  This value is used by the AWS API to wait for a maximum time for a message to occur before returning.
* **maxNumberOfMessages** Value can only be between 1 and 10.  Any other value will give errors and the service will not start properly.  This defines the maximum number of messages to pop off the queue during a single read request to the service.
* **pollingIntervalSeconds** this can be any value and defines the number of second to *SLEEP* the background process between each call to the read request.  By default it will keep calling the read request until no messages are found.  After no messages are found the backgroun process will then *SLEEP* for **pollingIntervalSeconds**.
* **destination.type** This value can be either "post" or "stdout".   If the value is a post then it expects the **post** entry to be defined.  If the type is stdout then all message payload/message body are printed to standard out.
* **destination.post.urlEndPoint** Defines the url to post the message to.  The example here was taken from the ossim-vagrant implementation

## AWS Credentials

Currently we assume that the credentials are put in the proper location.  For testing we added a file to the home account of the user the sqs-app process is running as.  

```
vi ~/.aws/credentials
```

with contents

```
[default]
aws_access_key_id=
aws_secret_access_key=
```

Where you replace **aws\_access\_key\_id** and **aws\_secret\_access\_key** with your AWS credentials.


In production you will probably already have machine based roles and this technique should only be used when testing from a local laptop and connecting to the AWS.

##Executing

To run the service on systems that use the init.d you can issue the command.

```
sudo service sqs-app start
```

On systems using systemd for starting and stopping

```
sudo systemctl start sqs-app
```

The service scripts calls the shell script under the directory /usr/share/omar/aws-app/aws-app.sh.   You should be able to tail the aws-app.log to see any standard output

```
tail -f /var/log/stager-app/sqs-app.log
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
