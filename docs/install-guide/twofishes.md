# Welcome to Twofishes

[Twofishes](http://twofishes.net) is not owned or packaged by us and the full installation instructions can be found at their location.  Twofishes location service is used by the [OMAR/O2 UI](omar-app.md) 

For clarity we will repeat some of the instructions:

* [Download server binary](http://twofishes.net/binaries/server-assembly-0.84.9.jar) (version 0.84.9, 2015-03-10)
* [Download latest index](http://twofishes.net/indexes/revgeo/2015-03-05.zip) (updated 2015-03-05)

Create a directory location for running the twofish service and then extract the index as a subdirectory to that location.  After extracting the latest and copying the server-assembly jar file you should be able to give the following command to the index directory

```
java -jar server-assembly-<version>.jar --hfile_basepath <INDEX_DIRECTORY>
```

* **version** Corresponds to the latest version downloaded
* **INDEX_DIRECTORY** Is a subdirectory that was created during the extraction of the index file.  This is the location database that is served by the server-assembly.

