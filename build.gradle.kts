import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("io.spring.dependency-management") version "1.1.4"
	kotlin("jvm") version "1.9.23"
	kotlin("plugin.spring") version "1.9.23"
	kotlin("plugin.jpa") version "1.9.23"
}

group = "podongdaeng2"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_17
}

repositories {
	mavenCentral()
}

dependencies {
	// base-setups / spring
	implementation("org.springframework.boot:spring-boot-starter-web:3.2.4")
	implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.2.4")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	implementation("mysql:mysql-connector-java:8.0.33")
	implementation("org.jetbrains.exposed:exposed-spring-boot-starter:0.49.0")

	// for OpenAI - some might seem not, but it's dependency. how miserable.
	runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
	runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.0")
	implementation("org.springframework.boot:spring-boot-starter-webflux:3.2.4")
	implementation("io.ktor:ktor-client-apache5:2.3.9")
	implementation ("com.aallam.openai:openai-client:3.7.0")

	// .env easier get
	implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "17"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}
