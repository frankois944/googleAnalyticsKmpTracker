import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "io.github.frankois944.matomoKMPTracker"
version = libs.versions.libaryVersion.get()
val productName = "core"

kotlin {

    explicitApi()
    jvm("desktop")
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
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
        }
    }
}

android {
    namespace = "$group.$productName"
    compileSdk =
        libs.versions.android.compileSdk
            .get()
            .toInt()
    defaultConfig {
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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
        name = "Matomo KMP Tracker Core"
        description = "A Matomo client tracker for Kotlin Multiplatform"
        inceptionYear = "2025"
        url = "https://github.com/frankois944/matomoKMPTracker"
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
            url = "https://github.com/frankois944/matomoKMPTracker"
        }
    }
}
