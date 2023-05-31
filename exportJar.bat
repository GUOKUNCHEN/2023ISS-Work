@echo off

:: Helper function to build and copy the jar file
set "module=%1"
set "filename=%2"
set "newfilename=%3"

echo filename:%filename%
echo newfilename:%newfilename%
echo Building %module%...

pushd ".\src\%module%"
echo %cd%
call mvn package
copy ".\target\%filename%" "..\..\JarFile\%newfilename%"
popd
goto :eof