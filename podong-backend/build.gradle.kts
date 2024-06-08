import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version "3.2.4"
	id("io.spring.dependency-management") version "1.1.4"
	id("java")
	kotlin("jvm") version "1.9.23"
	kotlin("plugin.spring") version "1.9.23"
	kotlin("plugin.jpa") version "1.9.23"
}

tasks.jar {
	manifest {
		attributes["Main-Class"] = "podongdaeng2.PodongApplicationKt"
	}
	duplicatesStrategy = DuplicatesStrategy.EXCLUDE
	from(configurations.runtimeClasspath.get().map { if (it.isDirectory) it else zipTree(it) })
}

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	implementation(project(":podong-exposed"))

	// spring
	implementation("org.springframework.boot:spring-boot-starter-web:3.2.4")

	// exposed
	implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.49.0")

	// for OpenAI
	runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
	runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.0")
	implementation("org.springframework.boot:spring-boot-starter-webflux:3.2.4")
	implementation("io.ktor:ktor-client-apache5:2.3.9")
	implementation("com.aallam.openai:openai-client:3.7.0")

	// .env easier get
	implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

	testImplementation("junit:junit:4.13.2")
	testImplementation("org.springframework.boot:spring-boot-starter-test:3.2.4")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}
