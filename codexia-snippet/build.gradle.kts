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
    compileOnly("org.projectlombok:lombok")
    annotationProcessor("org.projectlombok:lombok")

    testCompileOnly("org.projectlombok:lombok")
    testAnnotationProcessor("org.projectlombok:lombok")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

// Gradle 9.4.1 was built against JUnit Platform 5.x; JP 6.0.3 (from Spring Boot 4)
// breaks its internal Launcher API. Force JP 5.12.2 for the test classpath.
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