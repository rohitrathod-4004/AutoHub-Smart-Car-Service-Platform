import java.util.Properties

plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.cscorner.autohub"
    compileSdk = 34

    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        localProperties.load(localPropertiesFile.inputStream())
    }

    buildFeatures {
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.cscorner.autohub"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        
        val razorpayKey = localProperties.getProperty("RAZORPAY_KEY") ?: ""
        buildConfigField("String", "RAZORPAY_KEY", "\"$razorpayKey\"")
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")

    // Firebase BOM (Bill of Materials)
    implementation(platform("com.google.firebase:firebase-bom:33.6.0"))

    // Firebase Services
    implementation("com.google.firebase:firebase-auth") // Firebase Authentication
    implementation("com.google.firebase:firebase-firestore") // Firestore
    implementation("com.google.firebase:firebase-database") // Realtime Database
    implementation("com.google.firebase:firebase-storage") // Firebase Storage

    implementation ("com.android.volley:volley:1.2.1")  //HTTP library , HTTP library
    implementation ("com.github.PhilJay:MPAndroidChart:v3.1.0")



    // Razorpay SDK
    implementation("com.razorpay:checkout:1.6.21")

    // Google Play Services
    implementation("com.google.android.gms:play-services-tasks:18.2.0")



    // UI Components
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")

    //For Glide
    implementation ("com.github.bumptech.glide:glide:4.16.0")

    // Lottie Animations
    implementation("com.airbnb.android:lottie:6.4.0")
    implementation("androidx.gridlayout:gridlayout:1.1.0")


    // Testing
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    //Location
    implementation ("com.google.android.gms:play-services-location:21.0.1")
}
