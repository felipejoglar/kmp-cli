plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.compose.compiler)
}

android {
    namespace = "com.example.kmptemplate.android"

    defaultConfig {
        applicationId = "com.example.kmptemplate.android"

        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        compileSdk = libs.versions.android.compileSdk.get().toInt()

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

    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"

            isDefault = true
        }
        create("beta") {
            dimension = "environment"
            applicationIdSuffix = ".beta"
            versionNameSuffix = "-beta"
        }
        create("prod") {
            dimension = "environment"
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }

    buildFeatures {
        buildConfig = true
        compose = true
    }
}

dependencies {
    implementation(project(":kmptemplate"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.activity.compose)
}