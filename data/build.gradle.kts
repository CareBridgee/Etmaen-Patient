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

dependencies{

    implementation(project(":domain"))

    implementation(libs.androidx.core.ktx)

    // Ktor
    implementation(libs.bundles.ktor)

    // JSON Serialization
    implementation(libs.kotlinx.serialization)

    // Coroutines
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // Date and time
    implementation(libs.kotlinx.datetime)

    // Hilt
    implementation(libs.hilt.android)
    ksp(libs.hilt.compiler)

    // Room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    // DataStore
    implementation(libs.androidx.datastore.preferences)

    // WorkManager
    implementation(libs.androidx.work.runtime.ktx)

    // Hilt with WorkManager
    implementation(libs.androidx.hilt.work)
    ksp(libs.androidx.hilt.compiler)

    // Paging data source
    implementation(libs.androidx.paging.runtime)

    // Room + Paging integration
    implementation(libs.androidx.room.paging)

    // Device location
    implementation(libs.play.services.location)

    // Unit tests
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test)
    testImplementation(libs.ktor.client.mock)
    testImplementation(libs.androidx.room.testing)

}

room {
    schemaDirectory("$projectDir/schemas")
}