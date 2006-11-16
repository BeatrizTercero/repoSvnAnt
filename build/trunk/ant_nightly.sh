#!/bin/sh
#==============================================================================
# Ant, Antlibs, Sandbox Nightly Build
#
# Executes "svn up" and then "ant clean dist" for all of the
# components in $nightly_list_location/nightly_proper_ant_list.txt,
# "maven clean dist" for those in 
# $nightly_list_location/nightly_proper_maven_list.txt and
# mvn assembly:assembly -DdescriptorId=bin (then src) for components in
# $nightly_list_location/nightly_proper_maven2_list.txt
# Similarly for sandbox components from the *_sandbox_* lists.
#
# Uses scp to upload resulting .zip and .tar.gz files to
# $deploy_user@$deploy_host:$deploy_location/commons-$component
#
# Deploys dated snapshot jars for the maven 1 components to
# $maven_snapshot_host:$maven_snapshot_directory and uses
# "mvn deploy" to deploy maven 2 jars to the snapshot repo configured
# in the pom.
#
# Names of distro files take the form 
# commons-$component-$time_stamp-src (source)
# commons-$component-$time_stamp (binaries)
#
# (Over-)writes a log for each component build to $log_location/$component.log
#
# Assumes $proper_root points to a checkout of commons proper trunks
# and similarly for $sandbox_root
#
# Sources configuration from a file specified on the command line:
#
# Usage: commons_nightly.sh <config>
#
# See vmbuild.conf for sample config and documentation of config parameters.
#===========================================================================

## TODO: Create an svnup script that begins by running svn cleanup, then 
##       uses svn status to svn revert any changed files; and rm's any ? mark files

if [ $# -eq 0 ]
then
    echo "A configuration file must be supplied"
    exit 1
else
    config=$1
fi

if [ -e $config ]
then
    source $config
else
    echo "Failed to find the config file ${config}"
    exit 1
fi

DIST_DEPLOY='true'
if [ -z "$deploy_user" -o -z "$deploy_location" -o -z "$deploy_host" ]
then
  unset DIST_DEPLOY
fi


#==============================================================================
# Function definitions
#==============================================================================
# Check to make sure current component checkout exists locally and if it is
# missing, execute "svn up" from the current root
#==============================================================================
verify_checkout() {
  if [ ! -e "${current_root}/${component}" ]  # Check if checkout exists.
  then
    echo "$component checkout is missing."
    echo "Updating top level checkout..."
    cd $current_root
    svn up
  fi
}
#==============================================================================
# Process ant components in $components list.
# Assumes $components are checked out with common root, $current_root
#==============================================================================
process_antlibs() {
  for component in $components
  do
    verify_checkout
    cd ${current_root}/${component}
    echo
    echo "Using Ant to build $component...."
    svn up
    ant -Dartifact.version=${time_stamp} clean dist > $log_location/$component.log 2>&1 

    if [ ! -e distribution ] # build failed
    then
      failed_builds[${#failed_builds[@]}]=$component
    fi
       

    # Upload files
  if [ $DIST_DEPLOY ] 
  then
    ssh $deploy_user@$deploy_host mkdir -p $deploy_location/$component
    scp distribution/binaries/*-$component*.gz \
    $deploy_user@$deploy_host:$deploy_location/$component
    scp distribution/binaries/*-$component*.zip \
    $deploy_user@$deploy_host:$deploy_location/$component
  fi

    # Cleanup
    rm -f *${time_stamp}*
  done 
} 
#==============================================================================
# Main
#==============================================================================
echo "Ant, Antlibs and Sandbox Antlibs nightly build starting: `date`"

# Update ant-build
cd $nightly_list_location
svn up
cd $ant_root
rm -rf bootstrap
./build.sh -Ddist.name=apache-ant-${time_stamp} clean distribution 
scp distribution/binaries/* $deploy_user@$deploy_host:$deploy_location
# Set umask
umask 002
# play it like gump
export ANT_HOME=$ant_root/dist
export PATH=$ANT_HOME/bin:$PATH

# Antlibs
list_file="$nightly_list_location/nightly_antlibs_list.txt"
components=`<$list_file`
current_root=$antlibs_root
echo "=========================================="
echo " Building Antlibs                         "
echo "=========================================="
process_antlibs

# Make tar/zip files group writable
ssh $deploy_user@$deploy_host chmod -R g+w $deploy_location

# Send failure notification email if there are build failures
if [ ${#failed_builds[@]} -gt 0 ]
then
  rm ${report_location}
  echo "Failed build logs:" > ${report_location}
  subject="\"[nightly build] ${failed_builds[@]} failed.\""
  for i in "${failed_builds[@]}"; do
    echo "${log_url}/${time_stamp}/${i}.log" >> ${report_location}
  done
 if [ $DIST_DEPLOY ]
 then
  scp ${report_location} $deploy_user@$deploy_host:/home/$deploy_user
  ssh $deploy_user@$deploy_host mail -s $subject $notification_email < ${report_location}
  ssh $deploy_user@$deploy_host rm /home/$deploy_user/build_report 
 else
  mail -s $subject $notification_email < ${report_location}
 fi
  rm ${report_location}
fi

exit 0


