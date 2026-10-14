plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    // Package ID disepakati pengguna (CP0->CP1): com.dtxpads.vtuber.
    // Tidak sama dengan DTX Kendang (applicationId com.dtxpads.kendang, namespace com.kendang.realpads).
    namespace = "com.dtxpads.vtuber"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.dtxpads.vtuber"
        minSdk = 26
        targetSdk = 36
        versionCode = 2
        versionName = "0.2.0-cp2"
    }

    buildFeatures {
        compose = true
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    packaging {
        resources.excludes.add("META-INF/*")
    }
}

dependencies {
    // Versi sama dengan DTX Kendang (terbukti dibuild di CI pengguna).
    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.06.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-icons-extended")

    // CP2: impor PNG di thread latar (Dispatchers.IO).
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1")

    // Unit test JVM murni (logika non-Android).
    testImplementation("junit:junit:4.13.2")
}
