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