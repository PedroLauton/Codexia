package br.com.codexia.snippet.infrastructure.config;

import br.com.codexia.snippet.application.ports.input.CreateSnippetUseCase;
import br.com.codexia.snippet.application.ports.output.command.CategoryCommandPort;
import br.com.codexia.snippet.application.ports.output.command.SnippetCommandPort;
import br.com.codexia.snippet.application.ports.output.command.TagCommandPort;
import br.com.codexia.snippet.application.ports.output.query.CategoryQueryPort;
import br.com.codexia.snippet.application.ports.output.query.TagQueryPort;
import br.com.codexia.snippet.application.usecase.CreateSnippetUseCaseImpl;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SnippetUseCaseConfig {

    @Bean
    public CreateSnippetUseCase createSnippetUseCase(SnippetCommandPort snippetCommandPort, CategoryQueryPort categoryQueryPort, TagQueryPort tagQueryPort) {
        return new CreateSnippetUseCaseImpl(snippetCommandPort, categoryQueryPort, tagQueryPort);
    }
}
