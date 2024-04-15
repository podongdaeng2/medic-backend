plugins {
    application
    kotlin("jvm")
}


repositories {
    mavenCentral()
}

val exposedVersion = "0.49.0"

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("mysql:mysql-connector-java:8.0.33")
    implementation("org.jetbrains.exposed:exposed-core:$exposedVersion")
    implementation("org.jetbrains.exposed:exposed-spring-boot-starter:$exposedVersion")
    implementation ("org.jetbrains.exposed:exposed-java-time:$exposedVersion")
}
