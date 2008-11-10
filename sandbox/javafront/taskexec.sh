#! /bin/sh

# Licensed to the Apache Software Foundation (ASF) under one or more
# contributor license agreements.  See the NOTICE file distributed with
# this work for additional information regarding copyright ownership.
# The ASF licenses this file to You under the Apache License, Version 2.0
# (the "License"); you may not use this file except in compliance with
# the License.  You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

StartAnt()
{
    ant -lib build/classes -main org.apache.ant.javafront.TaskExec "$@"
}

clear

echo '----- Build the library -----'
ant




echo '============================================================================================='
echo XML: '<echo message="Hello World"/>'
echo CMD: echo message \"Hello World\"
echo -----------------------------------------------------------------------------------------------
StartAnt echo message "Hello World"


echo ===============================================================================================
echo XML: '<echo message="This is Ant version ${ant.version}"/>'
echo CMD: echo message \'This is Ant version \\\${ant.version}\'
echo '---------------------------------------------------------------------------------------------'
StartAnt echo message 'This is Ant version \${ant.version}'


echo '==============================================================================================='
echo XML: '<echoproperties prefix="ant."/>'
echo CMD: echoproperties prefix ant.
echo '---------------------------------------------------------------------------------------------'
StartAnt echoproperties prefix ant.


echo '==============================================================================================='
echo XML: '<copy file="build.xml" tofile="build.xml.bak"/>'
echo CMD: copy file build.xml tofile build.xml.bak
echo '---------------------------------------------------------------------------------------------'
StartAnt copy file build.xml tofile build.xml.bak

echo ----- A 'build.xml.bak' should exist -----
ls bu*.*


echo '==============================================================================================='
echo XML: '<delete file="build.xml.bak"/>'
echo CMD: delete file build.xml.bak
echo '---------------------------------------------------------------------------------------------'
StartAnt delete file build.xml.bak

echo ----- A 'build.xml.bak' should not exist -----
ls bu*.*


echo '==============================================================================================='
echo XML: '<mkdir dir="test"/>'
echo XML: '<copy todir="test">'
echo          '<fileset dir="src"/>'
echo      '</copy>'
echo CMD: mkdir dir test
echo CMD: copy todir test + fileset dir src
echo '---------------------------------------------------------------------------------------------'
StartAnt mkdir dir test
StartAnt copy todir test + fileset dir src


echo '==============================================================================================='
echo XML: '<echo>Hello World</echo>'
echo CMD: echo \\\# \'This is Ant version \\\${ant.version}\'
echo '---------------------------------------------------------------------------------------------'
StartAnt echo \# 'This is Ant version \${ant.version}'


echo '==============================================================================================='
echo XML: '<concat>'
echo          '<fileset dir="." includes="*.properties"/>'
echo          '<header>Ant Version \${ant.version}</header>'
echo          '<footer>End of text</footer>'
echo      '</concat>'
echo CMD: concat + fileset dir . includes \\\*.properties - + header \\\# \'Ant Version \${ant.version}\' - + footer \\\# \'End of text\'
echo '---------------------------------------------------------------------------------------------'
StartAnt concat + fileset dir . includes \*.properties - + header \# 'Ant Version \${ant.version}' - + footer \# 'End of text'


echo '============================================================================================='
echo XML: '<ivy:retrieve xmlns:ivy="antlib:org.apache.ivy.ant" organisation="junit" module="junit" inline="true" pattern="_ivy/[artifact].[ext]"/>'
echo CMD: ivy:retrieve organisation junit module junit inline true pattern _ivy/[artifact].[ext]
echo '---------------------------------------------------------------------------------------------'
mkdir _ivy
echo "------ First we '<get>' Ivy -----"
StartAnt get dest _ivy/ivy.jar src http://people.apache.org/~xavier/ivy/ivy-trunk.jar
echo '------ Then we use Ivy to retrieve JUnit -----'
ant -lib build/classes -lib _ivy -main org.apache.ant.javafront.TaskExec -xmlns:ivy=antlib:org.apache.ivy.ant ivy:retrieve organisation junit module junit pattern _ivy/[artifact].[ext] inline true

