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
    alias(libs.plugins.sortDependencies) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.versionSort)
}

allprojects {
    repositories {
        google()
        mavenCentral()

    }
}