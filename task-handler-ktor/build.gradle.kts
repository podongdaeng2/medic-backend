plugins {
	application
	kotlin("jvm")
	id("io.ktor.plugin") version "2.3.9"
}

application {
	mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
	mavenCentral()
	maven { url = uri("https://maven.pkg.jetbrains.space/public/p/ktor/eap") }
}

val exposedVersion = "0.49.0"
val ktorVersion = "2.3.9"

dependencies {
	implementation(project(":podong-exposed"))
	implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")

	// for OpenAI
	runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.0")
	runtimeOnly("org.jetbrains.kotlinx:kotlinx-coroutines-reactor:1.8.0")
//	implementation("org.springframework.boot:spring-boot-starter-webflux:3.2.4")
	implementation("io.ktor:ktor-client-apache5:2.3.9")
	implementation ("com.aallam.openai:openai-client:3.7.0")

	// .env easier get
	implementation("io.github.cdimascio:dotenv-kotlin:6.4.1")

	implementation("io.ktor:ktor-server-core-jvm")
	implementation("io.ktor:ktor-server-netty-jvm")
	implementation("io.ktor:ktor-server-config-yaml:$ktorVersion")
//	testImplementation("ch.qos.logback:logback-classic:1.5.3")
	testImplementation("io.ktor:ktor-server-tests-jvm")
}
