# Welcome to OMAR suite of Web Services

Online guide can be found at [http://ossimlabs.s3-website-us-east-1.amazonaws.com/o2/docs](http://ossimlabs.s3-website-us-east-1.amazonaws.com/o2/docs).   This is a temporary location for this is the documentation generated for the dev branch distribution and is updated on any change.  In the future we will have webhosting for versioned docuementation

## Binary Distribution and Setup

For a quick example on how to configure and setup the new OMAR web services please visit the repo found here [https://github.com/ossimlabs/ossim-vagrant](https://github.com/ossimlabs/ossim-vagrant). The vagrant setup uses salt to provision each web service and is a good example on how to setup a default configuration on a CentOS distribution.

## Require environment variables
- OMAR_COMMON_PROPERTIES
- OPENSHIFT_USERNAME
- OPENSHIFT_PASSWORD
- ARTIFACTORY_USER
- ARTIFACTORY_PASSWORD

### Example:
```
  export OMAR_COMMON_PROPERTIES=~/omar-common-properties.gradle

```

## Install plugins in the following order

1. omar-core
2. omar-hibernate-spatial
3. omar-ingest-metrics
4. omar-openlayers
5. omar-geoscript
6. omar-oms
7. omar-stager

All other plugins can be installed in any order.

Install plugins by going into the plugins directory and run the command

```
 grails install

```
