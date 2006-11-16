#!/bin/sh
#==============================================================================
# Wrapper for commons_nightly.sh
# Updates build script, sets up environment, executes  and pipes output to log
#==============================================================================

# Set up Java environment
export JAVA_HOME=/usr/lib/jvm/java-1.5.0-sun

# Set Path
export PATH=/usr/local/sbin:/usr/local/bin:/usr/sbin:/usr/bin:/sbin:/bin:/usr/bin/X11:/usr/games:$HOME/bin:$JAVA_HOME/bin

# Update script before executing
cd /home/antoine/ant-build
# TODO: Update the nightly_wrapper.sh too?
svn up ant_nightly.sh vmbuild.conf
cp ant_nightly.sh vmbuild.conf /home/antoine/bin
chmod +x /home/antoine/bin/ant_nightly.sh
cd /home/antoine

time_stamp=`date +%Y%m%d` 
# Execute script, piping output to log
ant_nightly.sh $HOME/bin/vmbuild.conf > $HOME/log/ant_nightly.log 2>&1

# TODO: If $? is non zero, mail commons_nightly.log to commons-dev; or maybe just to a hardcoded list

# For now, scp the logs over to ~antoine
ssh people.apache.org mkdir /x1/home/antoine/public_html/ant-nightlies/$time_stamp
scp $HOME/log/* antoine@people.apache.org:/x1/home/antoine/public_html/ant-nightlies/$time_stamp

