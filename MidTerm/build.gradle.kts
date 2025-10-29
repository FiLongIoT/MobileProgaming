buildscript {
    dependencies {
        classpath ("com.android.tools.build:gradle:8.12.3")
        classpath("com.google.gms:google-services:4.4.1")
    }
}
// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.12.3" apply false
    id("org.jetbrains.kotlin.android") version "2.1.0" apply false  // ← Cập nhật lên 2.1.0
    id("com.google.gms.google-services") version "4.4.0" apply false

}