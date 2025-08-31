plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.kelasxi.blogapp"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.kelasxi.blogapp"
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
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    
    // CircleImageView for profile pictures
    implementation("de.hdodenhof:circleimageview:3.1.0")
    // Firebase libraries (explicit versions to avoid BOM resolution issues)
    implementation("com.google.firebase:firebase-auth-ktx:21.2.0")
    implementation("com.google.firebase:firebase-firestore-ktx:24.5.0")
    // Glide for image loading
    implementation("com.github.bumptech.glide:glide:4.15.1")
    // RecyclerView (if not included in material design)
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}