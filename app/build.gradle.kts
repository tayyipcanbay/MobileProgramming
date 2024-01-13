plugins {
    id("com.android.application");
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.mobileprogramming"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mobileprogramming"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    // Firebase BoM (Bill of Materials): Firebase kütüphanelerinin sürümlerini koordine etmek için kullanılır.
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))

// Firebase Analytics: Kullanıcı davranışlarını ve uygulama performansını izlemek ve analiz etmek için kullanılır.
    implementation("com.google.firebase:firebase-analytics")

// Firebase Firestore: Bulut tabanlı Firestore veritabanına erişim sağlar.
    implementation("com.google.firebase:firebase-firestore:24.0.0")

// AndroidX Annotation: AndroidX kütüphaneleri ile uyumlu annotasyonları içerir.
    implementation("androidx.annotation:annotation:1.3.0")

// Firebase Storage: Firebase bulut depolama hizmetine erişim sağlar.
    implementation("com.google.firebase:firebase-storage")

// Picasso: Resim yükleme ve gösterme kütüphanesi.
    implementation("com.squareup.picasso:picasso:2.71828")
}