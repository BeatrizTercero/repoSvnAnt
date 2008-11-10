@echo off

REM  Licensed to the Apache Software Foundation (ASF) under one or more
REM  contributor license agreements.  See the NOTICE file distributed with
REM  this work for additional information regarding copyright ownership.
REM  The ASF licenses this file to You under the Apache License, Version 2.0
REM  (the "License"); you may not use this file except in compliance with
REM  the License.  You may obtain a copy of the License at
REM 
REM      http://www.apache.org/licenses/LICENSE-2.0
REM 
REM  Unless required by applicable law or agreed to in writing, software
REM  distributed under the License is distributed on an "AS IS" BASIS,
REM  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
REM  See the License for the specific language governing permissions and
REM  limitations under the License.

cls
set START=call ant -lib build\classes -main org.apache.ant.javafront.TaskExec


echo ----- Build the library -----
call ant




echo ===============================================================================================
echo XML: ^<echo message="Hello World"/^>
echo CMD: echo message "Hello World"
echo -----------------------------------------------------------------------------------------------
%START% echo message "Hello World"


echo ===============================================================================================
echo XML: ^<echo message="This is Ant version ${ant.version}"/^>
echo CMD: echo message "This is Ant version ${ant.version}"
echo -----------------------------------------------------------------------------------------------
%START% echo message "This is Ant version ${ant.version}"


echo ===============================================================================================
echo XML: ^<echoproperties prefix="ant."/^>
echo CMD: echoproperties prefix ant.
echo -----------------------------------------------------------------------------------------------
%START% echoproperties prefix ant.


echo ===============================================================================================
echo XML: ^<copy file="build.xml" tofile="build.xml.bak"/^>
echo CMD: copy file build.xml tofile build.xml.bak
echo -----------------------------------------------------------------------------------------------
%START% copy file build.xml tofile build.xml.bak

echo ----- A 'build.xml.bak' should exist -----
dir bu*.*


echo ===============================================================================================
echo XML: ^<delete file="build.xml.bak"/^>
echo CMD: delete file build.xml.bak
echo -----------------------------------------------------------------------------------------------
%START% delete file build.xml.bak

echo ----- A 'build.xml.bak' should not exist -----
dir bu*.*


echo ===============================================================================================
echo XML: ^<mkdir dir="test"/^>
echo XML: ^<copy todir="test"^>
echo          ^<fileset dir="src"/^>
echo      ^</copy^>
echo CMD: mkdir dir test
echo CMD: copy todir test + fileset dir src
echo -----------------------------------------------------------------------------------------------
%START% mkdir dir test
%START% copy todir test + fileset dir src


echo ===============================================================================================
echo XML: ^<echo^>Hello World^</echo^>
echo CMD: echo # This is Ant version ${ant.version}
echo -----------------------------------------------------------------------------------------------
%START% echo # This is Ant version ${ant.version}


echo ===============================================================================================
echo XML: ^<concat^>
echo          ^<fileset dir="src" includes="*.properties"/^>
echo          ^<header^>Ant Version ${ant.version}^</header^>
echo          ^<footer^>End of text^</footer^>
echo      ^</concat^>
echo CMD: concat + fileset dir src includes *.properties - + header # Ant Version ${ant.version} - + footer # End of text
echo -----------------------------------------------------------------------------------------------
%START% concat + fileset dir src includes *.properties - + header # Ant Version ${ant.version} - + footer # End of text



:end