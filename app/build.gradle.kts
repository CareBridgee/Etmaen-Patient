import java.util.Properties
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.kover)
}
val localProperties = Properties().apply {
    val file = rootProject.file("local.properties")

    if (file.exists()) {
        file.inputStream().use(::load)
    }
}

fun localProperty(name: String): String {
    return localProperties.getProperty(name).orEmpty()
}

fun String.toBuildConfigString(): String {
    val escapedValue = replace("\\", "\\\\")
        .replace("\"", "\\\"")

    return "\"$escapedValue\""
}

android {
    namespace = "com.carenest"
    compileSdk = 37

    defaultConfig {
        applicationId = "com.carenest"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        buildConfigField(
            "String",
            "PAYMOB_PUBLIC_KEY",
            localProperty("PAYMOB_PUBLIC_KEY").toBuildConfigString()
        )

        buildConfigField(
            "String",
            "PAYMOB_SECRET_KEY",
            localProperty("PAYMOB_SECRET_KEY").toBuildConfigString()
        )

        buildConfigField(
            "String",
            "PAYMOB_INTEGRATION_ID",
            localProperty("PAYMOB_INTEGRATION_ID").toBuildConfigString()
        )
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        dataBinding = true
        buildConfig = true
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

    implementation(libs.paymob.sdk)
}