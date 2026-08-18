import java.security.KeyStore
import java.security.MessageDigest

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
}

android {
    namespace = "com.carenest"
    compileSdk = 37

    signingConfigs {
        create("debugShared") {
            val rootKeystore = project.rootProject.file("debug.keystore")
            val appKeystore = file("debug.keystore")
            val userKeystore = file("${System.getProperty("user.home")}/.android/debug.keystore")

            storeFile = when {
                appKeystore.exists() -> appKeystore
                rootKeystore.exists() -> rootKeystore
                userKeystore.exists() -> userKeystore
                else -> userKeystore
            }
            storePassword = "android"
            keyAlias = "androiddebugkey"
            keyPassword = "android"
        }
    }

    defaultConfig {
        applicationId = "com.carenest"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
        debug {
            signingConfig = signingConfigs.getByName("debugShared")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
        dataBinding = true
    }
}

tasks.whenTaskAdded {
    if (name == "preBuild") {
        dependsOn("printSha1")
    }
}

tasks.register("printSha1") {
    group = "help"
    description = "Prints the SHA-1 and SHA-256 fingerprints of the project's signing certificate for Google Cloud Console."
    val keystoreFile = project.rootProject.file("debug.keystore")
    doLast {
        if (keystoreFile.exists()) {
            val keystore = KeyStore.getInstance(KeyStore.getDefaultType())
            keystoreFile.inputStream().use { stream -> keystore.load(stream, "android".toCharArray()) }
            val cert = keystore.getCertificate("androiddebugkey")
            val sha1Bytes = MessageDigest.getInstance("SHA-1").digest(cert.encoded)
            val sha1Hex = sha1Bytes.joinToString(":") { byte -> "%02X".format(byte) }
            val sha256Bytes = MessageDigest.getInstance("SHA-256").digest(cert.encoded)
            val sha256Hex = sha256Bytes.joinToString(":") { byte -> "%02X".format(byte) }

            println("==========================================================")
            println("CARENEST-PATIENT SIGNING FINGERPRINTS")
            println("Package Name: com.carenest")
            println("Keystore:     ${keystoreFile.absolutePath}")
            println("SHA-1:       $sha1Hex")
            println("SHA-256:     $sha256Hex")
            println("==========================================================")
        } else {
            println("Keystore file not found at ${keystoreFile.absolutePath}")
        }
    }
}

dependencies {

    // Project modules
    implementation(project(":presentation"))
    implementation(project(":data"))
    implementation(project(":domain"))

    // AndroidX
    implementation(libs.bundles.androidx.core)

    // Compose
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.bundles.compose)

    // Splash
    implementation(libs.androidx.core.splashscreen)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Unit tests
    testImplementation(libs.junit)

    // Android tests
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Compose tests
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)

    // Hilt tests
    androidTestImplementation(libs.hilt.android.testing)
    kspAndroidTest(libs.hilt.compiler)

    // Debug only
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
