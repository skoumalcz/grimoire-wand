package com.skoumal.grimoire.build

import com.skoumal.grimoire.cover.android.AndroidConfigurationOptions
import com.skoumal.grimoire.cover.android.AndroidLibraryFactory
import com.skoumal.grimoire.cover.gradle.parentExtra
import org.gradle.api.JavaVersion
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.provideDelegate

class LibraryPlugin : Plugin<Project> {

    override fun apply(target: Project) {
        val kotlinVersion: String by target.parentExtra
        val coroutinesVersion: String by target.parentExtra

        val options = AndroidLibraryFactory(target)
            .applyAndroid()
            .applyKotlin()
            .applyKotlinDependencies(kotlinVersion)
            .applyCoroutineDependencies(coroutinesVersion)
            .applyTestDependencies()
            .applyAndroidTestDependencies()
            .applyCoroutinesTestDependencies(coroutinesVersion)
            .build()

        options
            .setTargetSdk(30)
            .setMinSdk(21)
            .applyJavaVersion(JavaVersion.VERSION_1_8)
    }

}

fun <Config : AndroidConfigurationOptions> Config.applyJavaVersion(version: JavaVersion) = apply {
    compileOptions {
        sourceCompatibility = version
        targetCompatibility = version
    }
    kotlinOptions {
        jvmTarget = version.toString()
    }
}