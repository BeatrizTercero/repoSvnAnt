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

echo '----- First Run: using ^<echo^> as Hello World'
StartAnt echo message "Hello World"

echo '----- Second Run: using ^<echoproperties^> for printing all Ant related properties -----'
StartAnt echoproperties prefix ant.

echo '----- Third Run: using ^<copy^> for copying one file -----'
StartAnt copy file build.xml tofile build.xml.bak

echo "----- A 'build.xml.bak' should exist -----"
ls bu*.*

echo '----- Delete that file again -----'
StartAnt delete file build.xml.bak
ls bu*.*
