# Menuw - Restaurant Food Ordering App

A comprehensive Android application for ordering food from restaurants. The app supports both user and admin roles with features for browsing menus, placing orders, and managing restaurants.

## About

Menuw is a mobile application built with Android that enables users to:
- Browse restaurants and their menus
- View food items with detailed information
- Place and track food orders
- View order history
- Manage favorite restaurants

Admins can:
- Manage restaurants
- Update menu items
- Monitor orders

## Tech Stack

- **Language**: Java & Kotlin
- **Platform**: Android
- **Build System**: Gradle
- **API Level**: Minimum API 21+
- **Architecture**: MVVM with Fragments and Activities

## Project Structure

```
app/
├── src/
│   ├── main/
│   │   ├── java/com/example/menuw/
│   │   │   ├── activities/          # App screens (Login, Admin, User, Splash)
│   │   │   ├── adapters/            # RecyclerView & List adapters
│   │   │   ├── fragments/           # UI fragments
│   │   │   ├── data/                # Data models
│   │   │   └── utilities/           # Helper classes
│   │   ├── res/
│   │   │   ├── layout/              # XML layouts
│   │   │   ├── drawable/            # Images & vector drawables
│   │   │   ├── menu/                # Navigation menus
│   │   │   ├── values/              # Colors, strings, themes
│   │   │   └── xml/                 # Backup & data extraction rules
│   │   └── AndroidManifest.xml
│   ├── androidTest/                 # Instrumented tests
│   └── test/                        # Unit tests
├── build.gradle
└── proguard-rules.pro
```

## Getting Started

### Prerequisites

- Android Studio (latest version)
- JDK 11 or higher
- Android SDK (API level 21+)
- Gradle 7.0+

### Installation

1. Clone the repository:
   ```bash
   git clone <repository-url>
   cd menuw
   ```

2. Open the project in Android Studio:
   - File → Open → Select the project folder

3. Let Gradle sync and download dependencies automatically

4. Build the project:
   - Build → Make Project (or press Ctrl+F9)

### Running the App

1. **On Emulator:**
   - Create a virtual device (AVD Manager)
   - Select the device and click Run

2. **On Physical Device:**
   - Enable USB debugging on your device
   - Connect via USB
   - Click Run in Android Studio

## Features

### User Features
- 🔐 Secure login and authentication
- 🍽️ Browse restaurants and menus
- 📋 View detailed food items
- 🛒 Add items to cart and place orders
- 📜 View order history
- ⭐ Save favorite restaurants

### Admin Features
- 🏪 Manage restaurants
- 🍴 Add/edit/delete food items
- 📊 View orders
- 🔧 Update menu items

## Key Components

### Activities
- `LoginActivity.java` - User authentication
- `User.java` - Main user interface
- `Admin.java` - Admin dashboard
- `SplashScreen.java` - App startup screen

### Adapters
- `ExpandableListAdapter.java` - Menu categories
- `FoodItemListRecyclerViewAdapter.java` - Food listings
- `OrderHistoryRecyclerViewAdapter.java` - Order history

### Fragments
- Menu browsing
- Food item views
- Order management
- Restaurant views

## Dependencies

See `build.gradle` for the complete list of dependencies including:
- AndroidX libraries
- Material Design Components
- RecyclerView
- Navigation Components
- And more...

## Configuration

### Database
- Configure database connection settings in the data layer
- Update API endpoints in configuration files

### API
- Update base URL for API calls in configuration
- Add authentication tokens as needed

## Testing

### Unit Tests
```bash
./gradlew test
```

### Instrumented Tests
```bash
./gradlew connectedAndroidTest
```

## Build & Deployment

### Debug Build
```bash
./gradlew assembleDebug
```

### Release Build
```bash
./gradlew assembleRelease
```

The APK will be generated in `app/build/outputs/apk/`

## ProGuard Rules

ProGuard configuration is defined in `proguard-rules.pro` to:
- Optimize code
- Remove unused code
- Protect code through obfuscation

## Troubleshooting

- **Build Issues**: Run `./gradlew clean` and rebuild
- **Dependency Issues**: Invalidate caches (File → Invalidate Caches in Android Studio)
- **Device Connection**: Check USB debugging is enabled and device drivers are installed

## Contributing

1. Create a feature branch (`git checkout -b feature/feature-name`)
2. Commit your changes (`git commit -m 'Add feature'`)
3. Push to the branch (`git push origin feature/feature-name`)
4. Create a Pull Request

## License

This project does not currently include an explicit open-source license. Add a LICENSE file before public distribution.

## Contact & Support

For issues, questions, or suggestions, please contact:
- **Project Owner**: me
- **Email**: saivarunkonda007@gmail.com

---

**Last Updated**: May 2026