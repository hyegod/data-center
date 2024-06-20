plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.next.up.code.core"
    compileSdk = 34

    defaultConfig {
        minSdk = 26

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://data-canter.taekwondosulsel.info/api/\""
            )
            buildConfigField(
                "String",
                "hostname1",
                "\"sha256/FPIgB5kmDjb/gXXxUPjsO/Yjzl+wCgxaPuUjbI7wXHs=\""
            )
            buildConfigField(
                "String",
                "hostname2",
                "\"sha256/jQJTbIh0grw0/1TkHSumWb+Fs0Ggogr621gT3PvPKG0=\""
            )
            buildConfigField(
                "String",
                "hostname3",
                "\"sha256/C5+lpZ7tcVwmwQIMcRtPbsQtWLABXhQzejna0wHFr8M=\""
            )
        }
        debug {
            buildConfigField(
                "String",
                "BASE_URL",
                "\"https://data-canter.taekwondosulsel.info/api/\""
            )
            buildConfigField(
                "String",
                "hostname1",
                "\"sha256/FPIgB5kmDjb/gXXxUPjsO/Yjzl+wCgxaPuUjbI7wXHs=\""
            )
            buildConfigField(
                "String",
                "hostname2",
                "\"sha256/jQJTbIh0grw0/1TkHSumWb+Fs0Ggogr621gT3PvPKG0=\""
            )
            buildConfigField(
                "String",
                "hostname3",
                "\"sha256/C5+lpZ7tcVwmwQIMcRtPbsQtWLABXhQzejna0wHFr8M=\""
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
        viewBinding = true
        buildConfig = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    /* Scale*/
    implementation("com.intuit.sdp:sdp-android:1.1.0")
    implementation("com.intuit.ssp:ssp-android:1.1.0")

    /* rx java */
    implementation("com.jakewharton.rxbinding2:rxbinding:2.0.0")

    /* Web implementation */
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

    /* Koin */
    implementation("io.insert-koin:koin-android-compat:3.4.3")

    /*Lifecycle*/
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")

    /*retrofit + rxJava */
    implementation("com.squareup.retrofit2:adapter-rxjava2:2.9.0")

    // Picasso
    implementation("com.squareup.picasso:picasso:2.71828")

    implementation ("org.apache.poi:poi:4.1.2")
    implementation ("org.apache.poi:poi-ooxml:4.1.2")

    /*Data Store*/
    implementation ("androidx.datastore:datastore-core:1.0.0")
    implementation ("androidx.datastore:datastore-preferences:1.0.0")

    /*Jetpack Security*/
    implementation ("androidx.security:security-crypto:1.1.0-alpha05")
    implementation ("com.scottyab:secure-preferences-lib:0.1.7")
    // Helper
    implementation("com.github.TistoW:MyHelper:1.1.26")

    // Room
    implementation("androidx.room:room-runtime:2.5.2")
    implementation("androidx.room:room-ktx:2.5.2")
    ksp("androidx.room:room-compiler:2.5.2")

}