plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.kapt")
    id("kotlin-parcelize")
}

android {
    namespace = "com.example.photo"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.photo"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        buildConfigField("String", "UNSPLASH_ACCESS_KEY", "\"fvABR0vxIpndmbQbirmjH9QpdV0LQDJ8yoCnOGPz_88\"")
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
        buildConfig = true
        viewBinding = true
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.13.1") // Kotlin extensions for core platform APIs
    implementation("androidx.appcompat:appcompat:1.7.1") // AppCompat support for Toolbar, themes, etc.
    implementation("com.google.android.material:material:1.13.0") // Material Components (Toolbar, buttons, cards)
    implementation("androidx.activity:activity-ktx:1.9.3") // Activity KTX helpers (ViewModel delegation, lifecycleScope)
    implementation("androidx.fragment:fragment-ktx:1.8.5") // Fragment KTX helpers
    implementation("androidx.constraintlayout:constraintlayout:2.2.1") // ConstraintLayout for responsive UI
    implementation("androidx.recyclerview:recyclerview:1.3.2") // RecyclerView for the photo list

    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.6") // ViewModel KTX (coroutines support)
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.8.6") // LiveData KTX transformations
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.6") // Lifecycle-aware coroutines

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7") // Navigation Component for fragments
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7") // Navigation UI helpers

    implementation("com.squareup.retrofit2:retrofit:2.11.0") // Retrofit HTTP client
    implementation("com.squareup.retrofit2:converter-moshi:2.11.0") // Moshi converter for JSON serialization
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0") // OkHttp logging interceptor for debugging
    implementation("com.squareup.moshi:moshi-kotlin:1.15.1") // Moshi Kotlin adapters

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.9.0") // Coroutines support on Android

    implementation("androidx.room:room-runtime:2.6.1") // Room persistence runtime
    implementation("androidx.room:room-ktx:2.6.1") // Room KTX (suspend functions, Flow)
    kapt("androidx.room:room-compiler:2.6.1") // Room annotation processor

    implementation("io.coil-kt:coil:2.7.0") // Coil image loading into ImageViews

    testImplementation("junit:junit:4.13.2") // Unit testing framework
    androidTestImplementation("androidx.test.ext:junit:1.2.1") // AndroidX JUnit extensions for instrumentation tests
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1") // Espresso UI testing (view interactions)
}
