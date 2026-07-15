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
