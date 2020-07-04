plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.multiplatform")
}

kotlin {
    android()
    jvm("testJvm") // TODO disable publication!

    sourceSets {
        commonMain {
            dependencies {
                api(kotlin("stdlib-common"))
            }
        }
        commonTest {
            dependencies {
                implementation(kotlin("test-common"))
                implementation(kotlin("test-annotations-common"))
            }
        }

        named("androidMain") {
            dependencies {
                api(kotlin("stdlib-jdk7"))
                api("androidx.annotation:annotation:1.1.0")
            }
        }
        named("androidTest") {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        named("testJvmMain") {
            dependencies {
                api(kotlin("reflect"))
            }
        }
        named("testJvmTest") {
            dependencies {
                implementation(kotlin("test-junit"))
            }
        }

        all {
            with(languageSettings) {
                useExperimentalAnnotation("kotlin.RequiresOptIn")
            }

            if(name.endsWith("Test")) {
                with(languageSettings) {
                    useExperimentalAnnotation("kotlin.time.ExperimentalTime")
                }
            }
        }
    }
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(17)
        targetSdkVersion(29)
    }
    sourceSets.all {
        setRoot("src/android${name.capitalize()}")
        //java.srcDirs.add(file("$path/kotlin"))
    }
}
