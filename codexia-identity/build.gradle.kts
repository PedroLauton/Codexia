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

    // Web + JPA
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    // Spring Authorization Server (já inclui Spring Security)
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server")

    // OAuth2 Client (para integrar Google como provider externo)
    implementation("org.springframework.boot:spring-boot-starter-oauth2-client")

    // DB
    runtimeOnly("org.postgresql:postgresql")

    // Lombok
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    // Testes
    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-testcontainers")
    testImplementation("org.testcontainers:junit-jupiter:1.21.3")
    testImplementation("org.testcontainers:postgresql:1.21.3")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.4.1")
}

configurations.testRuntimeClasspath {
    resolutionStrategy.force(
        "org.junit.platform:junit-platform-launcher:1.12.2",
        "org.junit.platform:junit-platform-engine:1.12.2",
        "org.junit.platform:junit-platform-commons:1.12.2",
        "org.junit.jupiter:junit-jupiter:5.12.2",
        "org.junit.jupiter:junit-jupiter-api:5.12.2",
        "org.junit.jupiter:junit-jupiter-engine:5.12.2",
        "org.junit.jupiter:junit-jupiter-params:5.12.2"
    )
}

tasks.withType<Test> {
    useJUnitPlatform()
    jvmArgs(
        "--add-opens", "java.base/java.lang=ALL-UNNAMED",
        "--add-opens", "java.base/java.util=ALL-UNNAMED",
        "--add-opens", "java.base/java.lang.reflect=ALL-UNNAMED"
    )
}