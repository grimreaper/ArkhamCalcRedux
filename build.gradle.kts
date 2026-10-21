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
    alias(libs.plugins.aboutLibraries)
    alias(libs.plugins.aboutLibrariesAndroid)
//    alias(libs.plugins.dependencyAnalysis)
//    alias(libs.plugins.sortDependencies) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.versionCatalogUpdate)

}

allprojects {
    repositories {
        google()
        mavenCentral()
    }
}