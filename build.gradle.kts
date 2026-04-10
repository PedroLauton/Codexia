plugins {
    id("org.springframework.boot") version "4.0.5" apply false
    id("io.spring.dependency-management") version "1.1.7" apply false
}

allprojects {
    group = "br.com.codexia"
    version = "1.0.0-SNAPSHOT"

    repositories {
        mavenCentral()
    }
}

// Configuração centralizada apenas para os submódulos
subprojects {
    // Escuta: Se o submódulo aplicar o plugin "java", injete esta configuração nele
    plugins.withType<JavaPlugin> {
        extensions.configure<JavaPluginExtension> {
            toolchain {
                languageVersion = JavaLanguageVersion.of(25)
            }
        }
    }
}