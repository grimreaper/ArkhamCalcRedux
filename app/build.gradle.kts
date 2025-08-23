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
        versionName = "9.$versionCode"
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
    androidTestImplementation(libs.androidx.runner)

    implementation (libs.androidx.foundation)
    implementation (libs.androidx.foundation.layout)
    implementation (libs.androidx.material3)
    implementation (libs.androidx.runtime)
    implementation (libs.androidx.runtime.livedata)
    implementation (libs.androidx.ui)
    implementation (libs.androidx.ui.tooling)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.datastore.preferences)

    testImplementation(libs.junit)
}
