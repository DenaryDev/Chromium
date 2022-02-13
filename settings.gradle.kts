pluginManagement {
    repositories {
        maven("https://maven.fabricmc.net/")
        gradlePluginPortal()
    }
}

if (JavaVersion.current().ordinal + 1 < 17) {
    println(JavaVersion.current().ordinal + 1)
    throw IllegalStateException("Please run gradle with Java 17+!")
}

rootProject.name = "chromium-fabric"
