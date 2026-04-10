plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    java
}

group = "br.com.codexia"
version = "1.0.0-SNAPSHOT"

// BLOCO 'java' REMOVIDO. O Root project gerencia a versão da linguagem agora.

repositories {
    mavenCentral()
}

dependencies {
    // 1. Shared Kernel (OBRIGATÓRIO)
    implementation(project(":codexia-shared"))

    // 2. Dependências de Infraestrutura (Restritas ao pacote 'infrastructure/')
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-jdbc")
    runtimeOnly("org.postgresql:postgresql")

    // 3. Testes e Blindagem Arquitetural
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("com.tngtech.archunit:archunit-junit5:1.2.1")
}

tasks.withType<Test> {
    useJUnitPlatform()
}