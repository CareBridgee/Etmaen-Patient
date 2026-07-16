plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kover)
}

android {
    namespace = "com.carenest.domain"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    // Flow and suspend APIs
    api(libs.kotlinx.coroutines.core)

    // Dates used in domain models
    api(libs.kotlinx.datetime)

    // Unit tests
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
}