# How to Install AndroidBulkSender on Your Phone

This guide will walk you through the process of installing AndroidBulkSender on your Android device.

## Prerequisites

Before you begin, make sure you have:

1. An Android device running Android 6.0 (Marshmallow) or higher
2. USB debugging enabled on your device (see instructions below)
3. A USB cable to connect your phone to your computer
4. Android Studio OR Android SDK Platform Tools installed on your computer

## Step 1: Enable Developer Options and USB Debugging

1. Open Settings on your Android device
2. Scroll down and tap "About phone"
3. Find "Build number" and tap it 7 times
4. You'll see a message that you are now a developer
5. Go back to Settings and find "Developer options"
6. Enable "USB debugging"

## Step 2: Build the APK

### Using Android Studio:
1. Open Android Studio
2. Open the AndroidBulkSender project
3. Click Build > Build Bundle(s) / APK(s) > Build APK(s)
4. Wait for the build to complete
5. Look for a notification that says "APK generated successfully"

### Using Command Line:
1. Open a terminal/command prompt
2. Navigate to the AndroidBulkSender project directory
3. Run the following command:
   ```bash
   # On Linux/Mac:
   ./gradlew assembleDebug

   # On Windows:
   gradlew.bat assembleDebug
   ```
4. The APK will be generated at:
   `AndroidBulkSender/app/build/outputs/apk/debug/app-debug.apk`

## Step 3: Install the App

### Method 1: Direct APK Installation
1. Copy the APK file to your Android device
2. On your Android device, open Files or File Manager
3. Navigate to where you copied the APK
4. Tap the APK file to start installation
5. If prompted, enable "Install from Unknown Sources"
6. Tap "Install"

### Method 2: Using ADB
1. Connect your Android device to your computer via USB
2. Open terminal/command prompt
3. Verify your device is connected:
   ```bash
   adb devices
   ```
4. Install the app:
   ```bash
   adb install -r path/to/app-debug.apk
   ```

## Step 4: Required Permissions

After installation, you'll need to:

1. Open AndroidBulkSender
2. Grant the following permissions when prompted:
   - Storage access (for attachments)
   - Accessibility Service (for WhatsApp automation)

## Troubleshooting

### Common Issues:

1. **"App not installed" error**
   - Make sure you have enough storage space
   - Try uninstalling any previous version first
   - Enable "Install from Unknown Sources"

2. **ADB device not found**
   - Ensure USB debugging is enabled
   - Try a different USB cable
   - Reinstall USB drivers on your computer

3. **Build fails**
   - Ensure you have the latest version of Android Studio or SDK Tools
   - Try running `./gradlew clean` before building
   - Check your internet connection for downloading dependencies

### Still Having Problems?

If you encounter any issues during installation:
1. Check the Android Studio build window for specific error messages
2. Make sure all prerequisites are properly set up
3. Try cleaning and rebuilding the project

## After Installation

Once installed successfully:
1. Open AndroidBulkSender from your app drawer
2. Complete the initial setup process
3. Configure the Accessibility Service when prompted
4. You're ready to start using the app!

## Security Note

Always download and install APKs from trusted sources only. This app requires certain permissions to function properly, but will only use them for their intended purposes as described in the app's privacy policy.
