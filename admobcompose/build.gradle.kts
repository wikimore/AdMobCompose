import com.vanniktech.maven.publish.AndroidSingleVariantLibrary
import com.vanniktech.maven.publish.SonatypeHost

plugins {
    alias(libs.plugins.com.android.library)
    alias(libs.plugins.com.vanniktech.maven.publish)
    alias(libs.plugins.org.jetbrains.kotlin.android)

}

android {
    namespace = "io.github.wikimore.admobcompose"
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
            consumerProguardFiles("consumer-rules.pro")
        }
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }

}

dependencies {
    implementation(platform(libs.compose.bom))
    implementation(libs.core.ktx)
    implementation(libs.activity.compose)
    implementation(libs.appcompat)
    implementation(libs.material3)
    implementation(libs.timber)
    implementation(libs.coil.compose)
    implementation(libs.accompanist.drawablepainter)
    implementation(libs.lifecycle.runtime.ktx)
    implementation(libs.play.services.ads)
    implementation(libs.ui)
    implementation(libs.ui.tooling.preview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

mavenPublishing {
    coordinates("io.github.wikimore", "admob-compose", "0.0.4")
    pom {
        name.set("AdMobCompose")
        description.set("A library for using AdMob in android jetpack compose.")
        inceptionYear.set("2024")
        url.set("https://github.com/wikimore/AdMobCompose/")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("wikimore")
                name.set("ted")
                url.set("https://github.com/wikimore")
                email.set("wikimore357@gmail.com")
                roles.add("Developer")
                timezone.set("+8")
            }
        }
        scm {
            url.set("https://github.com/wikimore/AdMobCompose/")
            connection.set("scm:git:git://github.com/wikimore/AdMobCompose.git")
            developerConnection.set("scm:git:ssh://git@github.com/wikimore/AdMobCompose.git")
        }
    }


    configure(
        AndroidSingleVariantLibrary(
            // the published variant
            variant = "release",
            // whether to publish a sources jar
            sourcesJar = true,
            // whether to publish a javadoc jar
            publishJavadocJar = true,
        )
    )
    signAllPublications()
    publishToMavenCentral(SonatypeHost.S01, true)
}
