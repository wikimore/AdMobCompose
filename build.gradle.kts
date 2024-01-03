import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.com.android.application) apply false
    alias(libs.plugins.com.android.library) apply false
    alias(libs.plugins.org.jetbrains.kotlin.android) apply false
    alias(libs.plugins.com.vanniktech.maven.publish) apply false
    alias(libs.plugins.com.google.gms.google.services) apply false
}
true // Needed to make the Suppress annotation work for the plugins block