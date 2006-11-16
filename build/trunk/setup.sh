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
svn co $sandbox_antlibs_svn $sandbox_antlibs_root
svn co $nightly_list_svn $nightly_list_location 
mkdir $log_location
mkdir $HOME/bin
wget ${fetch_url} -O $HOME/${fetch_binary}
cd $HOME
gzip -cd $HOME/${fetch_binary} | tar xvf -
export ANT_HOME=${fetch_product}
$ANT_HOME/bin/ant -f $ANT_HOME/fetch.xml -Ddest.dir=${ant_root}/lib/optional
