@file:Suppress("UnstableApiUsage")

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.android.application)
    alias(libs.plugins.gms.google.services)
    alias(libs.plugins.ksp)
    alias(libs.plugins.firebase.crashlytics)
}


android {
    namespace = "cz.jaro.rozvrh"
    compileSdk = 33
    buildToolsVersion = "31.0.0"

    defaultConfig {
        applicationId = "cz.jaro.rozvrh"
        minSdk = 26
        targetSdk = 33
        versionCode = 5
        versionName = "2.1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
        freeCompilerArgs += "-Xcontext-receivers"
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.7"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    applicationVariants.forEach { variant ->
        sourceSets.getByName(variant.name) {
            java {
                srcDir("build/generated/ksp/${variant.name}/kotlin")
            }
        }
    }
    testOptions {
        unitTests.isReturnDefaultValues = true
    }
}

dependencies {
    //noinspection UseTomlInstead
    implementation("androidx.core:core-ktx:1.10.1")
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlin.stdlib)
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.bundles.androidx.navigation)
    implementation(libs.bundles.androidx.jetpack.compose)
    implementation(libs.bundles.androidx.jetpack.glance)
    implementation(libs.firebase.analytics.ktx)
    implementation(libs.firebase.crashlytics.ktx)
    implementation(libs.firebase.database.ktx)
    implementation(libs.firebase.config.ktx)
    implementation(libs.jsoup)
    implementation(libs.androidx.datastore.preferences)
    implementation(libs.bundles.koin)
    testImplementation(libs.junit.jupiter)
    ksp(libs.koin.ksp.compiler)
    implementation(libs.compose.destinations.core)
    ksp(libs.compose.destinations.ksp)
    implementation(libs.compose.material3.datetime.pickers)
    implementation(libs.semver)

    testImplementation(libs.json)
}

tasks.withType<Test> {
    useJUnitPlatform()
}

repositories {
    google()
    mavenCentral()
}