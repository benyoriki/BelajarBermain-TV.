import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

// Tanda tangan rilis (opsional): buat keystore.properties di root proyek (lihat README). Tanpa file ini,
// build "release" tetap jalan tetapi TIDAK bertanda tangan; gunakan assembleDebug untuk uji di TV.
val ksFile = rootProject.file("keystore.properties")
val ks = Properties().apply { if (ksFile.exists()) ksFile.inputStream().use { load(it) } }

android {
    namespace = "com.benyoriki.belajarbermain"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.benyoriki.belajarbermain"
        minSdk = 26          // Android 8.0 ke atas (periksa versi Android pada TV Anda di Pengaturan → Tentang)
        targetSdk = 35
        versionCode = 1
        versionName = "1.0.0"

        // Alamat situs. Ganti di sini bila domain berubah. Wajib HTTPS.
        buildConfigField("String", "START_URL", "\"https://benyoriki.github.io/BelajarBermain/\"")
        // Host yang boleh dibuka di dalam aplikasi (situs ini mengalihkan ke belajarbermain.online; "Mulai Cerita" ke Kisah3Dino di github.io)
        buildConfigField("String[]", "ALLOWED_HOSTS", "{\"benyoriki.github.io\", \"belajarbermain.online\"}")
    }

    signingConfigs {
        if (ksFile.exists()) {
            create("release") {
                storeFile = rootProject.file(ks.getProperty("storeFile"))
                storePassword = ks.getProperty("storePassword")
                keyAlias = ks.getProperty("keyAlias")
                keyPassword = ks.getProperty("keyPassword")
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = true
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            if (ksFile.exists()) signingConfig = signingConfigs.getByName("release")
        }
    }

    buildFeatures { buildConfig = true }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }
}

dependencies {
    implementation("androidx.core:core-ktx:1.15.0")
    implementation("androidx.activity:activity-ktx:1.9.3")
}
