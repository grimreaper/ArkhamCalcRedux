plugins {
    id("com.android.application")
    alias(libs.plugins.kotlin.android)
}

android {
    compileSdk = 36
    defaultConfig {
        applicationId = "com.eitanadler.arkhamcalcredux2"
        minSdk = 26
        targetSdk = 36
        versionCode = 10
        versionName = "8.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        debug {
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
    namespace = "com.eitanadler.arkhamcalcredux2"
    kotlin {
        jvmToolchain(21)
        explicitApi()
    }
}

dependencies {
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.material)
    implementation(libs.androidx.core.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.runner)
    androidTestImplementation(libs.androidx.espresso.core)
}
