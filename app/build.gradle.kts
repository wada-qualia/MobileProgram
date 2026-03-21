plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.kotlin.serialization)
}

import org.gradle.api.GradleException

// Get version from parent project (set by version.gradle.kts)
val appVersionName: String = rootProject.extra.get("versionName") as String
val appVersionCode: Int = rootProject.extra.get("versionCode") as Int

fun requiredConfig(propertyName: String, envName: String): String =
    providers.gradleProperty(propertyName)
        .orElse(providers.environmentVariable(envName))
        .orNull
        ?.takeIf { it.isNotBlank() }
        ?: throw GradleException("Missing required configuration: set $propertyName or $envName")

val devBaseUrl = requiredConfig("russifyDevBaseUrl", "RUSSIFY_DEV_BASE_URL")
val prodBaseUrl = requiredConfig("russifyProdBaseUrl", "RUSSIFY_PROD_BASE_URL")
val devStorageBaseUrl = requiredConfig("russifyDevStorageBaseUrl", "RUSSIFY_DEV_STORAGE_BASE_URL")
val prodStorageBaseUrl = requiredConfig("russifyProdStorageBaseUrl", "RUSSIFY_PROD_STORAGE_BASE_URL")

android {
    namespace = "com.example.Russify"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.Russify"
        minSdk = 24
        targetSdk = 35

        // Dynamic versioning from git tags
        versionCode = appVersionCode
        versionName = appVersionName

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        vectorDrawables {
            useSupportLibrary = true
        }
    }

    /**
     * Product Flavors для разных окружений
     *
     * dev - для разработки с локальным backend
     * prod - для production с реальным backend URL
     */
    flavorDimensions += "environment"
    productFlavors {
        create("dev") {
            dimension = "environment"
            applicationIdSuffix = ".dev"
            versionNameSuffix = "-dev"

            // BuildConfig для dev окружения
            buildConfigField("String", "BASE_URL", "\"$devBaseUrl\"")
            buildConfigField("String", "STORAGE_BASE_URL", "\"$devStorageBaseUrl\"")
            buildConfigField("String", "ENVIRONMENT", "\"development\"")
            buildConfigField("boolean", "ENABLE_LOGGING", "true")

            // App name будет "Russify Dev" из src/dev/res/values/strings.xml
        }

        create("prod") {
            dimension = "environment"

            buildConfigField("String", "BASE_URL", "\"$prodBaseUrl\"")
            buildConfigField("String", "STORAGE_BASE_URL", "\"$prodStorageBaseUrl\"")
            buildConfigField("String", "ENVIRONMENT", "\"production\"")
            buildConfigField("boolean", "ENABLE_LOGGING", "false")

            // App name будет "Russify" из src/prod/res/values/strings.xml
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            applicationIdSuffix = ".debug"
            versionNameSuffix = "-debug"
        }

        release {
            isDebuggable = false
            isMinifyEnabled = true
            isShrinkResources = true

            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )

            // Signing config для release сборки
            // Раскомментируйте и настройте при необходимости
            // signingConfig = signingConfigs.getByName("release")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    buildFeatures {
        compose = true
        buildConfig = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.15"
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

// Kotlin toolchain configuration
kotlin {
    jvmToolchain(11)
}

dependencies {
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.androidx.compose.foundation)
    implementation(libs.androidx.foundation)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
    implementation("androidx.navigation:navigation-compose:2.7.7")
    implementation("androidx.compose.material:material-icons-extended:1.6.1")
    implementation("com.google.code.gson:gson:2.10.1")
    val ktorVersion = "2.3.8"
    implementation("io.ktor:ktor-client-core:$ktorVersion")
    implementation("io.ktor:ktor-client-android:$ktorVersion")
    implementation("io.ktor:ktor-client-content-negotiation:$ktorVersion")
    implementation("io.ktor:ktor-serialization-kotlinx-json:$ktorVersion")
    implementation("io.ktor:ktor-client-logging:$ktorVersion")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")
    val media3Version = "1.2.1"
    implementation("androidx.media3:media3-exoplayer:$media3Version")
    implementation("androidx.media3:media3-common:$media3Version")
}
