plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

group = "br.com.codexia"
version = "1.0.0-SNAPSHOT"


repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":codexia-shared"))

    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    runtimeOnly("org.postgresql:postgresql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.2.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}