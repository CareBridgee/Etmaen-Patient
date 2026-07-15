plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kover)
}

android {
    namespace = "com.carenest.data"
    compileSdk = 37

    defaultConfig {
        minSdk = 24
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

}

dependencies{

    implementation(project(":domain"))

}
