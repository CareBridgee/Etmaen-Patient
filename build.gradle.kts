// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
    alias(libs.plugins.kover)
}

dependencies {
    kover(project(":app"))
    kover(project(":presentation"))
    kover(project(":data"))
    kover(project(":domain"))
    kover(project(":designsystem"))
}

kover {
    reports {
        total {
            filters {
                excludes {
                    // Exclude generated Android classes from coverage
                    classes(
                        "*.R",
                        "*.R$*",
                        "*.*_HiltModules*",
                        "*.*_Factory*",
                        "*.*_MembersInjector*",
                        "*.Hilt_*",
                        "*.BuildConfig",
                        "*.Manifest*",
                        "*.*_Impl*",
                        "*.*Directions*",
                        "*.*Args*"
                    )
                    // Exclude Android-generated packages
                    packages(
                        "hilt_aggregated_deps",
                        "dagger.hilt.internal.aggregatedroot.codegen"
                    )
                }
            }

            verify {
                rule("Minimum line coverage") {
                    bound {
                        minValue = 80
                        coverageUnits = kotlinx.kover.gradle.plugin.dsl.CoverageUnit.LINE
                        aggregationForGroup = kotlinx.kover.gradle.plugin.dsl.AggregationType.COVERED_PERCENTAGE
                    }
                }
            }

            html {
                onCheck = false
            }

            xml {
                onCheck = false
            }
        }
    }
}