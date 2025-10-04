plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    // подключаем плагин сериализации (без версии, если версия задана в корневом build.gradle.kts)
    id("org.jetbrains.kotlin.plugin.serialization")
}

android {
    namespace = "com.example.caminodesantiago"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.caminodesantiago"
        minSdk = 24
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }
    // при использовании BOM, версия компилятора Compose подтягивается автоматически,
    // но при проблемах можно добавить composeOptions { kotlinCompilerExtensionVersion = "x.y.z" }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)

    implementation("androidx.compose.material:material:1.5.0")

    // === Дополнительные библиотеки для карты, картинок и сериализации ===
    // OpenStreetMap (osmdroid)
    implementation("org.osmdroid:osmdroid-android:6.1.11")

    // Coil для загрузки изображений в Compose
    implementation("io.coil-kt:coil-compose:2.4.0")

    // kotlinx.serialization (JSON)
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // Coroutines для фоновых операций
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    // ===================================================================

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}
