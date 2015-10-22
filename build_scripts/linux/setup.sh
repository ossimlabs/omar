#!/bin/bash

if [ ! -d "$HOME/.sdkman" ]; then
   curl -s get.sdkman.io | bash
fi
source "$HOME/.sdkman/bin/sdkman-init.sh"


if [ ! -z "$GROOVY_VERSION" ]; then
   if [ ! -d "$HOME/.sdkman/groovy/$GROOVY_VERSION" ]; then
      sdk install groovy $GROOVY_VERSION
      sdkReturnCode=$?
   if [ $antReturnCode -ne 0 ];then
       echo "GROOVY_VERSION INSTALL ERROR: sdk install for groovy $GROOVY_VERSION failed.."
       exit 1;
   fi

   fi
else
   echo "******OMAR SETUP ERROR: Environment variable GROOVY_VERSION must be set"
   exit 1
fi

if [ ! -z "$GRAILS_VERSION" ]; then
   if [ ! -d "$HOME/.sdkman/groovy/$GRAILS_VERSION" ]; then
      sdk install grails $GRAILS_VERSION
      sdkReturnCode=$?
      if [ $antReturnCode -ne 0 ];then
          echo "GRAILS_VERSION INSTALL ERROR: sdk install for grails $GRAILS_VERSION failed.."
          exit 1;
      fi
   fi
else
   echo "******OMAR SETUP ERROR: Environment variable GRAILS_VERSION must be set"
   exit 1
fi

if [ ! -z "$GRADLE_VERSION" ]; then
   if [ ! -d "$HOME/.sdkman/groovy/$GRADLE_VERSION" ]; then
      sdk install gradle $GRADLE_VERSION
      if [ $antReturnCode -ne 0 ];then
          echo "GRADLE_VERSION INSTALL ERROR: sdk install for groovy $GRADLE_VERSION failed.."
          exit 1;
      fi
   fi
else
   echo "******OMAR SETUP ERROR: Environment variable GRADLE_VERSION must be set"
   exit 1
fi
