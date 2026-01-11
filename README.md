# Google Analytics KMP Tracker

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.frankois944/googleAnalyticsKMPTracker)
[![Kotlin](https://img.shields.io/badge/kotlin-2.2.20-blue.svg?logo=kotlin)](https://kotlinlang.org)

![Android](https://img.shields.io/badge/Android-green.svg?logo=android)
![iOS](https://img.shields.io/badge/iOS-black.svg?logo=apple)
![macOS](https://img.shields.io/badge/macOS-black.svg?logo=apple)
![tvOS](https://img.shields.io/badge/tvOS-black.svg?logo=apple)
![watchOS](https://img.shields.io/badge/watchOS-black.svg?logo=apple)
![Desktop](https://img.shields.io/badge/Desktop-JVM-orange.svg?logo=openjdk)
![JS](https://img.shields.io/badge/JS-yellow.svg?logo=javascript)
![Wasm](https://img.shields.io/badge/Wasm-purple.svg?logo=webassembly)

A Kotlin Multiplatform library for Google Analytics (GA4) that supports all major targets (Android, iOS, Desktop, Web, macOS, tvOS, watchos). This library uses the Measurement Protocol (v2) to send events directly to Google Analytics from your shared code.

> [!IMPORTANT]  
> This library uses the Google Analytics API, not the Firebase API.
> 
> As the GA API is optimized for the web, you need to follow this [documentation](https://developers.google.com/analytics/devguides/collection/protocol/ga4/reference/events) to create events.
## Features

- **Multiplatform Support**: Android, iOS, Desktop (JVM), Web (JS/Wasm), macOS, tvOS, watchos.
- **Persistence**: Events are queued and persisted (using SQLDelight)

### Web targets

> [!IMPORTANT]  
> Due to the nature of Google Analytics, you can't use the tracker locally, as a CORS error will be triggered.

## Installation

![Maven Central Version](https://img.shields.io/maven-central/v/io.github.frankois944/googleAnalyticsKMPTracker)

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

* **apiSecret** :  Found under Admin > Data Streams > Choose your stream > Measurement Protocol > Create.  
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

### 6. User consent

```kotlin
tracker.enableAdUserData(false)
tracker.enableAdPersonalization(false)
```

# License

This project is licensed under the MIT.
