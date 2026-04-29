@echo off
echo Building Health Assistant Android Project...
echo.

REM Set Android SDK path
set ANDROID_HOME=C:\Users\hjbhg\AppData\Local\Android\Sdk
set PATH=%PATH%;%ANDROID_HOME%\platform-tools;%ANDROID_HOME%\emulator

REM Try to build using Android Studio's gradle
echo Looking for Gradle in Android Studio...
set GRADLE_PATH=C:\Program Files\Android\Android Studio1\gradle\bin\gradle.bat

if exist "%GRADLE_PATH%" (
    echo Found Gradle at: %GRADLE_PATH%
    echo.
    echo Building project...
    "%GRADLE_PATH%" assembleDebug
    if %ERRORLEVEL% EQU 0 (
        echo Build successful!
        echo APK location: app\build\outputs\apk\debug\app-debug.apk
    ) else (
        echo Build failed with error code %ERRORLEVEL%
    )
) else (
    echo Gradle not found in Android Studio installation.
    echo Please install Gradle or use Android Studio to build the project.
)

echo.
pause
