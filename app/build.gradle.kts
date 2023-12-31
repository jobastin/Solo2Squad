plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.solo2squad"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.solo2squad"
        minSdk = 24
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures{
        viewBinding=true;
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth-ktx:22.1.2")
    implementation("com.google.firebase:firebase-database-ktx:20.2.2")
    implementation("com.google.firebase:firebase-storage:20.3.0")
    implementation("com.google.firebase:firebase-auth:22.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.firebase:firebase-auth-ktx")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation(platform("com.google.firebase:firebase-bom:32.5.0"))
    implementation("com.google.firebase:firebase-database")
    implementation("mysql:mysql-connector-java:5.1.49")
    //implementation files("libs/mysql-connector-java-x.x.x.jar")
    implementation ("com.github.bumptech.glide:glide:4.12.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")


    //Test cases imports
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-intents:3.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-contrib:3.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-accessibility:3.4.0")
    androidTestImplementation("androidx.test.espresso:espresso-web:3.4.0")
    implementation("software.aws.rds:aws-mysql-jdbc:1.1.11")

    //payment gateway
//    implementation("com.stripe:stripe-java:20.77.0")
//    implementation("com.stripe:stripe-android:17.2.0")
//    implementation("com.android.volley:volley:1.1.0")

    implementation("com.stripe:stripe-android:20.5.0")
    //implementation("com.stripe:stripe-paymentsheet:20.5.0")
    implementation("com.squareup.okhttp3:okhttp:4.9.1")
    testImplementation("org.mockito:mockito-core:3.6.0")
    testImplementation("junit:junit:4.13.1")
    testImplementation("org.robolectric:robolectric:4.11.1")


}

