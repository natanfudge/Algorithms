import org.jetbrains.compose.compose
import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "com.example"
version = "1.0-SNAPSHOT"

val composeVersion = project.property("compose.version")

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}
dependencies {
}

kotlin {
    jvm {
        compilations.all {
            kotlinOptions.jvmTarget = "17"
            kotlinOptions.freeCompilerArgs += "-Xcontext-receivers"
        }
        withJava()
    }

    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation("org.jetbrains.compose.ui:ui-graphics-desktop:$composeVersion")
                implementation("org.jetbrains.compose.ui:ui-geometry-desktop:$composeVersion")
                implementation("org.jetbrains.compose.foundation:foundation-desktop:$composeVersion")
                compileOnly("org.jetbrains:annotations:23.0.0")
            }
        }
        val jvmTest by getting {
            dependencies {
                implementation("junit:junit:4.13.2")
                implementation("io.strikt:strikt-core:0.34.0")
            }
        }
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "AlgorithmsGui"
            packageVersion = "1.0.0"
        }
    }
}
