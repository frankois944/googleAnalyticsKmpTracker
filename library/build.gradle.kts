import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.kotlinx.serialization)
}

group = libs.versions.group.get()
val productName = libs.versions.productName.get()
version = libs.versions.versionName.get()

kotlin {

    applyDefaultHierarchyTemplate()
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")
    explicitApi()

    mingwX64()
    linuxX64()
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
        /*withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }*/

        compilerOptions {
            jvmTarget.set(
                JvmTarget.JVM_11,
            )
        }
    }
    listOf(
        // iosX64(),
        iosArm64(),
        iosSimulatorArm64(),
        // tvosX64(),
        tvosArm64(),
        tvosSimulatorArm64(),
        // macosX64(),
        macosArm64(),
        // watchosX64(),
        watchosArm64(),
        watchosSimulatorArm64(),
    ).forEach {
        it.binaries.getTest("debug").apply {
            linkerOpts +=
                listOf(
                    "-lsqlite3",
                )
        }
        /*it.binaries.framework {
            baseName = productName
            binaryOption("bundleId", "${group}${productName.lowercase()}")
            binaryOption("bundleVersion", version.toString())
            linkerOpts("-lsqlite3")
        }*/
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

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kermit)
        }
        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.kotlinx.coroutines.test)
            implementation(libs.kermit.test)
        }
        appleMain.dependencies {
            implementation(ktorLibs.client.darwin)
        }
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation(ktorLibs.client.android)
        }
        webMain.dependencies {
            implementation(libs.kotlinx.browser)
        }
        val androidHostTest by getting {
            dependencies {
                implementation(libs.robolectric)
                implementation(libs.mockito.core)
                implementation(libs.core)
            }
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.oshi.core)
                implementation(ktorLibs.client.java)
            }
        }
        val desktopTest by getting

        val nonWebMain by creating {
            dependsOn(commonMain.get())
            dependencies {
                implementation(libs.okio)
                implementation(ktorLibs.client.core)
                implementation(ktorLibs.client.contentNegotiation)
                implementation(ktorLibs.client.encoding)
                implementation(ktorLibs.serialization.kotlinx.json)
                implementation(ktorLibs.client.logging)
                implementation(libs.kotlinx.serialization.json)
                implementation(libs.kotlinx.serialization.cbor)
                implementation(project(":database"))
            }
        }
        androidMain.get().dependsOn(nonWebMain)
        nativeMain.get().dependsOn(nonWebMain)
        desktopMain.dependsOn(nonWebMain)
        val nonWebTest by creating {
            dependsOn(commonTest.get())
        }
        androidHostTest.dependsOn(nonWebTest)
        nativeTest.get().dependsOn(nonWebTest)
        desktopTest.dependsOn(nonWebTest)
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), productName, version.toString())

    pom {
        name = "My library"
        description = "A library."
        inceptionYear = "2024"
        url = "https://github.com/kotlin/multiplatform-library-template/"
        licenses {
            license {
                name = "XXX"
                url = "YYY"
                distribution = "ZZZ"
            }
        }
        developers {
            developer {
                id = "XXX"
                name = "YYY"
                url = "ZZZ"
            }
        }
        scm {
            url = "XXX"
            connection = "YYY"
            developerConnection = "ZZZ"
        }
    }
}
