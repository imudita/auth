plugins {
    id("org.springframework.boot") version "3.2.1"
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}

repositories {
    mavenCentral()
}

dependencies {

    // === Spring Boot Core ===
    implementation("org.springframework.boot:spring-boot-starter-web")

    // === Validation (@Valid, @NotBlank) ===
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // === JPA / Hibernate ===
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")

    // === Spring Security ===
    implementation("org.springframework.boot:spring-boot-starter-security")

    // === H2 Database ===
    runtimeOnly("com.h2database:h2")

    // === Kotlin Support ===
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")

    // === Test ===
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
