plugins {
	id("fabric-loom") version "1.3-SNAPSHOT"
	`maven-publish`
  	kotlin("jvm") version "1.9.0"
	java
}

group = property("maven_group")!!
version = property("mod_version")!!

base {
	archivesName.set(property("archives_base_name")!!.toString())
}

loom {
    splitEnvironmentSourceSets()

	mods {
		this.create("foldout-map") {
			sourceSet(sourceSets["main"])
			sourceSet(sourceSets["client"])
		}
	}

}

dependencies {
	minecraft("com.mojang:minecraft:${property("minecraft_version")!!}")
	mappings("net.fabricmc:yarn:${property("yarn_mappings")}:v2")
	modImplementation("net.fabricmc:fabric-loader:${property("loader_version")}")

	// Fabric API
	modImplementation("net.fabricmc.fabric-api:fabric-api:${property("fabric_version")}")
	modImplementation("net.fabricmc:fabric-language-kotlin:${property("fabric_kotlin_version")}")
}

tasks {
	processResources {
		inputs.property("version", project.version)

		filesMatching("fabric.mod.json") {
			expand("version" to project.version)
		}
	}

	withType(JavaCompile::class.java).configureEach {
		options.release = 17
	}

	compileKotlin {
		kotlinOptions {
			jvmTarget = "17"
			freeCompilerArgs += "-Xexplicit-api=strict"
		}
	}

	java {
		withSourcesJar()

		sourceCompatibility = JavaVersion.VERSION_17
		targetCompatibility = JavaVersion.VERSION_17
	}

	jar {
		from("LICENSE") {
			rename { "${it}_${project.base.archivesName.get()}"}
		}
	}
}

publishing {
	publications {
		create<MavenPublication>("mavenJava") {
			from(components["java"])
		}
	}

	repositories {
		// Add repositories to publish to here
	}
}
