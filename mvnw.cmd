@ECHO OFF
REM ----------------------------------------------------------------------------
REM Maven Wrapper startup script for Windows.
REM On first run, this downloads the correct Maven version automatically
REM (see .mvn\wrapper\maven-wrapper.properties). Requires only a JDK and an
REM internet connection the first time you run it.
REM Usage:
REM   mvnw.cmd spring-boot:run
REM   mvnw.cmd clean package
REM ----------------------------------------------------------------------------

SETLOCAL ENABLEDELAYEDEXPANSION

SET "BASE_DIR=%~dp0"
SET "WRAPPER_DIR=%BASE_DIR%.mvn\wrapper"
SET "WRAPPER_JAR=%WRAPPER_DIR%\maven-wrapper.jar"
SET "WRAPPER_PROPERTIES=%WRAPPER_DIR%\maven-wrapper.properties"

IF NOT EXIST "%WRAPPER_JAR%" (
    ECHO Downloading Maven Wrapper jar...
    FOR /F "usebackq tokens=1,* delims==" %%A IN ("%WRAPPER_PROPERTIES%") DO (
        IF "%%A"=="wrapperUrl" SET "WRAPPER_URL=%%B"
    )
    ECHO Using wrapper URL: !WRAPPER_URL!
    powershell -Command "Invoke-WebRequest -Uri '!WRAPPER_URL!' -OutFile '!WRAPPER_JAR!'"
)

IF DEFINED JAVA_HOME (
    SET "JAVA_EXE=%JAVA_HOME%\bin\java.exe"
) ELSE (
    SET "JAVA_EXE=java.exe"
)

"!JAVA_EXE!" -classpath "!WRAPPER_JAR!" "-Dmaven.multiModuleProjectDirectory=!BASE_DIR!" org.apache.maven.wrapper.MavenWrapperMain %*

ENDLOCAL
