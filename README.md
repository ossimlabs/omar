# ARCHIVE ARCHIVE ARCHIVE
# ARCHIVE ARCHIVE ARCHIVE
# ARCHIVE ARCHIVE ARCHIVE
# ARCHIVE ARCHIVE ARCHIVE
This repository is in place for archival purposes only. This source is no longer maintained. All supported OMAR applications have been migrated to individual repositories. A good place to start would be:

https://github.com/ossimlabs/ossim <br />
https://github.com/ossimlabs/omar-oms <br />
https://github.com/ossimlabs/omar-geoscript <br />
https://github.com/ossimlabs/omar-wfs <br />
https://github.com/ossimlabs/omar-wms <br />

# ARCHIVE ARCHIVE ARCHIVE
# ARCHIVE ARCHIVE ARCHIVE
# ARCHIVE ARCHIVE ARCHIVE
# ARCHIVE ARCHIVE ARCHIVE









# Welcome to OMAR suite of Web Services

Online guide can be found at [https://omar-dev.ossim.io/omar-docs](https://omar-dev.ossim.io/omar-docs).   This is a temporary location for this is the documentation generated for the dev branch distribution and is updated on any change.  In the future we will have webhosting for versioned docuementation

## Binary Distribution and Setup

For a quick example on how to configure and setup the new OMAR web services please visit the repo found here [https://github.com/ossimlabs/ossim-vagrant](https://github.com/ossimlabs/ossim-vagrant). The vagrant setup uses salt to provision each web service and is a good example on how to setup a default configuration on a CentOS distribution.

Git clone the omar-common repo.
```
  git clone https://github.com/ossimlabs/omar-common.git
```

## Required environment variable
- OMAR_COMMON_PROPERTIES

## Optional environment variables
### required by Jenkins or a local Artifactory or a local Openshift

- OPENSHIFT_USERNAME
- OPENSHIFT_PASSWORD
- ARTIFACTORY_USER
- ARTIFACTORY_PASSWORD

### Example:
```
  export OMAR_COMMON_PROPERTIES=~/omar-common/omar-common-properties.gradle

```

## Install plugins in the following order

1. omar-core
2. omar-hibernate-spatial
3. omar-ingest-metrics
4. omar-openlayers
5. omar-geoscript
6. omar-oms
7. omar-stager
8. omar-wms

All other plugins can be installed in any order.

Install plugins by going into the plugin's directory and run the command

```
 ./gradlew clean install

```
