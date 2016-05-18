#!/bin/bash
pushd `dirname $0` >/dev/null
export SCRIPT_DIR=`pwd -P`
popd >/dev/null

pushd $SCRIPT_DIR/../../.. >/dev/null
export ROOT_DIR=$PWD
popd >/dev/null

. $SCRIPT_DIR/env.sh

for app in ${O2_APPS[@]} ; do 
   echo "INSTALLING $app"
   pushd $OMAR_DEV_HOME/apps/$app/build/libs/ >/dev/null
   for artifact in  `ls *.jar` ; do
      install -p -m644 -D $artifact ${OMAR_INSTALL_PREFIX}/share/omar/$app/$artifact
      if [ $? -ne 0 ];then
          echo "INSTALL ERROR: $app failed to install..."
          exit 1
      fi   
   done
   popd >/dev/null

   pushd $OMAR_DEV_HOME/support/linux >/dev/null

   install -d -m755 ${OMAR_INSTALL_PREFIX}/etc/init.d
   install -d -m755 ${OMAR_INSTALL_PREFIX}/usr/lib/systemd/system
   install -d -m755 ${OMAR_INSTALL_PREFIX}/share/omar/$app/service-templates
   sed -e "s/{{program_name}}/${app}/g"  -e "s/{{program_user}}/omar/g" -e "s/{{program_group}}/omar/g" < service-wrapper-systemd-template >${OMAR_INSTALL_PREFIX}/lib/systemd/system/${app}.service 
   sed -e "s/{{program_name}}/${app}/g"  -e "s/{{program_user}}/omar/g" -e "s/{{program_group}}/omar/g" < service-wrapper-initd-template >${OMAR_INSTALL_PREFIX}/etc/init.d/${app} 
   sed -e "s/{{program_name}}/${app}/g"  < o2-shell-template >${OMAR_INSTALL_PREFIX}/share/omar/$app/${app}.sh 
   popd > /dev/null
done
