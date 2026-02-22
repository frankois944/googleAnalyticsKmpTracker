import com.android.build.api.dsl.androidLibrary
import org.jetbrains.kotlin.gradle.ExperimentalWasmDsl
import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.android.kotlin.multiplatform.library)
    alias(libs.plugins.vanniktech.mavenPublish)
    alias(libs.plugins.sqlDelight)
    alias(libs.plugins.kotlinx.serialization)
}

group = libs.versions.group.get()
val productName = "${libs.versions.productName.get()}.database"
version = libs.versions.versionName.get()

kotlin {

    explicitApi()
    compilerOptions.freeCompilerArgs.add("-Xexpect-actual-classes")

    mingwX64()
    linuxX64()
    jvm("desktop")
    androidLibrary {
        namespace = "$group.$productName.database"
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
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilerOptions {
            jvmTarget.set(
                JvmTarget.JVM_11,
            )
        }
    }
    // iosX64(),
    iosArm64()
    iosSimulatorArm64()
    // tvosX64(),
    tvosArm64()
    tvosSimulatorArm64()
    // macosX64(),
    macosArm64()
    // watchosX64(),
    watchosArm64()
    watchosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kermit)
        }
        appleMain.dependencies {
            implementation(libs.sqlite.native.driver)
        }
        linuxX64Main.dependencies {
            implementation(libs.sqlite.native.driver)
        }
        mingwX64Main.dependencies {
            implementation(libs.sqlite.native.driver)
        }
        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.sqlite.android.driver)
        }
        val desktopMain by getting {
            dependencies {
                implementation(libs.oshi.core)
                implementation(libs.sqlite.driver)
            }
        }
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

sqldelight {
    databases {
        create("CacheDatabase") {
            packageName = "$group.$productName.schema"
        }
    }
}
