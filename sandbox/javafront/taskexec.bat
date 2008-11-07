@echo off
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

