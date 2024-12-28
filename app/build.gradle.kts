plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.kotlinx.serialization)
    alias(libs.plugins.realm)
    id("com.google.gms.google-services")

}
configurations.all {
    resolutionStrategy {
        force ("org.commonmark:commonmark:0.21.0") // Choose the version you prefer
    }
}
android {
    namespace = "com.zaed.chatbot"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.zaed.chatbot"
        minSdk = 24
        targetSdk = 34
        versionCode =22
        versionName = "1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
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
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    packagingOptions {
        exclude("META-INF/DEPENDENCIES")
        exclude("META-INF/LICENSE")
        exclude("META-INF/LICENSE.txt")
        exclude("META-INF/NOTICE")
        exclude("META-INF/NOTICE.txt")
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.foundation.layout.android)
    implementation(libs.language.id.common)
    implementation(libs.translate)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    implementation(libs.kotlin.compose.compiler.plugin)

    //Kotlinx-Serialization
    implementation(libs.kotlinx.serialization.json)
    //Kotlinx-DateTime
    implementation(libs.kotlinx.datetime)
    //Compose ViewModel Lifecycle
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    //Compose Navigation
    implementation(libs.androidx.navigation.compose)
    //Material3 Extended Icons
    implementation(libs.androidx.material.icons.extended)
    //Realm
    implementation (libs.realm.base)
    //Realm - If using Device Sync
    implementation (libs.realm.sync)
    //Kotlinx-Coroutines
    implementation (libs.kotlinx.coroutines.core)
    //Coil
    implementation(libs.coil.compose)
    //Koin
    implementation(libs.koin.androidx.compose)
    implementation(libs.koin.androidx.compose.navigation)
    //Google Fonts
    implementation(libs.androidx.ui.text.google.fonts)
    //Lottie
    implementation(libs.lottie.compose)
    //DataStore
    implementation(libs.androidx.datastore.preferences)
    //Google play billing
    implementation(libs.billing.ktx)
    //Google AI
    implementation("com.google.ai.client.generativeai:generativeai:0.7.0")

    implementation (platform("com.aallam.openai:openai-client-bom:3.8.2"))

    // define dependencies without versions
    implementation ("com.aallam.openai:openai-client")
    runtimeOnly ("io.ktor:ktor-client-okhttp")
    implementation("com.halilibo.compose-richtext:richtext-commonmark:1.0.0-alpha01")
    implementation("com.halilibo.compose-richtext:richtext-ui-material3:1.0.0-alpha01")
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))
    implementation("com.google.firebase:firebase-storage-ktx")
    implementation("com.google.firebase:firebase-firestore-ktx")

    implementation ("com.tom-roush:pdfbox-android:2.0.27.0")

    implementation("androidx.core:core-splashscreen:1.0.0-beta02")
    implementation("androidx.datastore:datastore-preferences:1.0.0")









}