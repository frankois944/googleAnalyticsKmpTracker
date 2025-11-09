import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.sqlDelight)
}

group = "io.github.frankois944"
version = "0.1.1"

kotlin {
    explicitApi()
    jvm("desktop")
    androidTarget {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_11)
        }
        publishLibraryVariants("release", "debug")
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
        outputModuleName = "matomoKMPTracker"
        browser {
            testTask {
                useKarma {
                    useChrome()
                }
            }
        }
        binaries.executable()
    }

    js {
        browser {
            testTask {
                useKarma {
                    useChrome()
                }
            }
        }
        binaries.executable()
    }

    sourceSets {
        commonMain.dependencies {
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.content.negotiation)
            implementation(libs.ktor.content.encoding)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.logging)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.serialization.cbor)
            implementation(libs.kotlinx.datetime)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        appleMain.dependencies {
            implementation(libs.ktor.client.darwin)
            implementation(libs.native.driver)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.android.driver)
        }
        androidUnitTest.dependencies {
            implementation(libs.robolectric)
            implementation(libs.mockito.core)
            implementation(libs.core)
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.ktor.client.java)
                implementation(libs.sqlite.driver)
                implementation(libs.oshi.core)
                implementation(libs.okio)
            }
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(libs.web.worker.driver.wasm.js)
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.1.0"))
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
            implementation(npm("sql.js", "1.8.0"))
            implementation(npm("@js-joda/timezone", "2.22.0"))
        }

        jsMain.dependencies {
            implementation(libs.ktor.client.js)
            implementation(npm("@cashapp/sqldelight-sqljs-worker", "2.1.0"))
            implementation(libs.web.worker.driver)
            implementation(devNpm("copy-webpack-plugin", "9.1.0"))
            implementation(npm("sql.js", "1.8.0"))
            implementation(npm("@js-joda/timezone", "2.22.0"))
        }
    }
}

android {
    namespace = "io.github.frankois944.matomoKMPTracker"
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
    publishToMavenCentral()

    signAllPublications()

    coordinates(
        group.toString(),
        "matomoKMPTracker",
        version.toString(),
    )

    pom {
        name = "Matomo KMP Tracker"
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

sqldelight {
    databases {
        create("CacheDatabase") {
            packageName = "io.github.frankois944.matomoKMPTracker"
            generateAsync = true
        }
    }
}

val deviceName = project.findProperty("device") as? String ?: "testing"

tasks.register<Exec>("bootIOSSimulator") {
    isIgnoreExitValue = true
    commandLine("xcrun", "simctl", "boot", deviceName)

    doLast {
        val result = executionResult.get()
        if (result.exitValue != 148 && result.exitValue != 149) { // ignoring device already booted errors
            result.assertNormalExitValue()
        }
    }
}

tasks.withType<KotlinNativeSimulatorTest>().configureEach {
    dependsOn("bootIOSSimulator")
    standalone.set(false)
    device.set(deviceName)
}
