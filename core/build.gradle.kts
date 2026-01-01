@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "io.github.frankois944.googleAnalyticsKMPTracker"
version = libs.versions.libaryVersion.get()
val productName = "core"

kotlin {

    explicitApi()
    jvm("desktop")
    androidLibrary {
        namespace = "$group.$productName"
        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        withJava() // enable java compilation support
        compilerOptions {
            jvmTarget.set(
                JvmTarget.JVM_11,
            )
        }
    }

    iosX64()
    iosArm64()
    iosSimulatorArm64()
    tvosX64()
    tvosArm64()
    tvosSimulatorArm64()
    macosX64()
    macosArm64()
    watchosX64()
    watchosArm64()
    watchosSimulatorArm64()
    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        browser()
    }
    js {
        browser()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.serialization.cbor)
            implementation(libs.kotlinx.serialization.json)
        }
    }
}

mavenPublishing {
    publishToMavenCentral(true)
    signAllPublications()

    coordinates(
        group.toString(),
        productName, // unique artifact name
        version.toString(),
    )

    pom {
        name = "GoogleAnalytics KMP Tracker"
        description = "A Google Analytics client tracker for Kotlin Multiplatform"
        inceptionYear = "2026"
        url = "https://github.com/frankois944/googleAnalyticsKmpTracker"
        licenses {
            license {
                name = "MIT"
                url = "https://opensource.org/licenses/MIT"
            }
        }
        developers {
            developer {
                id = "frankois944"
                name = "François Dabonot"
                email = "dabonot.francois@gmail.com"
            }
        }
        scm {
            url = "https://github.com/frankois944/googleAnalyticsKmpTracker"
        }
    }
}
