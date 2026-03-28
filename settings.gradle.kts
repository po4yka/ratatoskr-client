pluginManagement {
    includeBuild("build-logic")

    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

rootProject.name = "BiteSizeReader"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

include(":core")
include(":composeApp")
include(":shared")
include(":feature:auth")
include(":feature:collections")
include(":feature:digest")
include(":feature:settings")
include(":feature:summary")
include(":feature:sync")
