@file:OptIn(ExperimentalKotlinGradlePluginApi::class)
@file:Suppress("UnstableApiUsage")

import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalKotlinGradlePluginApi
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeSimulatorTest
import java.util.Locale

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.kotlinx.serialization)
}

group = "io.github.frankois944"
val productName = "googleAnalyticsKMPTracker"
version = "${libs.versions.libaryVersion.get()}"

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
        withHostTestBuilder {}.configure {}
        compilerOptions {
            jvmTarget.set(
                JvmTarget.JVM_11,
            )
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        tvosX64(),
        tvosArm64(),
        tvosSimulatorArm64(),
        macosX64(),
        macosArm64(),
        watchosX64(),
        watchosArm64(),
        watchosSimulatorArm64(),
    ).forEach {
        it.binaries.getTest("debug").apply {
            linkerOpts +=
                listOf(
                    "-lsqlite3",
                )
        }
        it.binaries.framework {
            baseName =
                productName.replaceFirstChar { name ->
                    if (name.isLowerCase()) name.titlecase(Locale.getDefault()) else name.toString()
                }
            binaryOption("bundleId", "${group}${productName.lowercase()}")
            binaryOption("bundleVersion", version.toString())
            export(project(":core"))
            linkerOpts("-lsqlite3")
        }
    }

    @OptIn(ExperimentalWasmDsl::class)
    wasmJs {
        outputModuleName = productName
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

    dependencies {
        implementation(libs.ktor.client.core)
        implementation(libs.ktor.content.negotiation)
        implementation(libs.ktor.content.encoding)
        implementation(libs.ktor.serialization.kotlinx.json)
        implementation(libs.ktor.logging)
        implementation(libs.kotlinx.coroutines.core)
        implementation(libs.kotlinx.serialization.json)
        implementation(libs.kotlinx.datetime)
        implementation(project(":database"))
        api(project(":core"))
    }

    sourceSets {
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
        }
        appleMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
        androidMain.dependencies {
            implementation(libs.ktor.client.okhttp)
            implementation(libs.kotlinx.coroutines.android)
        }
        getByName("androidHostTest").dependencies {
            implementation(libs.robolectric)
            implementation(libs.mockito.core)
            implementation(libs.core)
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.ktor.client.java)
                implementation(libs.oshi.core)
            }
        }
        wasmJsMain.dependencies {
            implementation(libs.ktor.client.js)
        }

        jsMain.dependencies {
            implementation(libs.ktor.client.js)
        }
    }
}

mavenPublishing {
    publishToMavenCentral(true)

    signAllPublications()

    coordinates(
        group.toString(),
        productName,
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
