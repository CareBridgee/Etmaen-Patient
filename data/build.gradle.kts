plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.hilt)
    alias(libs.plugins.ksp)
    alias(libs.plugins.androidx.room)
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

dependencies {
    // Project modules
    implementation(project(":domain"))

    // General data dependencies
    implementation(libs.bundles.data.core)

    // Network
    implementation(libs.bundles.ktor)

    // Coroutines
    implementation(libs.bundles.coroutines)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.bundles.room)
    ksp(libs.androidx.room.compiler)

    // WorkManager
    implementation(libs.bundles.work)
    ksp(libs.androidx.hilt.compiler)

    // Paging data source
    implementation(libs.bundles.paging.data)

    // Unit tests
    testImplementation(libs.junit)
    testImplementation(libs.bundles.data.testing)
}

room {
    schemaDirectory("$projectDir/schemas")
}