plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    id("androidx.navigation.safeargs.kotlin")
    id("org.sonarqube") version "5.0.0.4638"
}

sonar {
    properties {
        property("sonar.projectKey", "Dispatch")
        property("sonar.projectName", "Dispatch")
        property("sonar.host.url", "http://localhost:9000")
        property("sonar.token", "sqp_11c201d9402944047495cc86bbd6a4af66665535")
    }
}

android {
    namespace = "com.android.dispatch"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.android.dispatch"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    viewBinding {
        enable = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    implementation (libs.androidx.room.ktx)
    implementation (libs.androidx.room.runtime)

    implementation (libs.androidx.navigation.fragment.ktx)
    implementation (libs.androidx.navigation.ui.ktx)
    implementation (libs.androidx.legacy.support.v4)

    implementation (libs.retrofit)
    implementation (libs.converter.gson)

    implementation (libs.lottie)

    implementation (libs.core)
    implementation(libs.zxing.android.embedded)
}