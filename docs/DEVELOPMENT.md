# Development Setup Guide

Complete development environment setup for Bite-Size Reader mobile client.

## Table of Contents

1. [Prerequisites](#prerequisites)
2. [Backend Setup](#backend-setup)
3. [Mobile Client Setup](#mobile-client-setup)
4. [IDE Configuration](#ide-configuration)
5. [Running the App](#running-the-app)
6. [Debugging](#debugging)
7. [Common Issues](#common-issues)

---

## Prerequisites

### Required Software

**All Platforms**:
- **Git**: Version control
- **JDK 17+**: For Gradle
- **Kotlin 2.2.20+**: Kotlin SDK

**Android Development**:
- **Android Studio Ladybug** (2024.2.1+)
- **Android SDK 24-36**
- **Android Emulator** or physical device

**iOS Development** (macOS only):
- **Xcode 15+**
- **CocoaPods**: `sudo gem install cocoapods`
- **iOS Simulator** or physical device
- **Apple Developer Account** (for device testing)

---

## Backend Setup

The mobile client requires the bite-size-reader backend service running.

### Option 1: Docker (Recommended)

```bash
# Clone backend repository
cd ..
git clone https://github.com/po4yka/bite-size-reader.git
cd bite-size-reader

# Configure environment
cp .env.example .env
# Edit .env with your API keys:
# - BOT_TOKEN (from @BotFather)
# - OPENROUTER_API_KEY
# - FIRECRAWL_API_KEY
# - JWT_SECRET_KEY (generate with: openssl rand -hex 32)
# - ALLOWED_USER_IDS (your Telegram user ID)

# Start with Docker Compose
docker-compose up -d

# Verify backend is running
curl http://localhost:8000/health
# Expected: {"status":"ok"}
```

### Option 2: Manual Setup

```bash
# Clone backend
cd ..
git clone https://github.com/po4yka/bite-size-reader.git
cd bite-size-reader

# Create virtual environment
python3.13 -m venv .venv
source .venv/bin/activate

# Install dependencies
pip install -r requirements.txt

# Configure environment
cp .env.example .env
# Edit .env with your credentials

# Run API server
uvicorn app.api.main:app --reload --host 0.0.0.0 --port 8000
```

### Backend Verification

```bash
# Test health endpoint
curl http://localhost:8000/health

# Test API documentation
open http://localhost:8000/docs
```

---

## Mobile Client Setup

### Clone Repository

```bash
git clone https://github.com/po4yka/bite-size-reader-client.git
cd bite-size-reader-client
```

### Configure Local Properties

Create `local.properties`:

```properties
# Backend API
api.base.url=http://10.0.2.2:8000  # Android emulator
# api.base.url=http://localhost:8000  # iOS simulator
api.timeout.seconds=30

# Authentication
telegram.bot.token=YOUR_BOT_TOKEN_HERE
client.id=android-app-v1.0

# Optional: Enable debug features
api.logging.enabled=true
api.debug.payloads=false
```

**Android Emulator Note**: Use `10.0.2.2` to access `localhost` on host machine.

**iOS Simulator Note**: Use `localhost` or `127.0.0.1` directly.

**Physical Device Note**: Use your computer's local IP (e.g., `192.168.1.100:8000`).

### Install Dependencies

```bash
# Gradle sync will download all dependencies
./gradlew build
```

---

## IDE Configuration

### Android Studio

**1. Open Project**:
```bash
open -a "Android Studio" .
```

**2. SDK Configuration**:
- File → Project Structure → SDK Location
- Verify Android SDK path
- Install missing SDK components if prompted

**3. Gradle Sync**:
- File → Sync Project with Gradle Files
- Wait for dependencies to download

**4. Run Configuration**:
- Run → Edit Configurations
- Add "Android App" configuration
- Module: `composeApp`
- Launch: Default Activity

**5. Enable KMP Plugin**:
- File → Settings → Plugins
- Install "Kotlin Multiplatform Mobile"

### Xcode

**1. Install CocoaPods Dependencies**:
```bash
cd iosApp
pod install
cd ..
```

**2. Open Workspace**:
```bash
open iosApp/iosApp.xcworkspace
```

**3. Configure Signing**:
- Select "iosApp" target
- Signing & Capabilities tab
- Team: Select your Apple Developer team
- Bundle Identifier: `com.po4yka.bitesizereader`

**4. Generate Framework**:
```bash
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

---

## Running the App

### Android

**From Android Studio**:
1. Select device/emulator from dropdown
2. Click "Run" (Ctrl+R / Cmd+R)

**From Command Line**:
```bash
# Build and install
./gradlew :composeApp:installDebug

# Or run directly
./gradlew :composeApp:run
```

### iOS

**From Xcode**:
1. Select target device/simulator
2. Product → Run (Cmd+R)

**From Command Line**:
```bash
# Build framework first
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64

# Then open in Xcode
open iosApp/iosApp.xcworkspace

# Or use xcodebuild (advanced)
xcodebuild -workspace iosApp/iosApp.xcworkspace \
           -scheme iosApp \
           -configuration Debug \
           -sdk iphonesimulator \
           -destination 'platform=iOS Simulator,name=iPhone 15'
```

---

## Debugging

### Logs

**Android**:
```bash
# View all logs
adb logcat

# Filter by app
adb logcat | grep "BiteSizeReader"

# Filter by tag
adb logcat -s "HTTP"

# Clear logs
adb logcat -c
```

**iOS**:
- View in Xcode Console (Cmd+Shift+Y)
- Filter by process name or log level

### Network Debugging

**Android (Charles Proxy)**:
1. Install Charles Proxy on computer
2. Configure proxy on emulator:
   - Settings → Network → Wi-Fi → Modify Network
   - Proxy: Manual
   - Host: `10.0.2.2`, Port: `8888`
3. Trust Charles certificate on device

**iOS (Charles Proxy)**:
1. Install Charles Proxy
2. Configure proxy on simulator:
   - Settings → Wi-Fi → Configure Proxy
   - Host: `localhost`, Port: `8888`
3. Install Charles certificate

### Database Inspection

**Android (Device File Explorer)**:
1. View → Tool Windows → Device File Explorer
2. Navigate to `/data/data/com.po4yka.bitesizereader/databases/`
3. Pull `bite_reader.db`
4. Open with SQLite browser

**iOS (Xcode)**:
1. Window → Devices and Simulators
2. Select device/simulator
3. Download Container
4. Navigate to `Library/Application Support/`

### Remote Debugging

**Android (Chrome DevTools)**:
```bash
chrome://inspect
```

**iOS (Safari Web Inspector)**:
1. Enable in iOS: Settings → Safari → Advanced → Web Inspector
2. Safari → Develop → [Device Name] → [Page]

---

## Common Issues

### Issue: "SDK location not found"

**Solution**:
```bash
# Create local.properties with SDK path
echo "sdk.dir=/Users/YOUR_USER/Library/Android/sdk" >> local.properties
```

### Issue: "Framework not found Shared" (iOS)

**Solution**:
```bash
./gradlew :shared:linkDebugFrameworkIosSimulatorArm64
```

### Issue: "Connection refused" to backend

**Solution**:
1. Verify backend is running: `curl http://localhost:8000/health`
2. Check firewall settings
3. For Android emulator, use `10.0.2.2` not `localhost`
4. For physical device, use computer's IP

### Issue: Build fails with "Execution failed for ':shared:compileKotlinAndroid'"

**Solution**:
```bash
# Clean and rebuild
./gradlew clean build --refresh-dependencies
```

### Issue: CocoaPods install fails

**Solution**:
```bash
# Update CocoaPods repo
pod repo update

# Clean and reinstall
cd iosApp
pod deintegrate
pod install
```

### Issue: "Gradle version mismatch"

**Solution**:
```bash
# Use Gradle wrapper (recommended)
./gradlew --version

# Or update wrapper
./gradlew wrapper --gradle-version=8.5
```

---

## Development Workflow

### 1. Feature Development

```bash
# Create feature branch
git checkout -b feature/summary-filters

# Work in shared/ first (business logic)
# shared/src/commonMain/kotlin/...

# Then add platform UI
# composeApp/src/androidMain/kotlin/...
# iosApp/iosApp/Views/...

# Test as you go
./gradlew :shared:allTests

# Commit frequently
git add .
git commit -m "Add summary filters"
```

### 2. Testing

```bash
# Run all shared tests
./gradlew :shared:allTests

# Run Android tests
./gradlew :composeApp:testDebugUnitTest

# Run iOS tests (requires macOS)
./gradlew :shared:iosSimulatorArm64Test
```

### 3. Code Quality

```bash
# Format code (use IDE auto-format)
# Cmd+Opt+L (macOS) or Ctrl+Alt+L (Windows/Linux)

# Or use ktlint (if configured)
./gradlew ktlintFormat
```

---

## Tips & Tricks

### Hot Reload

- **Android**: Supported natively with Jetpack Compose
- **iOS**: Use Xcode's SwiftUI Previews for rapid iteration

### Gradle Build Performance

Add to `gradle.properties`:
```properties
org.gradle.jvmargs=-Xmx4096M -Dfile.encoding=UTF-8
org.gradle.configuration-cache=true
org.gradle.caching=true
org.gradle.parallel=true
kotlin.incremental=true
kotlin.daemon.jvmargs=-Xmx3072M
```

### Useful Gradle Tasks

```bash
# List all tasks
./gradlew tasks

# Build without tests
./gradlew build -x test

# Clean build
./gradlew clean build

# Dependency tree
./gradlew :shared:dependencies

# Check for dependency updates
./gradlew dependencyUpdates
```

---

## Resources

- [Kotlin Multiplatform Docs](https://kotlinlang.org/docs/multiplatform.html)
- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Backend API Docs](http://localhost:8000/docs)
- [Ktor Client Guide](https://ktor.io/docs/client.html)
- [SQLDelight Documentation](https://cashapp.github.io/sqldelight/)

---

**Last Updated**: 2025-11-16
