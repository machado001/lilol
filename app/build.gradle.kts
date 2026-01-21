import com.google.protobuf.gradle.id
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    alias(libs.plugins.kotlinSerialization)
    id("com.google.gms.google-services")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.protobuf") version "0.9.4"
    id("com.google.firebase.crashlytics")
    id("org.jetbrains.kotlin.plugin.parcelize")
}

val localPropsFile = Properties().apply {
    val f = rootProject.file("local.properties")
    if (f.exists()) f.inputStream().use { load(it) }
}

fun propOrNull(key: String): String? = localPropsFile.getProperty(key)

fun String.configureOrThrow() =
    propOrNull(this) ?: throw GradleException("$this not found in local.properties.")


android {
    namespace = "com.machado001.lilol"
    compileSdk = libs.versions.compileSdk.get().toInt()

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    defaultConfig {
        applicationId = "com.machado001.lilol"
        minSdk = libs.versions.minSdk.get().toInt()
        targetSdk = libs.versions.compileSdk.get().toInt()
        versionCode = 16
        versionName = "1.0.7"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        debug {
            //public ad unit id for test
            resValue("string", "ad_unit_banner_all_champions", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "ad_unit_banner_rotation", "ca-app-pub-3940256099942544/6300978111")
            resValue("string", "ad_unit_banner_champion_details", "ca-app-pub-3940256099942544/6300978111")
            buildConfigField("String", "ADMOB_INTERSTITIAL_AD_UNIT_ID",  "\"ca-app-pub-3940256099942544/1033173712\"")
        }
        release {
            val all = "ADMOB_BANNER_ALL".configureOrThrow()
            val rotation = "ADMOB_BANNER_ROTATION".configureOrThrow()
            val details = "ADMOB_BANNER_DETAILS".configureOrThrow()
            val interstitial = "ADMOB_INTERSTITIAL".configureOrThrow()

            isMinifyEnabled = true
            isShrinkResources = true
            resValue("string", "ad_unit_banner_all_champions", all)
            resValue("string", "ad_unit_banner_rotation", rotation)
            resValue("string", "ad_unit_banner_champion_details", details)
            buildConfigField("String", "ADMOB_INTERSTITIAL_AD_UNIT_ID", interstitial)
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

kotlin {
    compilerOptions {
        jvmTarget.set(JvmTarget.JVM_17)
        freeCompilerArgs = listOf("-XXLanguage:+PropertyParamAnnotationDefaultTargetMode")
    }
}

dependencies {
    implementation(projects.google.inAppReview)

    implementation(libs.androidx.swiperefreshlayout)
    implementation(libs.picasso)
    implementation(libs.logging.interceptor)
    implementation(libs.retrofit.kotlinx.serialization.converter)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.retrofit)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.preference.ktx)
    implementation(libs.androidx.startup.runtime)
    implementation(libs.androidx.datastore)
    implementation(libs.protobuf.kotlin.lite)
    implementation(libs.protobuf.javalite)

    // Kotlin
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    implementation(libs.kotlinx.coroutines.core)
    // Feature module Support
    implementation(libs.androidx.navigation.dynamic.features.fragment)
    implementation(libs.androidx.junit.ktx)
    implementation(libs.androidx.uiautomator)
    implementation(libs.google.firebase.appcheck.debug)
    testImplementation(libs.junit.jupiter)

    // Testing Navigation
    androidTestImplementation(libs.androidx.navigation.testing)
    testImplementation(libs.junit)

    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    //Firebase
    implementation(platform(libs.firebase.bom))
    implementation(libs.firebase.analytics)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.functions)
    implementation(libs.firebase.messaging)
    implementation(libs.firebase.appcheck.playintegrity)
    debugImplementation(libs.firebase.appcheck.debug)
    debugImplementation(libs.leakcanary.android)

    // Google Mobile Ads
    implementation(libs.play.services.ads)
    implementation(libs.logcat)

}


protobuf {
    protoc {
        artifact = "com.google.protobuf:protoc:3.24.1"
    }
    generateProtoTasks {
        all().forEach { task ->
            task.builtins {
                id("java") {
                    option("lite")
                }
                id("kotlin") {
                    option("lite")
                }
            }
        }
    }
}
