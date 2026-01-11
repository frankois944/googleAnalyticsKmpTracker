@file:Suppress("UnstableApiUsage")
@file:OptIn(ExperimentalKotlinGradlePluginApi::class)

import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.vanniktech.mavenPublish)
}

group = "io.github.frankois944.googleAnalyticsKMPTracker"
val productName = "database"
version = libs.versions.libaryVersion.get()

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

    dependencies {
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlinx.serialization.cbor)
        implementation(libs.kotlinx.datetime)
        implementation(project(":core"))
    }

    sourceSets {
        appleMain.dependencies {
            implementation(libs.native.driver)
        }
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.android.driver)
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.sqlite.driver)
                implementation(libs.okio)
            }
        }
        wasmJsMain.dependencies {
            implementation(libs.web.worker.driver.wasm.js)
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.1.0"))
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
            implementation(npm("sql.js", "1.8.0"))
            implementation(npm("@js-joda/timezone", "2.22.0"))
        }

        jsMain.dependencies {
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.1.0"))
            implementation(libs.web.worker.driver)
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
            implementation(npm("sql.js", "1.8.0"))
            implementation(npm("@js-joda/timezone", "2.22.0"))
        }
    }
}

sqldelight {
    databases {
        create("CacheDatabase") {
            packageName = "io.github.frankois944.googleAnalyticsKMPTracker.schema"
            generateAsync = true
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
