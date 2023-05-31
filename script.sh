#!/bin/bash

echo "Checking Java and Maven installation..."

# Check if JDK 17 is installed
JAVA_VERSION=$(java -version 2>&1 | grep -i "version" | awk '{print $3}' | tr -d '"')
if [[ ${JAVA_VERSION:0:2} != "17" ]]; then
    echo "Please install JDK 17 to proceed."
    exit 1
else
    echo "Java version 17 detected."
fi

# Check if Maven is installed
if ! command -v mvn > /dev/null 2>&1; then
    echo "Maven could not be found. Please install Maven to proceed."
    exit 1
else
    echo "Maven detected."
fi

echo "Building the project..."

# Execute 'mvn clean install' in the ./src directory
pushd "./src" || exit
mvn clean install
popd || exit

# Build and copy the jar files for Client, Master, and Region
./exportJar.sh "Client" "Client-1.0-SNAPSHOT.jar" "Client.jar"
./exportJar.sh "MasterServer" "MasterServer-1.0-SNAPSHOT-jar-with-dependencies.jar" "Master.jar"
./exportJar.sh "RegionServer" "RegionServer-1.0-SNAPSHOT-jar-with-dependencies.jar" "Region.jar"

echo "Build completed successfully."