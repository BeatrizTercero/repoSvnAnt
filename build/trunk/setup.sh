#!/bin/sh
#==============================================================================
# Wrapper for commons_nightly.sh
# Updates build script, sets up environment, executes  and pipes output to log
#==============================================================================

# Set up Java environment
export JAVA_HOME=/usr/lib/jvm/java-1.5.0-sun

# Set Path
export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/bin/X11:/usr/games:$HOME/bin:$JAVA_HOME/bin
cd $HOME
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
svn co $ant_svn $ant_root
svn co $antlibs_svn $antlibs_root
svn co $sandbox_svn $sandbox_root
svn co $nighly_list_svn $nightly_list_location 

