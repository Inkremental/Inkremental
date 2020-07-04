pluginManagement {
    repositories {
        mavenLocal()
        maven(url = "https://dl.bintray.com/inkremental/maven")
        gradlePluginPortal()
        google()
        jcenter()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "com.android") {
                useModule("com.android.tools.build:gradle:${requested.version}")
            }
        }
    }
}

rootProject.name = "inkremental-root"

includeBuild("meta")

include(
    ":core",
    ":anvil",
    ":anvil-support-v4",
    ":anvil-appcompat-v7",
    ":anvil-cardview-v7",
    ":anvil-gridlayout-v7",
    ":anvil-design",
    ":anvil-recyclerview-v7",
    ":anvil-constraintlayout",
    ":anvil-yogalayout"
)

// Include projects from `samples` directory.
// `samples/android-showcase` is available as `:sample-android-showcase`.
file("samples").listFiles()?.forEach { dir ->
    val projectName = "sample-${dir.name}"
    include(projectName)
    project(":$projectName").projectDir = dir
}
