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

echo ----- First Run: using ^<echo^> as Hello World
%START% echo message "Hello World"

echo ----- Second Run: using ^<echoproperties^> for printing all Ant related properties -----
%START% echoproperties prefix ant.

echo ----- Third Run: using ^<copy^> for copying one file -----
%START% copy file build.xml tofile build.xml.bak

echo ----- A 'build.xml.bak' should exist -----
dir bu*.*

echo ----- Delete that file again -----
%START% delete file build.xml.bak
dir bu*.*

