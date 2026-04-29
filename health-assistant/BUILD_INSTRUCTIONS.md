# Build Instructions for Health Assistant

## Prerequisites
- Android Studio Hedgehog or later
- Android SDK 26-34
- Java 8 or higher
- Kotlin 1.9.10+

## Setup Steps

### 1. Clone/Open Project
1. Open Android Studio
2. Select "Open an existing project"
3. Navigate to the `health-assistant` folder
4. Click "OK"

### 2. Sync Gradle
1. Wait for Gradle to sync automatically
2. If sync fails, click "Sync Now" in the top right
3. Make sure all dependencies are downloaded

### 3. Install Required Components
1. **Health Connect**: Install from Google Play Store on your device/emulator
2. **Android 6+**: Ensure device/emulator runs Android 6.0 or higher

### 4. Build the Project
1. Go to `Build` > `Build Bundle(s) / APK(s)` > `Build APK(s)`
2. Wait for build to complete
3. Fix any compilation errors if they appear

### 5. Run the App
1. Connect an Android device or start an emulator
2. Click the green "Run" button in Android Studio
3. Grant permissions when prompted:
   - Health Connect permissions
   - Camera/Microphone (for video calls)
   - Storage (for data export)

## Troubleshooting

### Common Issues

#### 1. Hilt/Dagger Errors
```
Error: [Dagger/MissingBinding] Cannot provide...
```
**Solution**: Clean and rebuild the project
- `Build` > `Clean Project`
- `Build` > `Rebuild Project`

#### 2. Health Connect Not Available
```
Health Connect client not available
```
**Solution**: Install Health Connect from Play Store
- Open Google Play Store
- Search "Health Connect"
- Install and open the app
- Grant necessary permissions

#### 3. TensorFlow Lite Model Error
```
Failed to load TFLite model
```
**Solution**: This is expected - the app uses a placeholder model
- The app will fall back to rule-based anomaly detection
- For production, replace `health_anomaly_model.tflite` with a trained model

#### 4. Jitsi SDK Issues
```
Jitsi Meet SDK initialization failed
```
**Solution**: Check network connection and dependencies
- Ensure internet connection is available
- Verify Jitsi dependencies are properly downloaded

#### 5. Permission Errors
```
Permission denied for health data
```
**Solution**: Grant permissions through Health Connect
- Open Health Connect app
- Find "Health Assistant"
- Grant all requested permissions

### Build Variants

The project supports the following build variants:
- `debug` - Development build with debugging enabled
- `release` - Production build (requires signing configuration)

### Dependencies

Key dependencies and their purposes:
- **Hilt**: Dependency injection
- **Health Connect**: Android health data API
- **TensorFlow Lite**: On-device ML inference
- **Jitsi Meet**: Video calling functionality
- **iText**: PDF report generation
- **OpenCSV**: CSV data export
- **Compose**: Modern UI framework

### Testing

To run tests:
1. Unit tests: Right-click on `app/src/test` > `Run Tests`
2. Instrumentation tests: Right-click on `app/src/androidTest` > `Run Tests`

### Production Deployment

For production deployment:
1. Configure signing keys in `app/build.gradle`
2. Update `applicationId` to your unique package name
3. Replace placeholder TFLite model with trained model
4. Configure Jitsi server (optional - default uses meet.jit.si)
5. Add proper error handling and logging
6. Test on multiple devices and API levels

## Architecture Overview

```
app/
├── src/main/java/com/healthassistant/
│   ├── MainActivity.kt              # Main entry point
│   ├── HealthAssistantApplication.kt # Application class
│   ├── di/                         # Dependency injection
│   ├── navigation/                 # Navigation setup
│   ├── screens/                    # UI screens
│   ├── viewmodels/                 # ViewModels for screens
│   ├── health/                     # Health data management
│   ├── ml/                         # ML anomaly detection
│   ├── telehealth/                 # Video calling
│   ├── export/                     # Data export
│   └── ui/theme/                   # UI theme and styling
```

## Next Steps

After successful build:
1. Test all features on a real device
2. Customize UI colors and themes
3. Add proper error handling
4. Implement data persistence
5. Add user authentication
6. Configure backend services (optional)
7. Submit to Google Play Store

## Support

For issues:
1. Check Android Studio's Build Output for error messages
2. Verify all dependencies are properly synced
3. Ensure device/emulator meets minimum requirements
4. Check AndroidX and Jetpack Compose versions
