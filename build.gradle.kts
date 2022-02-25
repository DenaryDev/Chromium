plugins {
	java
	`maven-publish`
	id("fabric-loom") version "0.11-SNAPSHOT"
	id("org.cadixdev.licenser") version "0.6.1"
}

val archivesBaseName: String by project
val minecraftVersion: String by project
val yarnMappings: String by project
val loaderVersion: String by project
val fabricVersion: String by project
val modmenuVersion: String by project
val clothConfigVersion: String by project
val sodiumCompatibility: Boolean = project.properties["sodiumCompatibility"].toString().toBoolean()
val sodiumVersion: String by project
val irisCompatibility: Boolean = project.properties["irisCompatibility"].toString().toBoolean()
val irisVersion: String by project

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

	if (sodiumCompatibility) {
		modImplementation("maven.modrinth:sodium:$sodiumVersion")
		implementation("org.joml:joml:1.10.2")
	}
	if (irisCompatibility) {
		modImplementation("maven.modrinth:iris:$irisVersion")
	}
	modImplementation("com.terraformersmc:modmenu:$modmenuVersion")
	modImplementation("me.shedaniel.cloth:cloth-config-fabric:$clothConfigVersion") {
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

sourceSets {
	if (sodiumCompatibility) {
		create("sodiumCompatibility") {
			java {
				compileClasspath += main.get().compileClasspath
				compileClasspath += main.get().output
			}
		}
	}
	if (irisCompatibility) {
		create("irisCompatibility") {
			java {
				compileClasspath += main.get().compileClasspath
				compileClasspath += main.get().output
			}
		}
	}

	main {
		java {
			if (sodiumCompatibility) {
				runtimeClasspath += getByName("sodiumCompatibility").output
			}
			if (irisCompatibility) {
				runtimeClasspath += getByName("irisCompatibility").output
			}
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

			if (!sodiumCompatibility) {
				filter {
					it.replace("mixins.chromium.compat.sodium.json", "mixins.empty.sodium.json")
				}
			}
			if (!irisCompatibility) {
				filter {
					it.replace("mixins.chromium.compat.iris.json", "mixins.empty.iris.json")
				}
			}
		}

		if (sodiumCompatibility) {
			exclude("mixins.empty.sodium.json")
		}
		if (irisCompatibility) {
			exclude("mixins.empty.iris.json")
		}
	}

	jar {
		from("LICENSE") {
			rename { "${it}_$archivesBaseName\\_$minecraftVersion" }
		}

		if (sodiumCompatibility) {
			from(sourceSets["sodiumCompatibility"].output) {
				filesMatching("*refmap.json") {
					name = "chromium-sodium-compat-refmap.json"
				}
			}
		}
		if (irisCompatibility) {
			from(sourceSets["irisCompatibility"].output) {
				filesMatching("*refmap.json") {
					name = "chromium-iris-compat-refmap.json"
				}
			}
		}
	}

	remapJar {
		archiveBaseName.set(archivesBaseName)
	}

	runClient {
		jvmArgs?.add("-Dmixin.debug.export=true")
	}
}