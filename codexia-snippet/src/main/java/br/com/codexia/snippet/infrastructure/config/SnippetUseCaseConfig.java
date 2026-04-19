package br.com.codexia.snippet.infrastructure.config;

import br.com.codexia.snippet.application.ports.input.CreateSnippetUseCase;
import br.com.codexia.snippet.application.ports.output.CategoryRepositoryPort;
import br.com.codexia.snippet.application.ports.output.SnippetRepositoryPort;
import br.com.codexia.snippet.application.ports.output.TagRepositoryPort;
import br.com.codexia.snippet.application.usecase.CreateSnippetUseCaseImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnippetUseCaseConfig {

    @Bean
    public CreateSnippetUseCase createSnippetUseCase(SnippetRepositoryPort snippetRepositoryPort,  CategoryRepositoryPort categoryRepositoryPort,  TagRepositoryPort tagRepositoryPort) {
        return new CreateSnippetUseCaseImpl(snippetRepositoryPort, categoryRepositoryPort, tagRepositoryPort);
    }
}
