plugins {
	java
	`maven-publish`
	id("fabric-loom") version "0.10-SNAPSHOT"
}

val archivesName: String by project
val minecraftVersion: String by project
val modVersion: String by project
val mavenGroup: String by project
val yarnMappings: String by project
val loaderVersion: String by project
val fabricVersion: String by project
val modmenuVersion: String by project

group = mavenGroup
version = modVersion

repositories {
	mavenCentral()
	maven("http://repo.denaryworld.ru/snapshots") {
		isAllowInsecureProtocol = true
	}
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraftVersion")
	mappings("net.fabricmc:yarn:$yarnMappings:v2")
	modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
	modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")
	modImplementation("com.terraformersmc:modmenu:$modmenuVersion")

	compileOnly("net.fabricmc:fabric-loader:$loaderVersion")
}

tasks.withType<JavaCompile> {
	options.encoding = Charsets.UTF_8.name()
	options.release.set(17)
}

tasks.withType<Javadoc> {
	options.encoding = Charsets.UTF_8.name()
}

tasks.withType<ProcessResources> {
	filteringCharset = Charsets.UTF_8.name()
}

tasks.withType<Jar> {
	archiveBaseName.set(archivesName)
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
	withSourcesJar()
}

tasks.processResources {
	inputs.property("version", project.version)

	filesMatching("fabric.mod.json") {
		expand("version" to project.version)
	}
}

tasks.jar {
	from("LICENSE") {
		rename { "${it}_$archivesName\\_$minecraftVersion" }
	}
}

tasks.remapJar {
	archiveBaseName.set(archivesName)
}

publishing {
	publications.create<MavenPublication>("maven") {
		artifactId = "client"

		from(components["java"])
	}

	repositories {
		maven {
			name = "sapphireMC"
			url = uri("http://repo.denaryworld.ru/snapshots")
			isAllowInsecureProtocol = true
			credentials(PasswordCredentials::class)
		}
	}
}
