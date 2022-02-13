plugins {
	java
	`maven-publish`
	id("fabric-loom") version "0.11-SNAPSHOT"
	id("org.cadixdev.licenser") version "0.6.1"
}

val archivesBaseName: String by project
val minecraftVersion: String by project
val modVersion: String by project
val mavenGroup: String by project
val yarnMappings: String by project
val loaderVersion: String by project
val fabricVersion: String by project
val modmenuVersion: String by project
val clothConfigVersion: String by project
val sodiumVersion: String by project
val irisVersion: String by project

group = mavenGroup
version = modVersion

repositories {
	mavenCentral()
	maven("https://maven.shedaniel.me/")
	maven("http://repo.denaryworld.ru/snapshots") {
		isAllowInsecureProtocol = true
	}
	maven("https://api.modrinth.com/maven") {
		content {
			includeGroup("maven.modrinth")
		}
	}
}

dependencies {
	minecraft("com.mojang:minecraft:$minecraftVersion")
	mappings("net.fabricmc:yarn:$yarnMappings:v2")
	modImplementation("net.fabricmc:fabric-loader:$loaderVersion")
	modImplementation("net.fabricmc.fabric-api:fabric-api:$fabricVersion")

	modImplementation("com.terraformersmc:modmenu:$modmenuVersion")
	modImplementation("maven.modrinth:sodium:$sodiumVersion")
	modImplementation("maven.modrinth:iris:$irisVersion")
	modApi("me.shedaniel.cloth:cloth-config-fabric:$clothConfigVersion") {
		exclude(group = "net.fabricmc.fabric-api")
	}

	compileOnly("org.projectlombok:lombok:1.18.22")
	annotationProcessor("org.projectlombok:lombok:1.18.22")
}

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(17))
	}
	withSourcesJar()
}

license {
	include("**/io/sapphiremc/chromium/**")

	header(project.file("HEADER"))
	newLine(false)
}

publishing {
	publications.create<MavenPublication>("maven") {
		from(components["java"])
	}

	repositories {
		maven {
			name = "SapphireMC"
			url = uri("http://repo.denaryworld.ru/snapshots")
			isAllowInsecureProtocol = true
			credentials(PasswordCredentials::class)
		}
	}
}

tasks {
	withType<JavaCompile> {
		options.encoding = Charsets.UTF_8.name()
		options.release.set(17)
	}

	withType<Javadoc> {
		options.encoding = Charsets.UTF_8.name()
	}

	withType<ProcessResources> {
		filteringCharset = Charsets.UTF_8.name()
	}

	withType<Jar> {
		archiveBaseName.set(archivesBaseName)
	}

	processResources {
		inputs.property("version", project.version)

		filesMatching("fabric.mod.json") {
			expand("version" to project.version)
		}
	}

	jar {
		from("LICENSE") {
			rename { "${it}_$archivesBaseName\\_$minecraftVersion" }
		}
	}

	remapJar {
		archiveBaseName.set(archivesBaseName)
	}
}