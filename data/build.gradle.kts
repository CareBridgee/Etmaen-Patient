import com.android.build.gradle.internal.cxx.configure.gradleLocalProperties

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
    buildTypes {
        getByName("debug") {
            buildConfigField(
                type = "String",
                name = "base_url",
                value = "\"${gradleLocalProperties(rootDir, providers).getProperty("base_url")}\""
            )
            buildConfigField(
                type = "String",
                name = "api_key",
                value = "\"${gradleLocalProperties(rootDir, providers).getProperty("api_key")}\""
            )
            buildConfigField(
                type = "String",
                name = "location_iq_token",
                value = "\"${gradleLocalProperties(rootDir, providers).getProperty("location_iq_token")}\""
            )
            buildConfigField(
                type = "String",
                name = "cloudinary_cloud_name",
                value = "\"${gradleLocalProperties(rootDir, providers).getProperty("cloudinary_cloud_name", "jonkn070")}\""
            )
            buildConfigField(
                type = "String",
                name = "cloudinary_upload_preset",
                value = "\"${gradleLocalProperties(rootDir, providers).getProperty("cloudinary_upload_preset", "home_nursing_images")}\""
            )
            buildConfigField(
                type = "String",
                name = "cloudinary_asset_folder",
                value = "\"${gradleLocalProperties(rootDir, providers).getProperty("cloudinary_asset_folder", "home-nursing/profiles")}\""
            )
            buildConfigField(
                type = "String",
                name = "cloudinary_upload_url",
                value = "\"${gradleLocalProperties(rootDir, providers).getProperty("cloudinary_upload_url", "https://api.cloudinary.com/v1_1/jonkn070/image/upload")}\""
            )
        }
        getByName("release") {
            buildConfigField(
                type = "String",
                name = "base_url",
                value = "\"${gradleLocalProperties(rootDir, providers).getProperty("base_url")}\""
            )
            buildConfigField(
                type = "String",
                name = "api_key",
                value = "\"${gradleLocalProperties(rootDir, providers).getProperty("api_key")}\""
            )
            buildConfigField(
                type = "String",
                name = "location_iq_token",
                value = "\"${gradleLocalProperties(rootDir, providers).getProperty("location_iq_token")}\""
            )
            buildConfigField(
                type = "String",
                name = "cloudinary_cloud_name",
                value = "\"${gradleLocalProperties(rootDir, providers).getProperty("cloudinary_cloud_name", "jonkn070")}\""
            )
            buildConfigField(
                type = "String",
                name = "cloudinary_upload_preset",
                value = "\"${gradleLocalProperties(rootDir, providers).getProperty("cloudinary_upload_preset", "home_nursing_images")}\""
            )
            buildConfigField(
                type = "String",
                name = "cloudinary_asset_folder",
                value = "\"${gradleLocalProperties(rootDir, providers).getProperty("cloudinary_asset_folder", "home-nursing/profiles")}\""
            )
            buildConfigField(
                type = "String",
                name = "cloudinary_upload_url",
                value = "\"${gradleLocalProperties(rootDir, providers).getProperty("cloudinary_upload_url", "https://api.cloudinary.com/v1_1/jonkn070/image/upload")}\""
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        buildConfig = true
    }

}

dependencies {
    // Project modules
    implementation(project(":domain"))

    // General data dependencies
    implementation(libs.bundles.data.core)

    // Network
    implementation(libs.bundles.ktor)
    implementation(libs.okhttp3)
    implementation(libs.okhttp3.logging)

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