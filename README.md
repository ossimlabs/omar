# Welcome to OMAR suite of Web Services

Online guide can be found at [http://ossimlabs.s3-website-us-east-1.amazonaws.com/o2/docs](http://ossimlabs.s3-website-us-east-1.amazonaws.com/o2/docs).   This is a temporary location for this is the documentation generated for the dev branch distribution and is updated on any change.  In the future we will have webhosting for versioned docuementation

## Binary Distribution and Setup

For a quick example on how to configure and setup the new OMAR web services please visit the repo found here [https://github.com/ossimlabs/ossim-vagrant](https://github.com/ossimlabs/ossim-vagrant). The vagrant setup uses salt to provision each web service and is a good example on how to setup a default configuration on a CentOS distribution.

## Environment variables required to push jars to Artifactory

- OSSIM_MAVEN_PROXY
- OSSIM_BUILD_TAG
- ARTIFACTORY_CONTEXT_URL
- ARTIFACTORY_USER
- ARTIFACTORY_PASSWORD

### Example:
```
  export OSSIM_MAVEN_PROXY=https://artifacts.radiantbluecloud.com/artifactory/ossim-deps
  export ARTIFACTORY_CONTEXT_URL=https://artifacts.radiantbluecloud.com/artifactory
  export ARTIFACTORY_USER=<artifactory user>
  export ARTIFACTORY_PASSWORD=<artifactory password>
```

## Build Order for plugins

- omar-core
- omar-hibernate-spatial
- omar-ingest-metrics
- omar-openlayers
- omar-geoscript
- omar-oms
- omar-stager

Build order for other plugins does not matter.
