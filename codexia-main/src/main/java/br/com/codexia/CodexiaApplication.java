package br.com.codexia;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = "br.com.codexia")
public class CodexiaApplication {
    static void main(String[] args) {
        SpringApplication.run(CodexiaApplication.class, args);
    }
}