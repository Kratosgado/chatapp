buildscript {
    repositories {
        mavenCentral()
    }
    dependencies {
        classpath("org.liquibase:liquibase-core:4.31.1")
        classpath("org.liquibase:liquibase-gradle-plugin:3.1.0")
    }
}

plugins {
    id("org.springframework.boot") version "3.4.1" // Use a stable version
    id("io.spring.dependency-management") version "1.1.4"
    kotlin("jvm") version "1.9.22"
    kotlin("plugin.spring") version "1.9.22"
    kotlin("plugin.jpa") version "1.9.22"
    id("org.liquibase.gradle") version "3.1.0" // Latest stable for Gradle 8+
}

group = "com.kratosgado"
version = "0.0.1-SNAPSHOT"
val liquibaseVersion = "4.31.1" // Use a consistent version

java {
    sourceCompatibility = JavaVersion.VERSION_21
}

repositories {
    mavenCentral()
}

dependencies {
    // Core Starters
    implementation("org.springframework.boot:spring-boot-starter-aop")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")

    // Kotlin & JSON
    implementation("net.datafaker:datafaker:2.1.0")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    developmentOnly("org.springframework.boot:spring-boot-devtools")

    // JJWT (Updated to 0.12.6 for better Kotlin support)
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    // Database
    // implementation("org.liquibase:liquibase-core")
    implementation("org.liquibase:liquibase-core:$liquibaseVersion")
    runtimeOnly("org.postgresql:postgresql")

    // Liquibase Gradle Plugin runtime dependencies
    // liquibaseRuntime("org.liquibase:liquibase-core:4.27.0")
    // liquibaseRuntime("org.liquibase.ext:liquibase-hibernate6:4.27.0")
    liquibaseRuntime("org.liquibase:liquibase-core:$liquibaseVersion")
    liquibaseRuntime("org.liquibase.ext:liquibase-hibernate6:$liquibaseVersion")
    liquibaseRuntime("org.springframework.boot:spring-boot-starter-data-jpa")
    liquibaseRuntime("org.postgresql:postgresql")
    liquibaseRuntime("info.picocli:picocli:4.7.5")
    liquibaseRuntime(sourceSets.main.get().output)

    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")

    // Add MockK for idiomatic Kotlin mocking
    testImplementation("io.mockk:mockk:1.13.10")
}

liquibase {
    activities.register("main") {
        arguments =
            mapOf(
                "changelogFile" to "src/main/resources/db/changelog/db.changelog-master.xml",
                "url" to "jdbc:postgresql://localhost:5432/chatapp",
                "username" to "postgres",
                "password" to "postgres",
                "referenceUrl" to
                    "hibernate:spring:com.kratosgado.chatapp?dialect=org.hibernate.dialect.PostgreSQLDialect&hibernate.physical_naming_strategy=org.hibernate.boot.model.naming.CamelCaseToUnderscoresNamingStrategy",
            )
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs += "-Xjsr305=strict"
        jvmTarget = "21"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
