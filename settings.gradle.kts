// Top-level settings file for the GroupWork Manager Android application
// Defines the modules included in the build. Currently there is only the
// application module called "app".

pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
    plugins {
        id("com.android.application") version "8.2.0"
        id("org.jetbrains.kotlin.android") version "1.9.10"
    }
}

rootProject.name = "GroupWorkManagerApp"
include(":app")