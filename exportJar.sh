#!/bin/bash

# Helper function to build and copy the jar file
module=$1
filename=$2
newfilename=$3

echo "filename: $filename"
echo "newfilename: $newfilename"
echo "Building $module..."

pushd "./src/$module" || exit
# Uncomment the following line if you want to build the module using 'mvn package'
mvn package
cp "./target/$filename" "../../JarFile/$newfilename"
popd || exit