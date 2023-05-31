@echo off

echo Checking Java and Maven installation...

:: Check if JDK 17 is installed
for /f "tokens=3" %%i in ('java -version 2^>^&1 ^| findstr /i "version"') do set JAVA_VERSION=%%i
set JAVA_VERSION=%JAVA_VERSION:"=%
if not "%JAVA_VERSION:~0,2%"=="17" (
    echo Please install JDK 17 to proceed.
    exit /b 1
) else (
    echo Java version 17 detected.
)

:: Check if Maven is installed
where mvn >nul 2>&1
if errorlevel 1 (
    echo Maven could not be found. Please install Maven to proceed.
    exit /b 1
) else (
    echo Maven detected.
)

echo Building the project...

:: Execute 'mvn clean install' in the ./src directory
pushd ".\src"
call mvn clean install
popd

:: Build and copy the jar files for Client, Master, and Region
call ./exportJar.bat "Client" "Client-1.0-SNAPSHOT.jar" "Client.jar"
call ./exportJar.bat "MasterServer" "MasterServer-1.0-SNAPSHOT-jar-with-dependencies.jar" "Master.jar"
call ./exportJar.bat "RegionServer" "RegionServer-1.0-SNAPSHOT-jar-with-dependencies.jar" "Region.jar"

echo Build completed successfully.