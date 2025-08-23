buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath(libs.gradle)
    }
}
plugins {
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.sortDependencies) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()

    }
}