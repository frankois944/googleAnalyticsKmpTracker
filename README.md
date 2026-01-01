# Google Analytics KMP Tracker

A Kotlin Multiplatform library for Google Analytics (GA4) that supports all major targets (Android, iOS, Desktop, Web, macOS, tvOS, watchos). This library uses the Measurement Protocol (v2) to send events directly to Google Analytics from your shared code.

## Features

- **Multiplatform Support**: Android, iOS, Desktop (JVM), Web (JS/Wasm), macOS, tvOS, watchos.
- **Persistence**: Events are queued and persisted (using SQLDelight) to ensure delivery even if the app is offline or closed.

## Installation

```kotlin
// build.gradle.kts
commonMain {
    dependencies {
        implementation("io.github.frankois944:googleAnalyticsKMPTracker:LATEST_VERSION")
    }
}
```

## Usage

### 1. Initialization

Initialize the tracker in your shared code. On Android, a `Context` is required for disk persistence and device info.

#### Requirement

Found in the Google Analytics UI :

* **api_secret** :  Found under Admin > Data Streams > Choose your stream > Measurement Protocol > Create.
                Private to your organization. Should be regularly updated to avoid excessive SPAM.
* **measurementId** : Found under Admin > Data Streams > choose your stream > Measurement ID.

```kotlin
val tracker = Tracker.create(
    apiSecret = "YOUR_API_SECRET",
    measurementId = "G-XXXXXXXXXX",
    context = androidContext // Mandatory for Android, null otherwise
)
```

### 2. Tracking Events

You can track custom events with parameters:

```kotlin
tracker.trackEvent(
    name = "button_click",
    parameters = mapOf(
        "button_id" to "login_submit",
        "screen_name" to "LoginScreen"
    )
)
```

### 3. Tracking Page Views

Track screen transitions easily:

```kotlin
// Simple view tracking
tracker.trackView("HomeScreen")

// Tracking nested navigation paths
tracker.trackView(listOf("Main", "Settings", "Profile"))
```

### 4. Tracking Search

```kotlin
tracker.trackSearch("Kotlin Multiplatform")
```

### 5. User Properties and User ID

```kotlin
// Set unique user ID
tracker.setUserId("user_123456")

// Set custom user properties
tracker.setUserProperty("membership_level", "premium")
```

## License

This project is licensed under the Apache License 2.0.
