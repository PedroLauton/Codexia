plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "br.com.codexia"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":codexia-shared"))
    implementation(project(":codexia-snippet"))
    implementation(project(":codexia-identity"))
    implementation(project(":codexia-workspace"))
    implementation(project(":codexia-ai"))
    implementation(project(":codexia-notification"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    runtimeOnly("org.postgresql:postgresql")
}

tasks.test {
    useJUnitPlatform()
}